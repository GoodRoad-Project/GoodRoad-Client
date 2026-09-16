package com.example.goodroad.modules.maps.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Route
import org.maplibre.android.style.layers.Property
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

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
            context = context,
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

    var startAddress by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }

    var selectedRouteType by rememberSaveable { mutableStateOf<String?>(null) }

    var showStartField by rememberSaveable { mutableStateOf(false) }

    var showInstruction by rememberSaveable { mutableStateOf(false) }

    var showCurrentLocationOption by remember { mutableStateOf(false) }

    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var styleReady by remember { mutableStateOf(false) }

    var startAddressError by rememberSaveable { mutableStateOf(false) }

    //var message by remember { mutableStateOf<String?>(null) }
    //var isLoadingMessage by remember { mutableStateOf(false) }

    var showPlaceInfo by remember { mutableStateOf(false) }
    val mapService = remember { MapService() }

    val preferences = remember {
        context.getSharedPreferences("goodroad_preferences", android.content.Context.MODE_PRIVATE)
    }

    LaunchedEffect(Unit) {
        val instructionShown = preferences.getBoolean("instruction_shown", false)

        if (!instructionShown) {
            showInstruction = true
            preferences.edit()
                .putBoolean("instruction_shown", true)
                .apply()
        }
    }

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

    LaunchedEffect(userLocation, mapLibreMap) {
        val location = userLocation ?: return@LaunchedEffect
        val map = mapLibreMap ?: return@LaunchedEffect

        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ),
                16.0
            ),
            1000
        )
    }

    LaunchedEffect(routes, mapLibreMap, styleReady) {
        routes?.let { routeData ->
            mapLibreMap?.let { map ->

                Log.d(
                    "RouteSimplification",
                    "Получены новые маршруты"
                )

                mapService.clearRouteLayers(map)

                routeData.fast?.let { path ->
                    val points = decodePoints(path.points)
                        .map {
                            LatLng(
                                it.latitude,
                                it.longitude
                            )
                        }

                    Log.d(
                        "RouteSimplification",
                        "fast: points=${points.size}, " +
                                "obstacles=${path.obstacles.size}"
                    )

                    mapService.setRoute(
                        map = map,
                        points = points,
                        obstacles = path.obstacles,
                        routeType = "fast"
                    )
                }

                routeData.balanced?.let { path ->
                    val points = decodePoints(path.points)
                        .map {
                            LatLng(
                                it.latitude,
                                it.longitude
                            )
                        }

                    Log.d(
                        "RouteSimplification",
                        "balanced: points=${points.size}, " +
                                "obstacles=${path.obstacles.size}"
                    )

                    mapService.setRoute(
                        map = map,
                        points = points,
                        obstacles = path.obstacles,
                        routeType = "balanced"
                    )
                }

                routeData.safe?.let { path ->
                    val points = decodePoints(path.points)
                        .map {
                            LatLng(
                                it.latitude,
                                it.longitude
                            )
                        }

                    Log.d(
                        "RouteSimplification",
                        "safe: points=${points.size}, " +
                                "obstacles=${path.obstacles.size}"
                    )

                    mapService.setRoute(
                        map = map,
                        points = points,
                        obstacles = path.obstacles,
                        routeType = "safe"
                    )
                }

                routeData.fast?.let { path ->
                    val points = decodePoints(path.points)

                    if (points.isNotEmpty()) {

                        Log.d(
                            "RouteSimplification",
                            "Перемещение камеры на начало fast-маршрута"
                        )

                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    points.first().latitude,
                                    points.first().longitude
                                ),
                                14.0
                            ),
                            1000
                        )
                    }
                }
            }
        }
    }

    DisposableEffect(mapLibreMap, styleReady) {
        val map = mapLibreMap
            ?: return@DisposableEffect onDispose {}

        val cameraListener = MapLibreMap.OnCameraIdleListener {

            val zoom = map.cameraPosition.zoom

            Log.d(
                "RouteSimplification",
                "Камера остановилась: zoom=$zoom"
            )

            mapService.updateDetailLevel(
                map = map,
                zoom = zoom
            )
        }

        map.addOnCameraIdleListener(cameraListener)

        val initialZoom = map.cameraPosition.zoom

        Log.d(
            "RouteSimplification",
            "Инициализация уровня детализации: zoom=$initialZoom"
        )

        mapService.updateDetailLevel(
            map = map,
            zoom = initialZoom
        )

        onDispose {
            map.removeOnCameraIdleListener(cameraListener)

            Log.d(
                "RouteSimplification",
                "CameraIdleListener удалён"
            )
        }
    }

    LaunchedEffect(selectedPlaceInfo) {
        selectedPlaceInfo?.let { placeInfo ->
            showPlaceInfo = true
        }
    }

    fun parseCoordinates(value: String): Pair<Double, Double>? {
        val normalized = value
            .trim()
            .replace(";", ",")
            .replace(Regex("\\s+"), " ")

        val parts = normalized.split(Regex("[, ]"))

        if (parts.size != 2) {
            return null
        }

        val latitude = parts[0].toDoubleOrNull() ?: return null
        val longitude = parts[1].toDoubleOrNull() ?: return null

        if (latitude !in -90.0..90.0) {
            return null
        }

        if (longitude !in -180.0..180.0) {
            return null
        }

        return latitude to longitude
    }

    fun searchAddressAndBuildRoute() {
        scope.launch {
            if (startAddress.isBlank()) {
                startAddressError = true
                return@launch
            }

            startAddressError = false

            if (startAddress == "Моё местоположение") {
                if (!viewModel.hasStartLocation()) {
                    viewModel.getUserLocation()
                    return@launch
                }
            } else {
                val startFound = viewModel.setStartAddress(startAddress)
                if (!startFound) {
                    return@launch
                }
            }

            val coordinates = parseCoordinates(address)

            if (coordinates != null) {
                viewModel.buildRoute(
                    coordinates.first,
                    coordinates.second
                )
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

    LaunchedEffect(selectedRouteType, mapLibreMap, styleReady) {
        mapLibreMap?.let { map ->
            if (styleReady) {
                mapService.setSelectedRoute(map, selectedRouteType)
            }
        }
    }

    if (showInstruction) {
        AlertDialog(
            onDismissRequest = {
                showInstruction = false
            },
            title = {
                Text(
                    text = "Памятка",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 450.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = """
                Дорогой пользователь!

                Перед началом использования карты ознакомьтесь с этой инструкцией.

                При построении пути Вам будет предложено 3 вида маршрута: безопасный, сбалансированный и быстрый.

                Быстрый маршрут не учитывает Ваши ограничения, а просто показывает обычный путь.

                Сбалансированный маршрут покажет наиболее быстрый путь, в котором не будет непреодолимых для Вас препятствий.

                Безопасный маршрут покажет Вам путь, в котором вообще не будет препятствий, вызывающих у Вас трудности.

                На каждом пути, кроме безопасного, будут показаны препятствия, которые Вы выбрали в своём личном кабинете.

                Жёлтый цвет означает слабую тяжесть, оранжевый — среднюю, а красный — высокую.

                Мы надеемся, что Вам понравится наше приложение!

                В добрый путь!
            """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showInstruction = false
                    }
                ) {
                    Text("Понятно")
                }
            }
        )
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
            color = SurfaceWarm.copy(alpha = 0.92f),
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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

                if (showStartField) {
                    OutlinedTextField(
                        value = startAddress,
                        onValueChange = {
                            startAddress = it
                            startAddressError = false
                            showCurrentLocationOption = it.isBlank()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                            .onFocusChanged {
                                showCurrentLocationOption = it.isFocused && startAddress.isBlank()
                            },
                        singleLine = true,
                        placeholder = {
                            Text("Откуда", color = TextSecondary)
                        },
                        isError = startAddressError,
                        supportingText = {
                            if (startAddressError) {
                                Text("Введите адрес отправления")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UrbanBrown,
                            unfocusedBorderColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = UrbanBrown
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    if (showCurrentLocationOption) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp),
                            color = WhiteSoft,
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 4.dp,
                            onClick = {
                                startAddress = "Моё местоположение"
                                viewModel.getUserLocation()
                                showCurrentLocationOption = false
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    "Моё местоположение",
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .onFocusChanged {
                            if (it.isFocused) {
                                showStartField = true
                            }
                        },
                    singleLine = true,
                    placeholder = {
                        Text("Куда", color = TextSecondary)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UrbanBrown,
                        unfocusedBorderColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = UrbanBrown
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Button(
            onClick = { searchAddressAndBuildRoute() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .offset(y = (-106).dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = UrbanBrown,
                contentColor = WhiteSoft
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(
                horizontal = 28.dp,
                vertical = 8.dp
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = WhiteSoft,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Route,
                    contentDescription = "Построить маршрут",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (viewModelMessage != null && viewModelMessage!!.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 180.dp),
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
                        android.util.Log.d(
                            "MapRouteScreen",
                            "placeName: $placeName, lat: $lat, lon: $lon"
                        )

                        showPlaceInfo = false
                        viewModel.clearSelectedPlace()
                        android.util.Log.d("MapRouteScreen", "🔴 ВЫЗЫВАЕМ onNavigateToReview")
                        onNavigateToReview(placeName, lat, lon)
                        android.util.Log.d("MapRouteScreen", "🔴 onNavigateToReview ВЫЗВАН")
                    }
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            color = SurfaceWarm.copy(alpha = 0.92f),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                selectedRouteType =
                                    if (selectedRouteType == "fast") null else "fast"
                            }
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    Color(0xFF4F87C9),
                                    RoundedCornerShape(4.dp)
                                )
                        )

                        Text(
                            text = "Быстрый",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                selectedRouteType =
                                    if (selectedRouteType == "balanced") null else "balanced"
                            }
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    Color(0xFF8B7AC6),
                                    RoundedCornerShape(4.dp)
                                )
                        )

                        Text(
                            text = "Сбалансированный",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                selectedRouteType =
                                    if (selectedRouteType == "safe") null else "safe"
                            }
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    Color(0xFF6FAE8A),
                                    RoundedCornerShape(4.dp)
                                )
                        )

                        Text(
                            text = "Безопасный",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}