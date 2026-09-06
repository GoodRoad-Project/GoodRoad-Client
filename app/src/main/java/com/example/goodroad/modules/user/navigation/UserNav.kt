package com.example.goodroad.modules.user.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.example.goodroad.modules.tasks.screens.CompletedTasksHistoryScreen
import com.example.goodroad.modules.tasks.screens.TaskExecutionScreen
import com.example.goodroad.modules.rewards.screens.CouponsScreen
import com.example.goodroad.modules.tasks.data.TargetViewDto
import com.example.goodroad.ui.theme.*
import com.example.goodroad.ui.user.SecurityScreen
import com.example.goodroad.ui.user.ChangePasswordScreen
import com.example.goodroad.ui.user.ChangePhoneScreen

enum class BottomTab {
    MAP,
    REVIEWS,
    HELP,
    PROFILE
}

enum class OverlayScreen {
    EDIT_PROFILE,
    DELETE_PROFILE,
    SECURITY,
    CHANGE_PASSWORD,
    CHANGE_PHONE,
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
    TASK_DETAIL,
    TASKS_HISTORY,
    COUPONS,
    REVIEW_FORM_FROM_TASK,
}

@Composable
fun UserNav(
    navController: NavHostController,
    onLogout: () -> Unit,
    onNavigateToReview: (String, Double, Double) -> Unit = { _, _, _ -> }
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

    var currentTab by remember { mutableStateOf(BottomTab.MAP) }
    val overlayStack = remember { mutableStateListOf<OverlayScreen>() }

    var selectedReview by remember { mutableStateOf<ReviewCardResp?>(null) }
    var selectedReward by remember { mutableStateOf<RewardOffer?>(null) }
    var selectedTask by remember { mutableStateOf<TaskViewDto?>(null) }
    var selectedTaskTarget by remember { mutableStateOf<TargetViewDto?>(null) }

    val overlayScreen = overlayStack.lastOrNull()

    fun navigateTo(screen: OverlayScreen) {
        overlayStack.add(screen)
    }

    fun clearStack() {
        overlayStack.clear()
        selectedReview = null
        selectedReward = null
        selectedTask = null
        selectedTaskTarget = null
    }

    fun goBack() {
        if (overlayStack.isNotEmpty()) {
            overlayStack.removeAt(overlayStack.lastIndex)

            if (overlayStack.isEmpty()) {
                selectedReview = null
                selectedReward = null
                selectedTask = null
                selectedTaskTarget = null
            }
        }
    }

    BackHandler(
        enabled = overlayStack.isNotEmpty()
    ) {
        goBack()
    }

    Scaffold(
        bottomBar = {
            Column {
                Divider(
                    color = BorderWarm,
                    thickness = 0.5.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                NavigationBar(
                    containerColor = BackgroundLight,
                    contentColor = UrbanBrown,
                    tonalElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NavigationBarItem(
                        selected = currentTab == BottomTab.MAP,
                        onClick = {
                            currentTab = BottomTab.MAP
                            clearStack()
                        },
                        icon = { Icon(Icons.Default.Map, null) },
                        label = { Text("Карта") }
                    )

                    NavigationBarItem(
                        selected = currentTab == BottomTab.REVIEWS,
                        onClick = {
                            currentTab = BottomTab.REVIEWS
                            clearStack()
                        },
                        icon = { Icon(Icons.Default.Star, null) },
                        label = { Text("Отзывы") }
                    )

                    NavigationBarItem(
                        selected = currentTab == BottomTab.HELP,
                        onClick = {
                            currentTab = BottomTab.HELP
                            clearStack()
                        },
                        icon = { Icon(Icons.Default.VolunteerActivism, null) },
                        label = { Text("Помощь") }
                    )

                    NavigationBarItem(
                        selected = currentTab == BottomTab.PROFILE,
                        onClick = {
                            currentTab = BottomTab.PROFILE
                            clearStack()
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
            if (overlayScreen == null) {
                when (currentTab) {
                    BottomTab.MAP -> MapRouteScreen(
                        onNavigateToReview = onNavigateToReview
                    )

                    BottomTab.REVIEWS -> UserReviewsScreen(
                        reviewsViewModel = reviewsViewModel,
                        onAddReview = {
                            selectedReview = null
                            navigateTo(OverlayScreen.REVIEW_FORM)
                        },
                        onOpenDetails = { review ->
                            selectedReview = review
                            navigateTo(OverlayScreen.REVIEW_DETAILS)
                        },
                        onEditReview = { review ->
                            selectedReview = review
                            navigateTo(OverlayScreen.REVIEW_FORM)
                        }
                    )

                    BottomTab.HELP -> VolunteerScreen(
                        helpViewModel = helpViewModel,
                        onCreateRequest = {
                            navigateTo(OverlayScreen.HELP_CREATE)
                        },
                        onMyRequests = {
                            navigateTo(OverlayScreen.HELP_MY_REQUESTS)
                        },
                        onVolunteerFeed = {
                            navigateTo(OverlayScreen.VOLUNTEER_FEED)
                        },
                        onMyWards = {
                            navigateTo(OverlayScreen.VOLUNTEER_WARDS)
                        }
                    )

                    BottomTab.PROFILE -> UserProfileScreen(
                        userViewModel = userViewModel,
                        onEdit = {
                            navigateTo(OverlayScreen.EDIT_PROFILE)
                        },
                        onDelete = {
                            navigateTo(OverlayScreen.DELETE_PROFILE)
                        },
                        onLogout = onLogout,
                        onSelectObstacles = {
                            navigateTo(OverlayScreen.OBSTACLES)
                        },
                        onSecurity = {
                            navigateTo(OverlayScreen.SECURITY)
                        },
                        onBecomeVolunteer = {
                            navigateTo(OverlayScreen.VOLUNTEER_APPLICATION)
                        },
                        onNavigateToRewards = {
                            navigateTo(OverlayScreen.REWARDS_SHOP)
                        },
                        onNavigateToTasks = {
                            navigateTo(OverlayScreen.TASKS_SHOP)
                        }
                    )
                }
            }

            when (overlayScreen) {
                OverlayScreen.EDIT_PROFILE -> UserEditScreen(
                    userViewModel = userViewModel,
                    onBack = { goBack() },
                    onLogout = onLogout
                )

                OverlayScreen.DELETE_PROFILE -> UserDeleteAccountScreen(
                    viewModel = userViewModel,
                    onBack = { goBack() },
                    onExit = onLogout
                )

                OverlayScreen.SECURITY -> SecurityScreen(
                    onBack = { goBack() },
                    onChangePassword = {
                        navigateTo(OverlayScreen.CHANGE_PASSWORD)
                    },
                    onChangePhone = {
                        navigateTo(OverlayScreen.CHANGE_PHONE)
                    }
                )

                OverlayScreen.CHANGE_PASSWORD -> ChangePasswordScreen(
                    userViewModel = userViewModel,
                    onBack = { goBack() }
                )

                OverlayScreen.CHANGE_PHONE -> ChangePhoneScreen(
                    userViewModel = userViewModel,
                    onBack = { goBack() }
                )

                OverlayScreen.REVIEW_FORM -> ReviewFormScreen(
                    reviewsViewModel = reviewsViewModel,
                    initialReview = selectedReview,
                    onBack = { goBack() },
                    onSaved = {
                        selectedReview = null
                        clearStack()
                    }
                )

                OverlayScreen.REVIEW_DETAILS -> {
                    val review = selectedReview

                    if (review != null) {
                        ReviewDetailsScreen(
                            review = review,
                            reviewsViewModel = reviewsViewModel,
                            onBack = { goBack() },
                            onEdit = {
                                navigateTo(OverlayScreen.REVIEW_FORM)
                            },
                            onDeleted = {
                                selectedReview = null
                                clearStack()
                            }
                        )
                    } else {
                        clearStack()
                    }
                }

                OverlayScreen.COUPONS -> CouponsScreen(
                    viewModel = rewardsViewModel,
                    onBack = { goBack() }
                )

                OverlayScreen.OBSTACLES -> ObstacleSelectScreen(
                    mapsViewModel = mapsViewModel,
                    onBackToProfile = { goBack() },
                    onSaved = { goBack() }
                )

                OverlayScreen.HELP_CREATE -> HelpRequestCreateScreen(
                    helpViewModel = helpViewModel,
                    onCreated = { goBack() }
                )

                OverlayScreen.HELP_MY_REQUESTS -> UserHelpRequestsScreen(
                    viewModel = helpViewModel
                )

                OverlayScreen.VOLUNTEER_APPLICATION -> VolunteerApplicationFormScreen(
                    viewModel = helpViewModel,
                    onBack = { goBack() },
                    onSubmitted = { goBack() }
                )

                OverlayScreen.VOLUNTEER_FEED -> VolunteerFeedScreen(
                    onBack = { goBack() }
                )

                OverlayScreen.VOLUNTEER_WARDS -> VolunteerWardsScreen(
                    viewModel = helpViewModel,
                    onBack = { goBack() }
                )

                OverlayScreen.REWARDS_SHOP -> RewardsShopScreen(
                    viewModel = rewardsViewModel,
                    onRewardClick = { reward ->
                        selectedReward = reward
                        navigateTo(OverlayScreen.REWARD_DETAIL)
                    },
                    onNavigateToHistory = {
                        navigateTo(OverlayScreen.REWARDS_HISTORY)
                    },
                    onNavigateToLeaderboard = {
                        navigateTo(OverlayScreen.LEADERBOARD)
                    },
                    onNavigateToCoupons = {
                        navigateTo(OverlayScreen.COUPONS)
                    },
                    onBack = { goBack() }
                )

                OverlayScreen.REWARD_DETAIL -> {
                    val reward = selectedReward

                    if (reward != null) {
                        RewardDetailScreen(
                            viewModel = rewardsViewModel,
                            reward = reward,
                            onPurchaseComplete = {
                                selectedReward = null
                                goBack()
                            },
                            onBack = {
                                selectedReward = null
                                goBack()
                            }
                        )
                    } else {
                        goBack()
                    }
                }

                OverlayScreen.REVIEW_FORM_FROM_TASK -> {
                    val target = selectedTaskTarget

                    if (target != null) {
                        ReviewFormScreen(
                            reviewsViewModel = reviewsViewModel,
                            initialReview = null,
                            initialPlaceName = target.title,
                            initialLatitude = target.latitude?.toString() ?: "",
                            initialLongitude = target.longitude?.toString() ?: "",
                            isLocationLocked = true,
                            featureId = target.targetId.toLong(),
                            onBack = {
                                selectedTaskTarget = null
                                goBack()
                            },
                            onSaved = {
                                selectedTaskTarget = null
                                goBack()
                            }
                        )
                    } else {
                        goBack()
                    }
                }

                OverlayScreen.REWARDS_HISTORY -> RewardsHistoryScreen(
                    viewModel = rewardsViewModel,
                    onBack = { goBack() }
                )

                OverlayScreen.LEADERBOARD -> LeaderboardScreen(
                    viewModel = rewardsViewModel,
                    onBack = { goBack() }
                )

                OverlayScreen.TASKS_SHOP -> TasksScreen(
                    viewModel = tasksViewModel,
                    onTaskClick = { task ->
                        selectedTask = task
                        navigateTo(OverlayScreen.TASK_DETAIL)
                    },
                    onBack = { goBack() },
                    onHistoryClick = {
                        navigateTo(OverlayScreen.TASKS_HISTORY)
                    }
                )

                OverlayScreen.TASKS_HISTORY -> CompletedTasksHistoryScreen(
                    viewModel = tasksViewModel,
                    onBack = { goBack() }
                )

                OverlayScreen.TASK_DETAIL -> {
                    val task = selectedTask

                    if (task != null) {
                        TaskExecutionScreen(
                            task = task,
                            onTargetClick = { target ->
                                selectedTaskTarget = target
                                navigateTo(OverlayScreen.REVIEW_FORM_FROM_TASK)
                            },
                            onBack = {
                                selectedTask = null
                                selectedTaskTarget = null
                                goBack()
                            }
                        )
                    } else {
                        goBack()
                    }
                }

                null -> Unit
            }
        }
    }
}