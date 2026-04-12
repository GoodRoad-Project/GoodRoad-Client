package com.example.goodroad.ui.auth

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.user.UserNav
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
                    onLoginSuccess = { role ->
                        navController.navigate(homeRoute(role)) {
                            popUpTo(LOGIN_ROUTE) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onSignUp = {
                        navController.navigate(REGISTER_ROUTE)
                    },
                    onForgotPassword = {
                        navController.navigate(RECOVER_ROUTE)
                    }
                )
            }

            composable(REGISTER_ROUTE) {
                RegisterScreen(
                    onRegisterSuccess = { role ->
                        navController.navigate(homeRoute(role)) {
                            popUpTo(LOGIN_ROUTE) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(RECOVER_ROUTE) {
                RecoverPasswordScreen(
                    onLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(USER_HOME_ROUTE) {
                UserNav(
                    onLogout = {
                        navController.navigate(LOGIN_ROUTE) {
                            popUpTo(USER_HOME_ROUTE) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(MODERATOR_HOME_ROUTE) {
                RoleStubScreen(
                    title = "Главный экран модератора",
                    onLogout = {
                        navController.navigate(LOGIN_ROUTE) {
                            popUpTo(MODERATOR_HOME_ROUTE) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}