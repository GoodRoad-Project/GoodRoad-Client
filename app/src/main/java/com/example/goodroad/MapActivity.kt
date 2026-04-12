package com.example.goodroad

import android.location.Address
import org.maplibre.android.camera.CameraUpdateFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.goodroad.features.location.LocationTracker
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import android.location.Geocoder
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import com.example.goodroad.model.RouteRequest
import com.example.goodroad.model.RouteResponse
import com.example.goodroad.features.network.api.GoodRoadApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.goodroad.features.network.utils.decodePoints
import com.example.goodroad.model.PathResponse
import org.maplibre.android.geometry.LatLng
import com.google.maps.android.PolyUtil
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.layers.PropertyFactory

class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var locationTracker: LocationTracker
    private lateinit var addressEditText: EditText
    private lateinit var setDestinationButton: Button

    private var startLat: Double = 0.0
    private var startLon: Double = 0.0

    private val api: GoodRoadApi by lazy {
        Retrofit.Builder()
            //.baseUrl("http://10.0.2.2:8080/")  // для эмулятора
            .baseUrl("http://10.0.0.2:8080/")  // для моего телефона
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoodRoadApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapLibre.getInstance(this)
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        addressEditText = findViewById(R.id.addressEditText)
        setDestinationButton = findViewById(R.id.setDestinationButton)

        locationTracker = LocationTracker(this)

        mapView.getMapAsync { map ->
            map.setStyle(Style.Builder().fromUri("https://tiles.stadiamaps.com/styles/alidade_smooth.json?api_key=a5972731-a9e9-4ebb-943b-2965bc3f9dca")) {
                getUserLocation()
            }
        }

        setDestinationButton.setOnClickListener {
            val destinationAddress = addressEditText.text.toString()
            if (destinationAddress.isNotBlank()) {
                getCoordinatesFromAddress(destinationAddress)
            } else {
                Toast.makeText(this, "Введите адрес назначения", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserLocation() {
        lifecycleScope.launch {
            val location = locationTracker.getCurrentLocation()
            if (location != null) {
                startLat = location.latitude
                startLon = location.longitude
                Toast.makeText(
                    this@MapActivity,
                    "Ваше местоположение: $startLat, $startLon",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this@MapActivity, "Не удалось определить местоположение", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCoordinatesFromAddress(address: String) {
        lifecycleScope.launch {
            Toast.makeText(this@MapActivity, "Поиск адреса...", Toast.LENGTH_SHORT).show()

            val addresses = withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(this@MapActivity, Locale.getDefault())
                    geocoder.getFromLocationName(address, 1)
                } catch (e: Exception) {
                    null
                }
            }

            if (!addresses.isNullOrEmpty()) {
                val destination = addresses[0]
                val endLat = destination.latitude
                val endLon = destination.longitude

                Toast.makeText(
                    this@MapActivity,
                    "Маршрут от \$startLat,\$startLon до \$endLat,\$endLon",
                    Toast.LENGTH_LONG
                ).show()

                buildRoute(endLat, endLon)
            } else {
                Toast.makeText(this@MapActivity, "Адрес не найден. Попробуйте точнее.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buildRoute(endLat: Double, endLon: Double) {
        lifecycleScope.launch {
            //взять реальный Id пользователя
            if (startLat == 0.0 || startLon == 0.0) {
                Toast.makeText(this@MapActivity, "Стартовая точка не определена", Toast.LENGTH_SHORT).show()
                return@launch
            }

            drawRoute(RouteResponse(id = "test", paths = emptyList()))

            val request = RouteRequest(
                start = "$startLat,$startLon",
                end = "$endLat,$endLon",
                userId = "" //userId //надо узнать откуда взять
            )

            try {
                val response = api.getRoute(request)
                drawRoute(response)
            } catch (e: Exception) {
                Toast.makeText(this@MapActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun drawRoute(response: RouteResponse) {
        val path = response.paths.firstOrNull() ?: return
        val points = decodePoints(path.points)

        mapView.getMapAsync { map ->
            map.getStyle { style ->
                style.removeLayer("route-layer")
                style.removeSource("route-source")

                val coordinates = points.joinToString(", ") {
                    "[${it.longitude}, ${it.latitude}]"
                }
                val geojson = """
                {
                    "type": "FeatureCollection",
                    "features": [{
                        "type": "Feature",
                        "geometry": {
                            "type": "LineString",
                            "coordinates": [$coordinates]
                        }
                    }]
                }
            """.trimIndent()

                val source = GeoJsonSource("route-source", geojson)
                style.addSource(source)

                val lineLayer = LineLayer("route-layer", "route-source").apply {
                    setProperties(
                        PropertyFactory.lineColor("#8B7AC6"),
                        PropertyFactory.lineWidth(5f),
                        PropertyFactory.lineOpacity(0.8f)
                    )
                }
                style.addLayer(lineLayer)

                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(startLat, startLon), 15.0),
                    1000
                )
            }
        }
    }

    override fun onStart() { super.onStart(); mapView.onStart() }
    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { super.onPause(); mapView.onPause() }
    override fun onStop() { super.onStop(); mapView.onStop() }
    override fun onDestroy() { super.onDestroy(); mapView.onDestroy() }
    override fun onLowMemory() { super.onLowMemory(); mapView.onLowMemory() }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}