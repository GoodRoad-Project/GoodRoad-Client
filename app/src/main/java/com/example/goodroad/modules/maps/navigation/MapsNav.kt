package com.example.goodroad.modules.maps.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.*
import com.example.goodroad.data.network.*
import com.example.goodroad.data.obstacle.*
import com.example.goodroad.modules.maps.presentation.MapsViewModel
import com.example.goodroad.modules.maps.screens.ObstacleSelectScreen
import com.example.goodroad.modules.maps.presentation.MapViewModel
import com.example.goodroad.modules.maps.presentation.MapViewModelFactory
import com.example.goodroad.modules.maps.screens.MapRouteScreen
import com.example.goodroad.modules.review.screens.ReviewFormScreen
import com.example.goodroad.data.network.location.LocationTracker
import com.example.goodroad.modules.review.data.ReviewRepository
import com.example.goodroad.modules.review.presentation.ReviewsViewModel

@Composable
fun MapsNav(
    onBackToProfile: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current

    var showReviewForm by remember { mutableStateOf(false) }
    var reviewPlaceName by remember { mutableStateOf("") }
    var reviewLat by remember { mutableStateOf(0.0) }
    var reviewLon by remember { mutableStateOf(0.0) }

    val api = ApiClient.obstacleApi

    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MapsViewModel(ObstacleRepository(api)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val mapsViewModel: MapsViewModel = viewModel(factory = factory)

    val locationTracker = remember { LocationTracker(context) }
    val obstacleRepository = remember { ObstacleRepository(ApiClient.obstacleApi) }

    val mapViewModel: MapViewModel = viewModel(
        factory = MapViewModelFactory(
            context = context,
            locationTracker = locationTracker,
            obstacleRepository = obstacleRepository
        )
    )

    val reviewRepository = remember { ReviewRepository(ApiClient.reviewApi) }

    val reviewsViewModel: ReviewsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ReviewsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ReviewsViewModel(reviewRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    if (showReviewForm) {
        android.util.Log.d("MapsNav", "🔴 ПОКАЗЫВАЕМ ReviewFormScreen")
        ReviewFormScreen(
            reviewsViewModel = reviewsViewModel,
            initialReview = null,
            initialPlaceName = reviewPlaceName,
            initialLatitude = reviewLat.toString(),
            initialLongitude = reviewLon.toString(),
            onBack = {
                android.util.Log.d("MapsNav", "🔴 Назад из ReviewFormScreen")
                showReviewForm = false
            },
            onSaved = {
                android.util.Log.d("MapsNav", "🔴 Сохранено из ReviewFormScreen")
                showReviewForm = false
                onSaved()
            }
        )
    } else {
        android.util.Log.d("MapsNav", "🔴 ПОКАЗЫВАЕМ MapRouteScreen")
        MapRouteScreen(
            onNavigateToReview = { placeName, lat, lon ->
                android.util.Log.d("MapsNav", "🔴🔴🔴 onNavigateToReview В MapsNav!")
                android.util.Log.d("MapsNav", "placeName: $placeName, lat: $lat, lon: $lon")
                reviewPlaceName = placeName
                reviewLat = lat
                reviewLon = lon
                showReviewForm = true
                android.util.Log.d("MapsNav", "showReviewForm = $showReviewForm")
            },
            onBack = onBackToProfile
        )
    }

//    ObstacleSelectScreen(
//        mapsViewModel = mapsViewModel,
//        onBackToProfile = onBackToProfile,
//        onSaved = onSaved
//    )
}
