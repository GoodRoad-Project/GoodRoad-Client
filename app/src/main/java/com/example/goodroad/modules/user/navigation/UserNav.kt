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

enum class AppScreen {
    MAP,
    REVIEWS,
    PROFILE,
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
    var appScreen by rememberSaveable { mutableStateOf(AppScreen.MAP) }
    var selectedReview by remember { mutableStateOf<ReviewCardResp?>(null) }

    val showBottomBar = when (appScreen) {
        AppScreen.MAP, AppScreen.REVIEWS, AppScreen.PROFILE -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentTab == BottomTab.MAP,
                        onClick = {
                            currentTab = BottomTab.MAP
                            appScreen = AppScreen.MAP
                        },
                        icon = { Text("🗺") },
                        label = { Text("Карта") }
                    )

                    NavigationBarItem(
                        selected = currentTab == BottomTab.REVIEWS,
                        onClick = {
                            currentTab = BottomTab.REVIEWS
                            appScreen = AppScreen.REVIEWS
                        },
                        icon = { Icon(Icons.Default.Star, null) },
                        label = { Text("Отзывы") }
                    )

                    NavigationBarItem(
                        selected = currentTab == BottomTab.PROFILE,
                        onClick = {
                            currentTab = BottomTab.PROFILE
                            appScreen = AppScreen.PROFILE
                        },
                        icon = { Icon(Icons.Default.Person, null) },
                        label = { Text("Профиль") }
                    )
                }
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (appScreen) {
                AppScreen.MAP -> {
                    MapRouteScreen()
                }

                AppScreen.REVIEWS -> {
                    UserReviewsScreen(
                        reviewsViewModel = reviewsViewModel,
                        onAddReview = {
                            selectedReview = null
                            appScreen = AppScreen.REVIEW_FORM
                        },
                        onOpenDetails = {
                            selectedReview = it
                            appScreen = AppScreen.REVIEW_DETAILS
                        },
                        onEditReview = {
                            selectedReview = it
                            appScreen = AppScreen.REVIEW_FORM
                        }
                    )
                }

                AppScreen.PROFILE -> {
                    UserProfileScreen(
                        userViewModel = userViewModel,
                        onEdit = {
                            appScreen = AppScreen.EDIT_PROFILE
                        },
                        onDelete = {
                            appScreen = AppScreen.DELETE_PROFILE
                        },
                        onLogout = onLogout,
                        onSelectObstacles = {}
                    )
                }

                AppScreen.EDIT_PROFILE -> {
                    UserEditScreen(
                        userViewModel = userViewModel,
                        onBack = {
                            appScreen = AppScreen.PROFILE
                            currentTab = BottomTab.PROFILE
                        },
                        onLogout = onLogout
                    )
                }

                AppScreen.DELETE_PROFILE -> {
                    UserDeleteAccountScreen(
                        viewModel = userViewModel,
                        onBack = {
                            appScreen = AppScreen.PROFILE
                            currentTab = BottomTab.PROFILE
                        },
                        onExit = onLogout
                    )
                }

                AppScreen.REVIEW_FORM -> {
                    ReviewFormScreen(
                        reviewsViewModel = reviewsViewModel,
                        initialReview = selectedReview,
                        onBack = {
                            appScreen = AppScreen.REVIEWS
                            currentTab = BottomTab.REVIEWS
                        },
                        onSaved = {
                            selectedReview = null
                            appScreen = AppScreen.REVIEWS
                            currentTab = BottomTab.REVIEWS
                        }
                    )
                }

                AppScreen.REVIEW_DETAILS -> {
                    val review = selectedReview
                    if (review != null) {
                        ReviewDetailsScreen(
                            review = review,
                            reviewsViewModel = reviewsViewModel,
                            onBack = {
                                appScreen = AppScreen.REVIEWS
                                currentTab = BottomTab.REVIEWS
                            },
                            onEdit = {
                                appScreen = AppScreen.REVIEW_FORM
                            },
                            onDeleted = {
                                selectedReview = null
                                appScreen = AppScreen.REVIEWS
                                currentTab = BottomTab.REVIEWS
                            }
                        )
                    } else {
                        appScreen = AppScreen.REVIEWS
                        currentTab = BottomTab.REVIEWS
                    }
                }
            }
        }
    }
}