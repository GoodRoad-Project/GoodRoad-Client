package com.example.goodroad.modules.auth.navigation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.goodroad.modules.moderationReview.data.ModerationReviewRepository
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.modules.auth.presentation.AuthViewModel
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
import com.example.goodroad.modules.moderator.screens.VolunteerManagementScreen
import com.example.goodroad.modules.moderationReview.screens.ReviewModerationScreen
import com.example.goodroad.modules.moderator.data.VolunteerModerationRepository
import com.example.goodroad.modules.moderator.presentation.VolunteerModerationViewModel

@Composable
fun AuthApp(
    navController: NavHostController = rememberNavController()
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("AuthApp", "Application started")
        val token = ApiClient.getCurrentToken()
        if (token != null) {
            Log.d("AuthApp", "Token found on startup: ${token.take(50)}...")
        } else {
            Log.d("AuthApp", "No token found on startup")
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        val startDest = remember {
            val token = ApiClient.getCurrentToken()
            if (token != null) {
                val role = getRoleFromToken()
                when (role) {
                    "MODERATOR_ADMIN" -> "admin_home"
                    "MODERATOR" -> "moderator_home"
                    else -> USER_HOME_ROUTE
                }
            } else {
                LOGIN_ROUTE
            }
        }

        NavHost(
            navController = navController,
            startDestination = startDest
        ) {

            composable(LOGIN_ROUTE) {
                val context = LocalContext.current
                val authViewModel: AuthViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return AuthViewModel(context) as T
                        }
                    }
                )
                LoginScreen(
                    viewModel = authViewModel,
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
                val context = LocalContext.current
                val authViewModel: AuthViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return AuthViewModel(context) as T
                        }
                    }
                )
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate(USER_HOME_ROUTE) {
                            popUpTo(LOGIN_ROUTE) { inclusive = true }
                        }
                    },
                    onLogin = { navController.popBackStack() }
                )
            }

            composable(RECOVER_ROUTE) {
                val context = LocalContext.current
                val authViewModel: AuthViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return AuthViewModel(context) as T
                        }
                    }
                )
                RecoverPasswordScreen(
                    viewModel = authViewModel,
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
                    onVolunteers = {
                        navController.navigate("admin_volunteers")
                    },
                    onLogout = {
                        ApiClient.logout()
                        navController.navigate(LOGIN_ROUTE) {
                            popUpTo("admin_home") { inclusive = true }
                        }
                    }
                )
            }

            composable("admin_volunteers") {

                val volunteerModerationViewModel: VolunteerModerationViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return VolunteerModerationViewModel(
                                VolunteerModerationRepository()
                            ) as T
                        }
                    }
                )

                VolunteerManagementScreen(
                    viewModel = volunteerModerationViewModel,
                    onBack = { navController.popBackStack() }
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
                    onVolunteers = { navController.navigate("volunteers") },
                    onLogout = {
                        ApiClient.logout()
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

            composable("volunteers") {

                val volunteerModerationViewModel: VolunteerModerationViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return VolunteerModerationViewModel(
                                VolunteerModerationRepository()
                            ) as T
                        }
                    }
                )

                VolunteerManagementScreen(
                    viewModel = volunteerModerationViewModel,
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

fun getRoleFromToken(): String? {
    val token = ApiClient.getCurrentToken() ?: return null
    return try {
        val chunks = token.split(".")
        if (chunks.size != 3) return null
        val payload = String(android.util.Base64.decode(chunks[1], android.util.Base64.URL_SAFE))
        val json = org.json.JSONObject(payload)
        json.getString("role")
    } catch (e: Exception) {
        null
    }
}