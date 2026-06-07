package com.example.goodroad.modules.user.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.network.ApiClient.rewardsApi
import com.example.goodroad.data.obstacle.ObstacleRepository
import com.example.goodroad.modules.maps.presentation.MapsViewModel
import com.example.goodroad.modules.maps.screens.MapRouteScreen
import com.example.goodroad.modules.maps.screens.ObstacleSelectScreen
import com.example.goodroad.modules.review.data.ReviewCardResp
import com.example.goodroad.modules.review.data.ReviewRepository
import com.example.goodroad.modules.review.presentation.ReviewsViewModel
import com.example.goodroad.modules.review.screens.ReviewFormScreen
import com.example.goodroad.modules.review.screens.ReviewDetailsScreen
import com.example.goodroad.modules.review.screens.UserReviewsScreen
import com.example.goodroad.modules.user.data.UserRepository
import com.example.goodroad.modules.user.presentation.UserViewModel
import com.example.goodroad.modules.user.screens.UserEditScreen
import com.example.goodroad.modules.volunteer.data.VolunteerRepository
import com.example.goodroad.modules.volunteer.presentation.VolunteerViewModel
import com.example.goodroad.modules.volunteer.screens.*
import com.example.goodroad.ui.user.UserDeleteAccountScreen
import com.example.goodroad.ui.user.UserProfileScreen
import com.example.goodroad.ui.volunteer.screens.VolunteerApplicationFormScreen
import com.example.goodroad.modules.rewards.screens.RewardsShopScreen
import com.example.goodroad.modules.rewards.screens.RewardDetailScreen
import com.example.goodroad.modules.rewards.screens.RewardsHistoryScreen
import com.example.goodroad.modules.rewards.screens.LeaderboardScreen
import com.example.goodroad.modules.rewards.data.RewardsRepository
import com.example.goodroad.modules.rewards.data.RewardOffer
import com.example.goodroad.modules.rewards.presentation.RewardsViewModel
import com.example.goodroad.modules.tasks.presentation.TasksViewModel
import com.example.goodroad.modules.tasks.data.TasksRepository
import com.example.goodroad.modules.tasks.screens.TasksScreen
import com.example.goodroad.modules.tasks.data.TaskViewDto
import com.example.goodroad.modules.tasks.screens.TaskExecutionScreen

enum class BottomTab {
    MAP,
    REVIEWS,
    HELP,
    PROFILE
}

enum class OverlayScreen {
    NONE,
    EDIT_PROFILE,
    DELETE_PROFILE,
    REVIEW_FORM,
    REVIEW_DETAILS,
    OBSTACLES,
    HELP_CREATE,
    HELP_MY_REQUESTS,
    VOLUNTEER_APPLICATION,
    VOLUNTEER_FEED,
    VOLUNTEER_WARDS,
    REWARDS_SHOP,
    REWARD_DETAIL,
    REWARDS_HISTORY,
    LEADERBOARD,
    TASKS_SHOP,
    TASK_DETAIL
}

@Composable
fun UserNav(
    navController: NavHostController,
    onLogout: () -> Unit
) {

    val userApi = ApiClient.userApi
    val reviewApi = ApiClient.reviewApi
    val obstacleApi = ApiClient.obstacleApi
    val volunteerApi = ApiClient.volunteerApi
    val tasksApi = ApiClient.tasksApi

    val userFactory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel(UserRepository(userApi)) as T
        }
    }

    val reviewsFactory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return ReviewsViewModel(ReviewRepository(reviewApi)) as T
        }
    }

    val mapsFactory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return MapsViewModel(ObstacleRepository(obstacleApi)) as T
        }
    }

    val helpFactory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return VolunteerViewModel(VolunteerRepository(volunteerApi)) as T
        }
    }

    val rewardsFactory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return RewardsViewModel(RewardsRepository(rewardsApi)) as T
        }
    }

    val tasksFactory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return TasksViewModel(TasksRepository(tasksApi)) as T
        }
    }

    val rewardsViewModel: RewardsViewModel = viewModel(factory = rewardsFactory)
    val tasksViewModel: TasksViewModel = viewModel(factory = tasksFactory)
    val userViewModel: UserViewModel = viewModel(factory = userFactory)
    val reviewsViewModel: ReviewsViewModel = viewModel(factory = reviewsFactory)
    val mapsViewModel: MapsViewModel = viewModel(factory = mapsFactory)
    val helpViewModel: VolunteerViewModel = viewModel(factory = helpFactory)

    var currentTab by rememberSaveable { mutableStateOf(BottomTab.MAP) }
    var overlayScreen by remember { mutableStateOf(OverlayScreen.NONE) }
    var selectedReview by remember { mutableStateOf<ReviewCardResp?>(null) }
    var selectedReward by remember { mutableStateOf<RewardOffer?>(null) }
    var selectedTask by remember { mutableStateOf<TaskViewDto?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = currentTab == BottomTab.MAP,
                    onClick = {
                        currentTab = BottomTab.MAP
                        overlayScreen = OverlayScreen.NONE
                    },
                    icon = { Icon(Icons.Default.Map, null) },
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
                    selected = currentTab == BottomTab.HELP,
                    onClick = {
                        currentTab = BottomTab.HELP
                        overlayScreen = OverlayScreen.NONE
                    },
                    icon = { Icon(Icons.Default.VolunteerActivism, null) },
                    label = { Text("Помощь") }
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

            if (overlayScreen == OverlayScreen.NONE) {

                when (currentTab) {

                    BottomTab.MAP -> MapRouteScreen()

                    BottomTab.REVIEWS -> UserReviewsScreen(
                        reviewsViewModel = reviewsViewModel,
                        onAddReview = {
                            selectedReview = null
                            overlayScreen = OverlayScreen.REVIEW_FORM
                        },
                        onOpenDetails = { review ->
                            selectedReview = review
                            overlayScreen = OverlayScreen.REVIEW_DETAILS
                        },
                        onEditReview = { review ->
                            selectedReview = review
                            overlayScreen = OverlayScreen.REVIEW_FORM
                        }
                    )

                    BottomTab.HELP -> VolunteerScreen(
                        helpViewModel = helpViewModel,
                        onCreateRequest = {
                            overlayScreen = OverlayScreen.HELP_CREATE
                        },
                        onMyRequests = {
                            overlayScreen = OverlayScreen.HELP_MY_REQUESTS
                        },
                        onVolunteerFeed = {
                            overlayScreen = OverlayScreen.VOLUNTEER_FEED
                        },
                        onMyWards = {
                            overlayScreen = OverlayScreen.VOLUNTEER_WARDS
                        }
                    )

                    BottomTab.PROFILE -> UserProfileScreen(
                        userViewModel = userViewModel,
                        onEdit = { overlayScreen = OverlayScreen.EDIT_PROFILE },
                        onDelete = { overlayScreen = OverlayScreen.DELETE_PROFILE },
                        onLogout = onLogout,
                        onSelectObstacles = {
                            overlayScreen = OverlayScreen.OBSTACLES
                        },
                        onBecomeVolunteer = {
                            overlayScreen = OverlayScreen.VOLUNTEER_APPLICATION
                        },
                        onNavigateToRewards = {
                            overlayScreen = OverlayScreen.REWARDS_SHOP
                        },
                        onNavigateToTasks = {
                            overlayScreen = OverlayScreen.TASKS_SHOP
                        }
                    )
                }
            }

            when (overlayScreen) {

                OverlayScreen.EDIT_PROFILE -> UserEditScreen(
                    userViewModel = userViewModel,
                    onBack = { overlayScreen = OverlayScreen.NONE },
                    onLogout = onLogout
                )

                OverlayScreen.DELETE_PROFILE -> UserDeleteAccountScreen(
                    viewModel = userViewModel,
                    onBack = { overlayScreen = OverlayScreen.NONE },
                    onExit = onLogout
                )

                OverlayScreen.REVIEW_FORM -> ReviewFormScreen(
                    reviewsViewModel = reviewsViewModel,
                    initialReview = selectedReview,
                    onBack = { overlayScreen = OverlayScreen.NONE },
                    onSaved = {
                        selectedReview = null
                        overlayScreen = OverlayScreen.NONE
                    }
                )

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

                OverlayScreen.OBSTACLES -> ObstacleSelectScreen(
                    mapsViewModel = mapsViewModel,
                    onBackToProfile = { overlayScreen = OverlayScreen.NONE },
                    onSaved = { overlayScreen = OverlayScreen.NONE }
                )

                OverlayScreen.HELP_CREATE -> HelpRequestCreateScreen(
                    helpViewModel = helpViewModel,
                    onBack = { overlayScreen = OverlayScreen.NONE },
                    onCreated = { overlayScreen = OverlayScreen.NONE }
                )

                OverlayScreen.HELP_MY_REQUESTS -> UserHelpRequestsScreen(
                    viewModel = helpViewModel
                )

                OverlayScreen.VOLUNTEER_APPLICATION -> VolunteerApplicationFormScreen(
                    viewModel = helpViewModel,
                    onBack = { overlayScreen = OverlayScreen.NONE },
                    onSubmitted = { overlayScreen = OverlayScreen.NONE }
                )

                OverlayScreen.VOLUNTEER_FEED -> VolunteerFeedScreen(
                    onBack = { overlayScreen = OverlayScreen.NONE }
                )

                OverlayScreen.VOLUNTEER_WARDS -> VolunteerWardsScreen(
                    viewModel = helpViewModel,
                    onBack = {
                        overlayScreen = OverlayScreen.NONE
                    }
                )

                OverlayScreen.REWARDS_SHOP -> RewardsShopScreen(
                    viewModel = rewardsViewModel,
                    onRewardClick = { reward: RewardOffer ->
                        selectedReward = reward
                        overlayScreen = OverlayScreen.REWARD_DETAIL
                    },
                    onNavigateToHistory = { overlayScreen = OverlayScreen.REWARDS_HISTORY },
                    onNavigateToLeaderboard = { overlayScreen = OverlayScreen.LEADERBOARD },
                    onBack = { overlayScreen = OverlayScreen.NONE }
                )

                OverlayScreen.REWARD_DETAIL -> {
                    val reward = selectedReward
                    if (reward != null) {
                        RewardDetailScreen(
                            viewModel = rewardsViewModel,
                            reward = reward,
                            onPurchaseComplete = {
                                selectedReward = null
                                overlayScreen = OverlayScreen.REWARDS_SHOP
                            },
                            onBack = {
                                selectedReward = null
                                overlayScreen = OverlayScreen.REWARDS_SHOP
                            }
                        )
                    } else {
                        overlayScreen = OverlayScreen.NONE
                    }
                }

                OverlayScreen.REWARDS_HISTORY -> RewardsHistoryScreen(
                    viewModel = rewardsViewModel,
                    onBack = { overlayScreen = OverlayScreen.REWARDS_SHOP }
                )

                OverlayScreen.LEADERBOARD -> LeaderboardScreen(
                    viewModel = rewardsViewModel,
                    onBack = { overlayScreen = OverlayScreen.REWARDS_SHOP }
                )

                OverlayScreen.TASKS_SHOP -> TasksScreen(
                    viewModel = tasksViewModel,
                    onTaskClick = { task: TaskViewDto ->
                        selectedTask = task
                        overlayScreen = OverlayScreen.TASK_DETAIL
                    },
                    onBack = { overlayScreen = OverlayScreen.NONE }
                )

                OverlayScreen.TASK_DETAIL -> {
                    val task = selectedTask
                    if (task != null) {
                        TaskExecutionScreen(
                            task = task,
                            onTargetComplete = { target ->
                                tasksViewModel.completeTarget(task.id, target.id)
                            },
                            onComplete = {
                                selectedTask = null
                                overlayScreen = OverlayScreen.TASKS_SHOP
                                tasksViewModel.loadTasks()
                            },
                            onBack = {
                                selectedTask = null
                                overlayScreen = OverlayScreen.TASKS_SHOP
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