package com.example.goodroad.modules.maps.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.network.GoodRoadApi
import com.example.goodroad.data.network.location.LocationTracker
import com.example.goodroad.data.network.route.RouteRequest
import com.example.goodroad.data.network.route.RouteResponse
import com.example.goodroad.data.network.utils.decodePoints
import com.example.goodroad.modules.maps.presentation.MapViewModel
import com.example.goodroad.modules.maps.presentation.MapViewModelFactory
import com.example.goodroad.ui.theme.*
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
import com.example.goodroad.data.obstacle.ObstacleRepository
import com.example.goodroad.modules.maps.services.MapService
import com.example.goodroad.ui.map.PlaceInfoBottomSheet
import java.util.Locale

@Composable
fun MapRouteScreen(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onNavigateToReview: (String, Double, Double) -> Unit = { _, _, _ -> }
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    MapLibre.getInstance(context)

    val locationTracker = remember { LocationTracker(context) }
    val obstacleRepository = remember {
        ObstacleRepository(ApiClient.obstacleApi)
    }

    val viewModel: MapViewModel = viewModel(
        factory = MapViewModelFactory(
            locationTracker = locationTracker,
            obstacleRepository = obstacleRepository
        )
    )

    val uiState by viewModel.uiState.collectAsState()
    val routes by viewModel.routes.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val selectedPlaceInfo by viewModel.selectedPlaceInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val viewModelMessage by viewModel.message.collectAsState()

    var address by rememberSaveable { mutableStateOf("") }

    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var styleReady by remember { mutableStateOf(false) }

    //var message by remember { mutableStateOf<String?>(null) }
    //var isLoadingMessage by remember { mutableStateOf(false) }

    var showPlaceInfo by remember { mutableStateOf(false) }
    val mapService = remember { MapService() }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->

        if (granted) {
            viewModel.getUserLocation()
        } else {
            //message = "Нет доступа к геолокации"
        }
    }

    val mapView = remember {
        MapView(context).apply {
            onCreate(Bundle())
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {

        val observer = object : DefaultLifecycleObserver {

            override fun onStart(owner: LifecycleOwner) {
                mapView.onStart()
            }

            override fun onResume(owner: LifecycleOwner) {
                mapView.onResume()
            }

            override fun onPause(owner: LifecycleOwner) {
                mapView.onPause()
            }

            override fun onStop(owner: LifecycleOwner) {
                mapView.onStop()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    LaunchedEffect(mapView) {

        mapView.getMapAsync { map ->

            mapLibreMap = map

            map.setStyle(
                Style.Builder().fromUri(
                    "https://tiles.openfreemap.org/styles/positron"
                )
            ) {

                styleReady = true

                map.addOnMapClickListener { point ->
                    viewModel.getPlaceInfo(point.latitude, point.longitude)
                    true
                }

                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    //loadUserLocation()
                    viewModel.getUserLocation()
                } else {
                    permissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
            }
        }
    }

    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            mapLibreMap?.let { map ->
                mapService.addMarker(
                    map = map,
                    point = LatLng(location.latitude, location.longitude),
                    markerId = "user-marker",
                    color = "#4F87C9",
                    radius = 8f
                )
            }
        }
    }

    LaunchedEffect(routes) {
        routes?.let { routeData ->
            mapLibreMap?.let { map ->
                mapService.clearRouteLayers(map)

                routeData.fast?.let { path ->
                    val points = decodePoints(path.points).map { LatLng(it.latitude, it.longitude) }
                    mapService.drawRouteWithSegments(map, points, path.obstacles, "fast")
                }

                routeData.balanced?.let { path ->
                    val points = decodePoints(path.points).map { LatLng(it.latitude, it.longitude) }
                    mapService.drawRouteWithSegments(map, points, path.obstacles, "balanced")
                }

                routeData.safe?.let { path ->
                    val points = decodePoints(path.points).map { LatLng(it.latitude, it.longitude) }
                    mapService.drawRouteWithSegments(map, points, path.obstacles, "safe")
                }

                routeData.fast?.let { path ->
                    val points = decodePoints(path.points)
                    if (points.isNotEmpty()) {
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(points.first().latitude, points.first().longitude),
                                14.0
                            ),
                            1000
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(selectedPlaceInfo) {
        selectedPlaceInfo?.let { placeInfo ->
            showPlaceInfo = true
        }
    }

    fun searchAddressAndBuildRoute() {
        scope.launch {
            if (address.isBlank()) {
                return@launch
            }

            val addresses = withContext(Dispatchers.IO) {
                try {
                    Geocoder(context, Locale.getDefault())
                        .getFromLocationName(address, 1)
                } catch (e: Exception) {
                    null
                }
            }

            if (addresses.isNullOrEmpty()) {
                return@launch
            }

            val destination = addresses[0]
            viewModel.buildRoute(
                destination.latitude,
                destination.longitude
            )
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),

            shadowElevation = 10.dp,

            shape = RoundedCornerShape(22.dp),

            color = SurfaceWarm
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                if (onBack != null) {

                    TextButton(
                        onClick = onBack,

                        colors = ButtonDefaults.textButtonColors(
                            contentColor = UrbanBrown
                        )
                    ) {
                        Text("Назад")
                    }
                }

                OutlinedTextField(
                    value = address,

                    onValueChange = {
                        address = it
                    },

                    modifier = Modifier.weight(1f),

                    singleLine = true,

                    placeholder = {
                        Text(
                            "Введите адрес",
                            color = TextSecondary
                        )
                    },

                    colors = OutlinedTextFieldDefaults.colors(

                        focusedContainerColor = WhiteSoft,
                        unfocusedContainerColor = WhiteSoft,

                        focusedBorderColor = UrbanBrown,
                        unfocusedBorderColor = BorderWarm,

                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,

                        cursorColor = UrbanBrown
                    ),

                    shape = RoundedCornerShape(16.dp)
                )

                Button(
                    onClick = { searchAddressAndBuildRoute() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UrbanBrown,
                        contentColor = WhiteSoft
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(
                        horizontal = 18.dp,
                        vertical = 14.dp
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = WhiteSoft,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Маршрут")
                    }
                }
            }
        }

        if (viewModelMessage != null && viewModelMessage!!.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp),
                color = SurfaceWarm,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 12.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = UrbanBrown,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    Text(
                        text = viewModelMessage ?: "",
                        color = TextPrimary
                    )
                }
            }
        }

        if (showPlaceInfo && selectedPlaceInfo != null) {
            selectedPlaceInfo?.let { placeInfo ->
                PlaceInfoBottomSheet(
                    placeInfo = placeInfo,
                    onDismiss = {
                        showPlaceInfo = false
                        viewModel.clearSelectedPlace()
                    },
                    onAddReview = { placeName, lat, lon ->
                        android.util.Log.d("MapRouteScreen", "🔴 onAddReview ВЫЗВАН!")
                        android.util.Log.d("MapRouteScreen", "placeName: $placeName, lat: $lat, lon: $lon")

                        showPlaceInfo = false
                        viewModel.clearSelectedPlace()
                        android.util.Log.d("MapRouteScreen", "🔴 ВЫЗЫВАЕМ onNavigateToReview")
                        onNavigateToReview(placeName, lat, lon)
                        android.util.Log.d("MapRouteScreen", "🔴 onNavigateToReview ВЫЗВАН")
                    }
                )
            }
        }
    }
}