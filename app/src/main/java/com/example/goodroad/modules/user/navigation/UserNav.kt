package com.example.goodroad.modules.user.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.modules.maps.screens.MapRouteScreen
import com.example.goodroad.modules.review.data.ReviewCardResp
import com.example.goodroad.modules.review.data.ReviewRepository
import com.example.goodroad.modules.review.presentation.ReviewsViewModel
import com.example.goodroad.modules.review.screens.ReviewDetailsScreen
import com.example.goodroad.modules.review.screens.ReviewFormScreen
import com.example.goodroad.modules.review.screens.UserReviewsScreen
import com.example.goodroad.modules.user.data.UserRepository
import com.example.goodroad.modules.user.presentation.UserViewModel
import com.example.goodroad.modules.user.screens.UserEditScreen
import com.example.goodroad.ui.user.UserDeleteAccountScreen
import com.example.goodroad.ui.user.UserProfileScreen

enum class BottomTab {
    MAP,
    REVIEWS,
    PROFILE
}

enum class OverlayScreen {
    NONE,
    EDIT_PROFILE,
    DELETE_PROFILE,
    REVIEW_FORM,
    REVIEW_DETAILS
}

@Composable
fun UserNav(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val userApi = ApiClient.userApi
    val reviewApi = ApiClient.reviewApi

    val userFactory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel(UserRepository(userApi)) as T
        }
    }

    val reviewsFactory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return ReviewsViewModel(
                ReviewRepository(reviewApi, userApi)
            ) as T
        }
    }

    val userViewModel: UserViewModel = viewModel(factory = userFactory)
    val reviewsViewModel: ReviewsViewModel = viewModel(factory = reviewsFactory)

    var currentTab by rememberSaveable { mutableStateOf(BottomTab.MAP) }
    var overlayScreen by remember { mutableStateOf(OverlayScreen.NONE) }
    var selectedReview by remember { mutableStateOf<ReviewCardResp?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentTab == BottomTab.MAP,
                    onClick = {
                        currentTab = BottomTab.MAP
                        overlayScreen = OverlayScreen.NONE
                    },
                    icon = { Text("🗺") },
                    label = { Text("Карта") }
                )

                NavigationBarItem(
                    selected = currentTab == BottomTab.REVIEWS,
                    onClick = {
                        currentTab = BottomTab.REVIEWS
                        overlayScreen = OverlayScreen.NONE
                    },
                    icon = { Icon(Icons.Default.Star, null) },
                    label = { Text("Отзывы") }
                )

                NavigationBarItem(
                    selected = currentTab == BottomTab.PROFILE,
                    onClick = {
                        currentTab = BottomTab.PROFILE
                        overlayScreen = OverlayScreen.NONE
                    },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Профиль") }
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (currentTab) {
                BottomTab.MAP -> {
                    MapRouteScreen()
                }

                BottomTab.REVIEWS -> {
                    UserReviewsScreen(
                        reviewsViewModel = reviewsViewModel,
                        onAddReview = {
                            selectedReview = null
                            overlayScreen = OverlayScreen.REVIEW_FORM
                        },
                        onOpenDetails = {
                            selectedReview = it
                            overlayScreen = OverlayScreen.REVIEW_DETAILS
                        },
                        onEditReview = {
                            selectedReview = it
                            overlayScreen = OverlayScreen.REVIEW_FORM
                        }
                    )
                }

                BottomTab.PROFILE -> {
                    UserProfileScreen(
                        userViewModel = userViewModel,
                        onEdit = { overlayScreen = OverlayScreen.EDIT_PROFILE },
                        onDelete = { overlayScreen = OverlayScreen.DELETE_PROFILE },
                        onLogout = onLogout,
                        onSelectObstacles = {}
                    )
                }
            }

            when (overlayScreen) {
                OverlayScreen.EDIT_PROFILE -> {
                    UserEditScreen(
                        userViewModel = userViewModel,
                        onBack = { overlayScreen = OverlayScreen.NONE },
                        onLogout = onLogout
                    )
                }

                OverlayScreen.DELETE_PROFILE -> {
                    UserDeleteAccountScreen(
                        viewModel = userViewModel,
                        onBack = { overlayScreen = OverlayScreen.NONE },
                        onExit = onLogout
                    )
                }

                OverlayScreen.REVIEW_FORM -> {
                    ReviewFormScreen(
                        reviewsViewModel = reviewsViewModel,
                        initialReview = selectedReview,
                        onBack = { overlayScreen = OverlayScreen.NONE },
                        onSaved = {
                            selectedReview = null
                            overlayScreen = OverlayScreen.NONE
                        }
                    )
                }

                OverlayScreen.REVIEW_DETAILS -> {
                    val review = selectedReview
                    if (review != null) {
                        ReviewDetailsScreen(
                            review = review,
                            reviewsViewModel = reviewsViewModel,
                            onBack = { overlayScreen = OverlayScreen.NONE },
                            onEdit = { overlayScreen = OverlayScreen.REVIEW_FORM },
                            onDeleted = {
                                selectedReview = null
                                overlayScreen = OverlayScreen.NONE
                            }
                        )
                    } else {
                        overlayScreen = OverlayScreen.NONE
                    }
                }

                OverlayScreen.NONE -> Unit
            }
        }
    }
}