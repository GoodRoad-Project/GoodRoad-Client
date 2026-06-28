package com.example.goodroad

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.network.GoodRoadApi
import com.example.goodroad.data.network.location.LocationTracker
import com.example.goodroad.data.network.route.RouteRequest
import com.example.goodroad.data.network.route.RouteObstaclePolicy
import com.example.goodroad.data.network.route.PathResponse
import com.example.goodroad.data.network.route.RouteResponse
import com.example.goodroad.data.network.utils.decodePoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import kotlin.collections.firstOrNull
import org.maplibre.android.style.layers.CircleLayer
import com.example.goodroad.domain.model.LocationPoint
import org.maplibre.android.style.sources.GeoJsonSource
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import com.example.goodroad.data.network.route.ObstacleResponse
import com.example.goodroad.ui.map.CoordinatesBottomSheet
import com.example.goodroad.ui.theme.GoodRoadTheme
import com.example.goodroad.data.place.PlaceInfoResponse
import com.example.goodroad.ui.map.PlaceInfoBottomSheet
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var locationTracker: LocationTracker
    private lateinit var addressEditText: EditText
    private lateinit var setDestinationButton: Button

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private var startLat: Double = 0.0
    private var startLon: Double = 0.0
    private var fastRoute: PathResponse? = null
    private var balancedRoute: PathResponse? = null
    private var safeRoute: PathResponse? = null

    private var showPlaceInfoBottomSheet by mutableStateOf(false)
    private var selectedPlaceInfo by mutableStateOf<PlaceInfoResponse?>(null)

    private val api: GoodRoadApi by lazy {
        ApiClient.routeApi
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        MapLibre.getInstance(this)

        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        mapView.isClickable = true
        mapView.isFocusable = true
        mapView.isFocusableInTouchMode = true
        mapView.requestFocus()

        addressEditText = findViewById(R.id.addressEditText)
        setDestinationButton = findViewById(R.id.setDestinationButton)

        locationTracker = LocationTracker(this)

        mapView.getMapAsync { map ->
            map.setStyle(Style.Builder().fromUri("https://tiles.openfreemap.org/styles/positron")) {
                startLocationTracking()
                if (hasLocationPermission()) {
                    getUserLocation()
                } else {
                    requestLocationPermission()
                }
            }

            map.addOnMapClickListener { point ->
                lifecycleScope.launch {
                    try {
                        val response = api.getPlaceInfo(point.latitude, point.longitude)
                        if (response.isSuccessful && response.body() != null) {
                            selectedPlaceInfo = response.body()
                            showPlaceInfoBottomSheet = true
                        } else {
                            Toast.makeText(this@MapActivity, "Заведение не найдено", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@MapActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
            true
        }

        findViewById<ComposeView>(R.id.composeView).setContent {
            GoodRoadTheme {
                if (showPlaceInfoBottomSheet && selectedPlaceInfo != null) {
                    PlaceInfoBottomSheet(
                        placeInfo = selectedPlaceInfo!!,
                        onDismiss = {
                            showPlaceInfoBottomSheet = false
                            selectedPlaceInfo = null
                        }
                    )
                }
            }
        }

        setDestinationButton.setOnClickListener {

            val destinationAddress = addressEditText.text.toString()

            if (destinationAddress.isNotBlank()) {
                getCoordinatesFromAddress(destinationAddress)
            } else {
                Toast.makeText(
                    this,
                    "Введите адрес назначения",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun addTemporaryMarker(point: LatLng) {
        mapView.getMapAsync { map ->
            map.getStyle { style ->
                style.removeLayer("click-marker-layer")
                style.removeSource("click-marker-source")

                val geojson = """
            {
                "type": "FeatureCollection",
                "features": [{
                    "type": "Feature",
                    "geometry": {
                        "type": "Point",
                        "coordinates": [${point.longitude}, ${point.latitude}]
                    }
                }]
            }
            """.trimIndent()

                val source = GeoJsonSource("click-marker-source", geojson)
                style.addSource(source)

                val circleLayer = CircleLayer("click-marker-layer", "click-marker-source").apply {
                    setProperties(
                        PropertyFactory.circleColor("#FF5722"),
                        PropertyFactory.circleRadius(12f),
                        PropertyFactory.circleOpacity(0.8f),
                        PropertyFactory.circleStrokeColor("#FFFFFF"),
                        PropertyFactory.circleStrokeWidth(2f)
                    )
                }
                style.addLayer(circleLayer)
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

                Toast.makeText(
                    this@MapActivity,
                    "Не удалось определить местоположение",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getCoordinatesFromAddress(address: String) {

        lifecycleScope.launch {

            Toast.makeText(
                this@MapActivity,
                "Поиск адреса...",
                Toast.LENGTH_SHORT
            ).show()

            val addresses = withContext(Dispatchers.IO) {

                try {

                    val geocoder = Geocoder(
                        this@MapActivity,
                        Locale.getDefault()
                    )

                    geocoder.getFromLocationName(address, 1)

                } catch (e: Exception) {
                    null
                }
            }

            if (!addresses.isNullOrEmpty()) {

                val destination = addresses[0]

                val endLat = destination.latitude
                val endLon = destination.longitude

                buildRoute(endLat, endLon)

            } else {

                Toast.makeText(
                    this@MapActivity,
                    "Адрес не найден. Попробуйте точнее.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun buildRoute(endLat: Double, endLon: Double) {

        lifecycleScope.launch {

            if (startLat == 0.0 || startLon == 0.0) {

                Toast.makeText(
                    this@MapActivity,
                    "Стартовая точка не определена",
                    Toast.LENGTH_SHORT
                ).show()

                return@launch
            }

            val res = ApiClient.obstacleApi.getUserObstaclePolicies()

            val policies = res.body()

            if(policies != null) {
                val obstaclePolicies = policies
                    .filter { it.selected && it.maxAllowedSeverity != null }
                    .map { RouteObstaclePolicy(it.obstacleType, it.maxAllowedSeverity) }

                val request = RouteRequest(
                    start = "$startLat,$startLon",
                    end = "$endLat,$endLon",
                    obstaclePolicies = obstaclePolicies
                )

                try {

                    val response = api.getRoute(request)

                    if(response.paths.isEmpty())  {
                        Toast.makeText(this@MapActivity, "Маршрутов нет", Toast.LENGTH_SHORT).show()
                    }

                    fastRoute = response.paths.find { it.routeType == "fast" }
                    balancedRoute = response.paths.find { it.routeType == "balanced" }
                    safeRoute = response.paths.find { it.routeType == "safe" }

                    fastRoute?.let {
                        val points = decodePoints(it.points).map { LatLng(it.latitude, it.longitude) }
                        drawRouteWithSegments(points, it.obstacles, "fast")
                    } ?: Toast.makeText(this@MapActivity, "Быстрый маршрут не найден", Toast.LENGTH_SHORT).show()
                    balancedRoute?.let {
                        val points = decodePoints(it.points).map { LatLng(it.latitude, it.longitude) }
                        drawRouteWithSegments(points, it.obstacles, "balanced")
                    } ?: Toast.makeText(this@MapActivity, "Сбалансированный маршрут не найден", Toast.LENGTH_SHORT).show()
                    safeRoute?.let {
                        val points = decodePoints(it.points).map { LatLng(it.latitude, it.longitude) }
                        drawRouteWithSegments(points, it.obstacles, "safe")
                    } ?: Toast.makeText(this@MapActivity, "Безопасный маршрут не найден", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {

                    Toast.makeText(
                        this@MapActivity,
                        "Ошибка: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                    e.printStackTrace()
                }
            }
        }
    }

    private fun drawRoute(pathResponse: PathResponse?) {
        if (pathResponse == null) {
            Toast.makeText(this, "Маршрут не найден", Toast.LENGTH_SHORT).show()
            return
        }

        val points = decodePoints(pathResponse.points)
        if (points.isEmpty()) {

            Toast.makeText(
                this,
                "Нет точек для отрисовки",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val latLngs = points.map {
            LatLng(it.latitude, it.longitude)
        }

        mapView.getMapAsync { map ->

            map.getStyle { style ->
                val layerId = "route-layer-${pathResponse.routeType}"
                val sourceId = "route-source-${pathResponse.routeType}"

                style.removeLayer(layerId)
                style.removeSource(sourceId)

                val coordinates = latLngs.joinToString(", ") {
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

                val source = GeoJsonSource(sourceId, geojson)
                style.addSource(source)

                val lineColor = when (pathResponse.routeType) {
                    "fast" -> "#4F87C9"
                    "balanced" -> "#8B7AC6"
                    "safe" -> "#6FAE8A"
                    else -> "#8B7AC6"
                }

                val lineLayer = LineLayer(layerId, sourceId).apply {
                    setProperties(
                        PropertyFactory.lineColor(lineColor),
                        PropertyFactory.lineWidth(6f),
                        PropertyFactory.lineOpacity(0.9f)
                    )
                }

                style.addLayer(lineLayer)

                if (latLngs.isNotEmpty()) {

                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            latLngs.first(),
                            14.0
                        ),
                        1000
                    )
                }
            }
        }
    }

    private fun drawRouteWithSegments(
        allPoints: List<LatLng>,
        obstacles: List<ObstacleResponse>,
        routeType: String
    ) {
        mapView.getMapAsync { map ->
            map.getStyle { style ->
                val layerPrefix = "segmented-layer-${routeType}"
                val sourcePrefix = "segmented-source-${routeType}"

                style.removeLayer(layerPrefix)
                style.removeSource(sourcePrefix)

                val defaultColor = when (routeType) {
                    "fast" -> "#4F87C9"
                    "balanced" -> "#8B7AC6"
                    "safe" -> "#6FAE8A"
                    else -> "#8B7AC6"
                }

                for (i in 0 until allPoints.size - 1) {
                    val segment = listOf(allPoints[i], allPoints[i + 1])
                    val segmentCoordinates = segment.joinToString(", ") {
                        "[${it.longitude}, ${it.latitude}]"
                    }

                    val segmentColor = getSegmentColor(segment, obstacles, routeType, defaultColor)

                    val segmentGeojson = """
                {
                    "type": "Feature",
                    "geometry": {
                        "type": "LineString",
                        "coordinates": [$segmentCoordinates]
                    }
                }
                """.trimIndent()

                    val segmentSourceId = "${sourcePrefix}-${i}"
                    val segmentLayerId = "${layerPrefix}-${i}"

                    val source = GeoJsonSource(segmentSourceId, segmentGeojson)
                    style.addSource(source)

                    val lineLayer = LineLayer(segmentLayerId, segmentSourceId).apply {
                        setProperties(
                            PropertyFactory.lineColor(segmentColor),
                            PropertyFactory.lineWidth(6f),
                            PropertyFactory.lineOpacity(0.9f)
                        )
                    }
                    style.addLayer(lineLayer)
                }

                if (allPoints.isNotEmpty()) {
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(allPoints.first(), 14.0),
                        1000
                    )
                }
            }
        }
    }

    private fun getSegmentColor(
        segment: List<LatLng>,
        obstacles: List<ObstacleResponse>,
        routeType: String,
        defaultColor: String
    ): String {

        val segmentCenterLat = (segment[0].latitude + segment[1].latitude) / 2
        val segmentCenterLon = (segment[0].longitude + segment[1].longitude) / 2

        val nearbyObstacle = obstacles.firstOrNull { obstacle ->
            val distance = haversineDistance(
                segmentCenterLat, segmentCenterLon,
                obstacle.latitude, obstacle.longitude
            )
            distance < 50.0 // Радиус поиска 50 метров
        }

        return when (routeType) {
            "fast" -> {
                when (nearbyObstacle?.severity) {
                    1.toShort() -> "#FFC107"     // LITE — жёлтый
                    2.toShort() -> "#FF9800"     // MEDIUM — оранжевый
                    3.toShort() -> "#F44336"     // IMPOSSIBLE — красный
                    else -> defaultColor
                }
            }
            "balanced" -> {
                when (nearbyObstacle?.severity) {
                    1.toShort() -> "#FFC107"     // LITE — жёлтый
                    2.toShort() -> "#FF9800"     // MEDIUM — оранжевый
                    else -> defaultColor
                }
            }
            else -> defaultColor
        }
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 // радиус Земли в метрах
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }

    private fun hasLocationPermission(): Boolean {

        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (
                grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {

                getUserLocation()

            } else {

                Toast.makeText(
                    this,
                    "Без разрешения геолокация не будет работать",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateUserMarker(location: LocationPoint) {
        val point = LatLng(location.latitude, location.longitude)

        mapView.getMapAsync { map ->
            map.getStyle { style ->
                style.removeLayer("user-marker-layer")
                style.removeSource("user-marker-source")

                val geojson = """
                {
                    "type": "FeatureCollection",
                    "features": [{
                        "type": "Feature",
                        "geometry": {
                            "type": "Point",
                            "coordinates": [${point.longitude}, ${point.latitude}]
                        }
                    }]
                }
            """.trimIndent()

                val source = GeoJsonSource("user-marker-source", geojson)
                style.addSource(source)

                val circleLayer = CircleLayer("user-marker-layer", "user-marker-source").apply {
                    setProperties(
                        PropertyFactory.circleColor("#4F87C9"),
                        PropertyFactory.circleRadius(8f),
                        PropertyFactory.circleOpacity(0.8f),
                        PropertyFactory.circleStrokeColor("#FFFFFF"),
                        PropertyFactory.circleStrokeWidth(2f)
                    )
                }
                style.addLayer(circleLayer)
            }
        }
    }

    private fun startLocationTracking() {
        lifecycleScope.launch {
            locationTracker.locationUpdates().collect { location ->
                updateUserMarker(location)
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