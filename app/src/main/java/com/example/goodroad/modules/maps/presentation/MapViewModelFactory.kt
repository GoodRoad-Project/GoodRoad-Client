package com.example.goodroad.modules.maps.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.goodroad.data.network.location.LocationTracker
import com.example.goodroad.data.obstacle.ObstacleRepository

class MapViewModelFactory(
    private val locationTracker: LocationTracker,
    private val obstacleRepository: ObstacleRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(
                locationTracker = locationTracker,
                obstacleRepository = obstacleRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}