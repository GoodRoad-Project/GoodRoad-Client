package com.example.goodroad.modules.user.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.modules.review.data.ReviewCardResp
import com.example.goodroad.modules.review.data.ReviewRepository
import com.example.goodroad.modules.user.data.UserRepository
import com.example.goodroad.modules.user.screens.UserDeleteAccountScreen
import com.example.goodroad.modules.user.screens.UserEditScreen
import com.example.goodroad.modules.user.screens.UserProfileScreen
import com.example.goodroad.modules.maps.navigation.MapsNav
import com.example.goodroad.modules.review.screens.ReviewDetailsScreen
import com.example.goodroad.modules.review.screens.ReviewFormScreen
import com.example.goodroad.modules.review.screens.UserReviewsScreen
import com.example.goodroad.modules.review.presentation.ReviewsViewModel
import com.example.goodroad.modules.user.presentation.UserViewModel

@Composable
fun UserNav(onLogout: () -> Unit) {
    val userApi = ApiClient.userApi
    val reviewApi = ApiClient.reviewApi

    val userFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(UserRepository(userApi)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val reviewsFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReviewsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReviewsViewModel(ReviewRepository(reviewApi, userApi)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val userViewModel: UserViewModel = viewModel(factory = userFactory)
    val reviewsViewModel: ReviewsViewModel = viewModel(factory = reviewsFactory)

    var screen by remember { mutableStateOf("profile") }
    var selectedReview by remember { mutableStateOf<ReviewCardResp?>(null) }

    when (screen) {
        "profile" -> UserProfileScreen(
            userViewModel = userViewModel,
            onEdit = { screen = "edit" },
            onDelete = { screen = "delete" },
            onLogout = onLogout,
            onSelectObstacles = { screen = "obstacles" },
            onOpenReviews = {
                selectedReview = null
                screen = "reviews"
            }
        )

        "edit" -> UserEditScreen(
            userViewModel = userViewModel,
            onBack = { screen = "profile" },
            onLogout = onLogout
        )

        "delete" -> UserDeleteAccountScreen(
            viewModel = userViewModel,
            onBack = { screen = "profile" },
            onExit = onLogout
        )

        "obstacles" -> MapsNav(
            onBackToProfile = {
                screen = "profile"
            },
            onSaved = {}
        )

        "reviews" -> UserReviewsScreen(
            reviewsViewModel = reviewsViewModel,
            onBack = { screen = "profile" },
            onAddReview = {
                selectedReview = null
                screen = "review_form"
            },
            onOpenDetails = {
                selectedReview = it
                screen = "review_details"
            },
            onEditReview = {
                selectedReview = it
                screen = "review_form"
            }
        )

        "review_form" -> ReviewFormScreen(
            reviewsViewModel = reviewsViewModel,
            initialReview = selectedReview,
            onBack = { screen = "reviews" },
            onSaved = {
                selectedReview = null
                screen = "reviews"
            }
        )

        "review_details" -> selectedReview?.let { review ->
            ReviewDetailsScreen(
                review = review,
                reviewsViewModel = reviewsViewModel,
                onBack = { screen = "reviews" },
                onEdit = { screen = "review_form" },
                onDeleted = {
                    selectedReview = null
                    screen = "reviews"
                }
            )
        } ?: run {
            LaunchedEffect(Unit) {
                screen = "reviews"
            }
        }
    }
}