package com.example.goodroad.modules.maps.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.network.GoodRoadApi
import com.example.goodroad.data.network.location.LocationTracker
import com.example.goodroad.data.network.route.PathResponse
import com.example.goodroad.data.network.route.RouteObstaclePolicy
import com.example.goodroad.data.network.route.RouteRequest
import com.example.goodroad.data.obstacle.ObstacleRepository
import com.example.goodroad.data.place.PlaceInfoResponse
import com.example.goodroad.domain.model.LocationPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val locationTracker: LocationTracker,
    private val obstacleRepository: ObstacleRepository,
    private val api: GoodRoadApi = ApiClient.routeApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Idle)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _userLocation = MutableStateFlow<LocationPoint?>(null)
    val userLocation: StateFlow<LocationPoint?> = _userLocation.asStateFlow()

    private val _routes = MutableStateFlow<RoutesData?>(null)
    val routes: StateFlow<RoutesData?> = _routes.asStateFlow()

    private val _selectedPlaceInfo = MutableStateFlow<PlaceInfoResponse?>(null)
    val selectedPlaceInfo: StateFlow<PlaceInfoResponse?> = _selectedPlaceInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private var startLat: Double = 0.0
    private var startLon: Double = 0.0

    fun getUserLocation() {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = "Определение местоположения..."

            try {
                val location = locationTracker.getCurrentLocation()
                if (location != null) {
                    startLat = location.latitude
                    startLon = location.longitude
                    _userLocation.value = location
                    _uiState.value = MapUiState.Success("Местоположение определено")
                    _message.value = null
                } else {
                    _uiState.value = MapUiState.Error("Не удалось определить местоположение")
                    _message.value = "Не удалось определить местоположение"
                }
            } catch (e: Exception) {
                _uiState.value = MapUiState.Error(e.message ?: "Ошибка геолокации")
                _message.value = e.message ?: "Ошибка геолокации"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun buildRoute(endLat: Double, endLon: Double) {
        viewModelScope.launch {
            if (startLat == 0.0 || startLon == 0.0) {
                _message.value = "Стартовая точка не определена"
                _uiState.value = MapUiState.Error("Стартовая точка не определена")
                return@launch
            }

            _isLoading.value = true
            _message.value = "Построение маршрута..."
            _uiState.value = MapUiState.Loading

            try {
                val policies = obstacleRepository.getUserObstaclePolicies()

                if (policies.isEmpty()) {
                    _message.value = "Нет настроенных политик препятствий"
                    _uiState.value = MapUiState.Error("Нет настроенных политик препятствий")
                    _isLoading.value = false
                    return@launch
                }

                val obstaclePolicies = policies
                    .filter { it.selected && it.maxAllowedSeverity != null }
                    .map { RouteObstaclePolicy(it.obstacleType, it.maxAllowedSeverity) }

                val request = RouteRequest(
                    start = "$startLat,$startLon",
                    end = "$endLat,$endLon",
                    obstaclePolicies = obstaclePolicies
                )

                val response = api.getRoute(request)

                if (response.paths.isEmpty()) {
                    _message.value = "Маршрутов не найдено"
                    _uiState.value = MapUiState.Error("Маршрутов не найдено")
                    _isLoading.value = false
                    return@launch
                }

                val fastRoute = response.paths.find { it.routeType == "fast" }
                val balancedRoute = response.paths.find { it.routeType == "balanced" }
                val safeRoute = response.paths.find { it.routeType == "safe" }

                _routes.value = RoutesData(
                    fast = fastRoute,
                    balanced = balancedRoute,
                    safe = safeRoute
                )

                _uiState.value = MapUiState.Success("Маршруты построены")
                _message.value = null

            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is java.io.IOException -> "Ошибка сети. Проверьте подключение."
                    is retrofit2.HttpException -> when (e.code()) {
                        400 -> "Некорректный запрос. Проверьте параметры."
                        401 -> "Требуется авторизация."
                        403 -> "Доступ запрещен."
                        500 -> "Ошибка сервера. Попробуйте позже."
                        else -> "Ошибка: ${e.message}"
                    }
                    else -> e.message ?: "Неизвестная ошибка"
                }
                _message.value = errorMsg
                _uiState.value = MapUiState.Error(errorMsg)
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPlaceInfo(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = api.getPlaceInfo(lat, lon)
                if (response.isSuccessful && response.body() != null) {
                    _selectedPlaceInfo.value = response.body()
                } else {
                    _message.value = "Заведение не найдено"
                }
            } catch (e: Exception) {
                _message.value = "Ошибка: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun clearSelectedPlace() {
        _selectedPlaceInfo.value = null
    }

    fun clearRoutes() {
        _routes.value = null
        _uiState.value = MapUiState.Idle
    }

    fun clearMessages() {
        _message.value = null
    }

    fun setStartLocation(lat: Double, lon: Double) {
        startLat = lat
        startLon = lon
    }
}

sealed class MapUiState {
    object Idle : MapUiState()
    object Loading : MapUiState()
    data class Success(val message: String) : MapUiState()
    data class Error(val message: String) : MapUiState()
}

data class RoutesData(
    val fast: PathResponse?,
    val balanced: PathResponse?,
    val safe: PathResponse?
)