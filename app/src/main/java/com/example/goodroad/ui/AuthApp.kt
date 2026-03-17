package com.example.goodroad.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private const val LOGIN_ROUTE = "login"
private const val REGISTER_ROUTE = "register"
private const val RECOVER_ROUTE = "recover"
private const val HOME_ROUTE = "home"

@Composable
fun AuthApp(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = LOGIN_ROUTE) {
        composable(LOGIN_ROUTE) {
            PlaceholderScreen(
                title = "GoodRoad",
                subtitle = "Урааа, все запустилось! Но пока тут заглушка для входа",
                primaryText = if (com.example.goodroad.BuildConfig.MOCK_AUTH) "Войти локально" else "Продолжить",
                onPrimary = { navController.navigate(HOME_ROUTE) },
                secondaryText = "Регистрация",
                onSecondary = { navController.navigate(REGISTER_ROUTE) },
                tertiaryText = "Восстановление пароля",
                onTertiary = { navController.navigate(RECOVER_ROUTE) }
            )
        }
        composable(REGISTER_ROUTE) {
            PlaceholderScreen(
                title = "Регистрация",
                subtitle = "И снова заглушка",
                primaryText = "Назад ко входу",
                onPrimary = { navController.popBackStack() }
            )
        }
        composable(RECOVER_ROUTE) {
            PlaceholderScreen(
                title = "Восстановление пароля",
                subtitle = "Экран пока не реализован, но вы держитесь",
                primaryText = "Назад ко входу",
                onPrimary = { navController.popBackStack() }
            )
        }
        composable(HOME_ROUTE) {
            PlaceholderScreen(
                title = "GoodRoad",
                subtitle = "Проект успешно стартовал. Ну типа стартовал.",
                primaryText = "Выйти",
                onPrimary = {
                    navController.navigate(LOGIN_ROUTE) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    subtitle: String,
    primaryText: String,
    onPrimary: () -> Unit,
    secondaryText: String? = null,
    onSecondary: (() -> Unit)? = null,
    tertiaryText: String? = null,
    onTertiary: (() -> Unit)? = null
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = subtitle,
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            Button(onClick = onPrimary) {
                Text(primaryText)
            }
            if (secondaryText != null && onSecondary != null) {
                Button(
                    onClick = onSecondary,
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(secondaryText)
                }
            }
            if (tertiaryText != null && onTertiary != null) {
                Button(
                    onClick = onTertiary,
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(tertiaryText)
                }
            }
        }
    }
}
