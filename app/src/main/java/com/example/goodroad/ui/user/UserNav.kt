package com.example.goodroad.ui.user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.review.ReviewCardResp
import com.example.goodroad.data.review.ReviewRepository
import com.example.goodroad.data.user.UserRepository
import com.example.goodroad.ui.maps.MapsNav
import com.example.goodroad.ui.reviews.ReviewDetailsScreen
import com.example.goodroad.ui.reviews.ReviewFormScreen
import com.example.goodroad.ui.reviews.UserReviewsScreen
import com.example.goodroad.ui.users.users.UserEditScreen
import com.example.goodroad.ui.viewmodel.ReviewsViewModel
import com.example.goodroad.ui.viewmodel.UserViewModel

private const val SCREEN_PROFILE = "profile"
private const val SCREEN_EDIT = "edit"
private const val SCREEN_DELETE = "delete"
private const val SCREEN_OBSTACLES = "obstacles"
private const val SCREEN_MAP = "map"
private const val SCREEN_REVIEWS = "reviews"
private const val SCREEN_REVIEW_FORM = "review_form"
private const val SCREEN_REVIEW_DETAILS = "review_details"

@Composable
fun UserNav(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val userApi = ApiClient.userApi
    val reviewApi = ApiClient.reviewApi

    val userFactory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(UserRepository(userApi)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val reviewsFactory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReviewsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReviewsViewModel(ReviewRepository(reviewApi, userApi)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val userViewModel: UserViewModel = viewModel(factory = userFactory)
    val reviewsViewModel: ReviewsViewModel = viewModel(factory = reviewsFactory)

    var screen by rememberSaveable { mutableStateOf(SCREEN_PROFILE) }
    var selectedReview by remember { mutableStateOf<ReviewCardResp?>(null) }

    when (screen) {
        SCREEN_PROFILE -> UserProfileScreen(
            userViewModel = userViewModel,
            onEdit = { screen = SCREEN_EDIT },
            onDelete = { screen = SCREEN_DELETE },
            onLogout = onLogout,
            onSelectObstacles = { screen = SCREEN_OBSTACLES },
            onOpenMap = { screen = SCREEN_MAP },
            onOpenReviews = {
                selectedReview = null
                screen = SCREEN_REVIEWS
            }
        )

        SCREEN_EDIT -> UserEditScreen(
            userViewModel = userViewModel,
            onBack = { screen = SCREEN_PROFILE },
            onLogout = onLogout
        )

        SCREEN_DELETE -> UserDeleteAccountScreen(
            viewModel = userViewModel,
            onBack = { screen = SCREEN_PROFILE },
            onExit = onLogout
        )

        SCREEN_OBSTACLES -> MapsNav(
            onBackToProfile = { screen = SCREEN_PROFILE },
            onSaved = { screen = SCREEN_PROFILE }
        )

        SCREEN_MAP -> MapsNav(
            onBackToProfile = { screen = SCREEN_PROFILE },
            onSaved = { screen = SCREEN_PROFILE }
        )

        SCREEN_REVIEWS -> UserReviewsScreen(
            reviewsViewModel = reviewsViewModel,
            onBack = { screen = SCREEN_PROFILE },
            onAddReview = {
                selectedReview = null
                screen = SCREEN_REVIEW_FORM
            },
            onOpenDetails = {
                selectedReview = it
                screen = SCREEN_REVIEW_DETAILS
            },
            onEditReview = {
                selectedReview = it
                screen = SCREEN_REVIEW_FORM
            }
        )

        SCREEN_REVIEW_FORM -> ReviewFormScreen(
            reviewsViewModel = reviewsViewModel,
            initialReview = selectedReview,
            onBack = { screen = SCREEN_REVIEWS },
            onSaved = {
                selectedReview = null
                screen = SCREEN_REVIEWS
            }
        )

        SCREEN_REVIEW_DETAILS -> {
            val review = selectedReview
            if (review != null) {
                ReviewDetailsScreen(
                    review = review,
                    reviewsViewModel = reviewsViewModel,
                    onBack = { screen = SCREEN_REVIEWS },
                    onEdit = { screen = SCREEN_REVIEW_FORM },
                    onDeleted = {
                        selectedReview = null
                        screen = SCREEN_REVIEWS
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    screen = SCREEN_REVIEWS
                }
            }
        }
    }
}