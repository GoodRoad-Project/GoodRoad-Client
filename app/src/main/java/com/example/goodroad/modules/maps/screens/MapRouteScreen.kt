package com.example.goodroad.modules.maps.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.network.GoodRoadApi
import com.example.goodroad.data.network.location.LocationTracker
import com.example.goodroad.data.network.route.RouteRequest
import com.example.goodroad.data.network.route.RouteResponse
import com.example.goodroad.data.network.utils.decodePoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun MapRouteScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    MapLibre.getInstance(context)

    val locationTracker = remember { LocationTracker(context) }
    val api: GoodRoadApi = remember { ApiClient.routeApi }

    var address by rememberSaveable { mutableStateOf("") }
    var startLat by remember { mutableStateOf(0.0) }
    var startLon by remember { mutableStateOf(0.0) }

    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var styleReady by remember { mutableStateOf(false) }

    lateinit var loadUserLocation: () -> Unit

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            loadUserLocation()
        } else {
            Toast.makeText(context, "Нет доступа к геолокации", Toast.LENGTH_SHORT).show()
        }
    }

    val mapView = remember {
        MapView(context).apply {
            onCreate(Bundle())
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) = mapView.onStart()
            override fun onResume(owner: LifecycleOwner) = mapView.onResume()
            override fun onPause(owner: LifecycleOwner) = mapView.onPause()
            override fun onStop(owner: LifecycleOwner) = mapView.onStop()
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    loadUserLocation = {
        scope.launch {
            val loc = locationTracker.getCurrentLocation()
            if (loc != null) {
                startLat = loc.latitude
                startLon = loc.longitude
            }
        }
    }

    LaunchedEffect(mapView) {
        mapView.getMapAsync { map ->
            mapLibreMap = map

            map.setStyle(
                Style.Builder().fromUri("https://tiles.openfreemap.org/styles/positron")
            ) {
                styleReady = true

                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    loadUserLocation()
                } else {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
    }

    fun drawRoute(response: RouteResponse) {
        val path = response.paths.firstOrNull() ?: return
        val points = decodePoints(path.points)

        if (points.isEmpty()) return

        val latLngs = points.map { LatLng(it.latitude, it.longitude) }
        val map = mapLibreMap ?: return

        map.getStyle { style ->
            style.removeLayer("route-layer")
            style.removeSource("route-source")

            val coords = latLngs.joinToString(", ") {
                "[${it.longitude}, ${it.latitude}]"
            }

            val geojson = """
                {
                  "type": "FeatureCollection",
                  "features": [{
                    "type": "Feature",
                    "geometry": {
                      "type": "LineString",
                      "coordinates": [$coords]
                    }
                  }]
                }
            """.trimIndent()

            style.addSource(GeoJsonSource("route-source", geojson))

            style.addLayer(
                LineLayer("route-layer", "route-source").apply {
                    setProperties(
                        PropertyFactory.lineColor("#8B7AC6"),
                        PropertyFactory.lineWidth(6f),
                        PropertyFactory.lineOpacity(0.9f)
                    )
                }
            )

            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(latLngs.first(), 14.0),
                1000
            )
        }
    }

    fun buildRoute(endLat: Double, endLon: Double) {
        scope.launch {
            if (!styleReady || mapLibreMap == null) return@launch
            if (startLat == 0.0 || startLon == 0.0) return@launch

            val res = ApiClient.obstacleApi.getUserObstaclePolicies()
            val policies = res.body() ?: return@launch

            val request = RouteRequest(
                start = "$startLat,$startLon",
                end = "$endLat,$endLon",
                avoidStairs = policies.any { it.obstacleType == "STAIRS" && it.selected },
                maxCurbHeight = policies.find { it.obstacleType == "CURB" }?.maxAllowedSeverity?.toInt(),
                maxSlopeAngle = policies.find { it.obstacleType == "ROAD_SLOPE" }?.maxAllowedSeverity?.toDouble(),
                avoidBadRoad = policies.any { it.obstacleType == "POTHOLES" && it.selected },
                avoidSurfaceTypes = policies.filter { it.selected }.map { it.obstacleType }
            )

            try {
                val response = api.getRoute(request)
                drawRoute(response)
            } catch (e: Exception) {
                Toast.makeText(context, e.message ?: "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Маршрут")

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Адрес") }
                )

                Button(onClick = {
                    scope.launch {
                        val geo = withContext(Dispatchers.IO) {
                            Geocoder(context).getFromLocationName(address, 1)
                        }

                        val dest = geo?.firstOrNull() ?: return@launch
                        buildRoute(dest.latitude, dest.longitude)
                    }
                }) {
                    Text("Построить")
                }
            }
        }
    }
}