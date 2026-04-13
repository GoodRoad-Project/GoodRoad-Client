package com.example.goodroad.ui.user

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.review.*
import com.example.goodroad.data.user.UserRepository
import com.example.goodroad.ui.maps.MapsNav
import com.example.goodroad.ui.reviews.*
import com.example.goodroad.ui.viewmodel.*

@Composable
fun UserNav(onLogout: () -> Unit) {

    val userApi = ApiClient.userApi
    val reviewApi = ApiClient.reviewApi

    val factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(UserRepository(userApi)) as T
            }
            if (modelClass.isAssignableFrom(ReviewsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReviewsViewModel(ReviewRepository(reviewApi, userApi)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val userViewModel: UserViewModel = viewModel(factory = factory)
    val reviewsViewModel: ReviewsViewModel = viewModel(factory = factory)

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
            onSaveObstacles = {
                screen = "profile"
            }
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
