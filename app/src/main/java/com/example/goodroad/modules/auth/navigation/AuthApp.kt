package com.example.goodroad.modules.auth.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.ui.Modifier
import com.example.goodroad.modules.moderationReview.data.ModerationReviewRepository
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.modules.user.data.UserRepository
import com.example.goodroad.modules.moderator.data.ModeratorRepository
import com.example.goodroad.modules.auth.screens.LoginScreen
import com.example.goodroad.modules.auth.screens.RecoverPasswordScreen
import com.example.goodroad.modules.auth.screens.RegisterScreen
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.modules.user.navigation.UserNav
import com.example.goodroad.modules.user.presentation.UserViewModel
import com.example.goodroad.modules.moderator.presentation.ModeratorViewModel
import com.example.goodroad.modules.moderationReview.presentation.ReviewModerationViewModel
import com.example.goodroad.modules.moderator.screens.AdminProfileScreen
import com.example.goodroad.modules.moderator.screens.ModeratorProfileScreen
import com.example.goodroad.modules.moderator.screens.ModeratorsManagementScreen
import com.example.goodroad.modules.moderationReview.screens.ReviewModerationScreen

@Composable
fun AuthApp(
    navController: NavHostController = rememberNavController()
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        NavHost(
            navController = navController,
            startDestination = LOGIN_ROUTE
        ) {
            composable(LOGIN_ROUTE) {
                LoginScreen(
                    onLoginSuccess = { resp ->
                        when (resp.user?.role) {
                            "MODERATOR_ADMIN" -> {
                                navController.navigate("admin_home") {
                                    popUpTo(LOGIN_ROUTE) { inclusive = true }
                                }
                            }

                            "MODERATOR" -> {
                                navController.navigate("moderator_home") {
                                    popUpTo(LOGIN_ROUTE) { inclusive = true }
                                }
                            }

                            else -> {
                                navController.navigate(USER_HOME_ROUTE) {
                                    popUpTo(LOGIN_ROUTE) { inclusive = true }
                                }
                            }
                        }
                    },
                    onSignUp = { navController.navigate(REGISTER_ROUTE) },
                    onForgotPassword = { navController.navigate(RECOVER_ROUTE) }
                )
            }

            composable(REGISTER_ROUTE) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(USER_HOME_ROUTE) {
                            popUpTo(LOGIN_ROUTE) { inclusive = true }
                        }
                    },
                    onLogin = { navController.popBackStack() }
                )
            }

            composable(RECOVER_ROUTE) {
                RecoverPasswordScreen(
                    onLogin = { navController.popBackStack() }
                )
            }

            composable(USER_HOME_ROUTE) {
                UserNav(
                    navController = navController,
                    onLogout = {
                        navController.navigate(LOGIN_ROUTE) {
                            popUpTo(USER_HOME_ROUTE) { inclusive = true }
                        }
                    }
                )
            }

            composable("admin_home") {
                val userViewModel: UserViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return UserViewModel(UserRepository(ApiClient.userApi)) as T
                    }
                })

                AdminProfileScreen(
                    userViewModel = userViewModel,
                    onModerators = { navController.navigate("moderators") },
                    onReviews = { navController.navigate("review_moderation") },
                    onLogout = {
                        navController.navigate(LOGIN_ROUTE) {
                            popUpTo("admin_home") { inclusive = true }
                        }
                    }
                )
            }

            composable("moderator_home") {
                val userViewModel: UserViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return UserViewModel(UserRepository(ApiClient.userApi)) as T
                    }
                })

                ModeratorProfileScreen(
                    userViewModel = userViewModel,
                    onReviews = { navController.navigate("review_moderation") },
                    onLogout = {
                        navController.navigate(LOGIN_ROUTE) {
                            popUpTo("moderator_home") { inclusive = true }
                        }
                    }
                )
            }

            composable("moderators") {
                val moderatorViewModel: ModeratorViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return ModeratorViewModel(ModeratorRepository()) as T
                    }
                })

                ModeratorsManagementScreen(
                    viewModel = moderatorViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("review_moderation") {
                val moderationRepository = ModerationReviewRepository(ApiClient.moderationReviewApi)
                val moderationViewModel: ReviewModerationViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return ReviewModerationViewModel(moderationRepository) as T
                    }
                })

                ReviewModerationScreen(
                    viewModel = moderationViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}