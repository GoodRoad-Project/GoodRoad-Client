package com.example.goodroad.ui.auth

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.ui.Modifier
import com.example.goodroad.data.auth.AuthResp
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.user.UserRepository
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.users.UserNav
import com.example.goodroad.ui.users.moderators.AdminProfileScreen
import com.example.goodroad.ui.viewmodel.UserViewModel

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
                    onLoginSuccess = { resp: AuthResp ->

                        val role = resp.user?.role

                        val isAdmin = when (role) {
                            "ADMIN",
                            "MODERATOR",
                            "MODERATOR_ADMIN" -> true
                            else -> false
                        }

                        if (isAdmin) {
                            navController.navigate("admin_home") {
                                popUpTo(LOGIN_ROUTE) { inclusive = true }
                            }
                        } else {
                            navController.navigate(USER_HOME_ROUTE) {
                                popUpTo(LOGIN_ROUTE) { inclusive = true }
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
                    onLogout = {
                        navController.navigate(LOGIN_ROUTE) {
                            popUpTo(USER_HOME_ROUTE) { inclusive = true }
                        }
                    }
                )
            }

            composable("admin_home") {

                val userViewModel: UserViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {

                            val repository = UserRepository(ApiClient.userApi)

                            return UserViewModel(repository) as T
                        }
                    }
                )

                AdminProfileScreen(
                    userViewModel = userViewModel,
                    onModerators = {},
                    onReviews = {},
                    onLogout = {
                        navController.navigate(LOGIN_ROUTE) {
                            popUpTo("admin_home") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}