package com.example.goodroad.ui.maps

import androidx.compose.runtime.*
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.*
import com.example.goodroad.data.network.*
import com.example.goodroad.data.obstacle.*
import com.example.goodroad.ui.viewmodel.*

@Composable
fun MapsNav(
    onBackToProfile: () -> Unit,
    onSaved: () -> Unit
) {
    val api = ApiClient.obstacleApi

    val factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MapsViewModel(ObstacleRepository(api)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val mapsViewModel: MapsViewModel = viewModel(factory = factory)

    ObstacleSelectScreen(
        mapsViewModel = mapsViewModel,
        onBackToProfile = onBackToProfile,
        onSaved = onSaved
    )
}
