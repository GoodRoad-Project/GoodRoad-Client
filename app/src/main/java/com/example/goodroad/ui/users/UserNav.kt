package com.example.goodroad.ui.users

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.user.UserRepository
import com.example.goodroad.ui.maps.MapsNav
import com.example.goodroad.ui.users.moderators.AdminProfileScreen
import com.example.goodroad.ui.users.users.UserDeleteAccountScreen
import com.example.goodroad.ui.users.users.UserEditScreen
import com.example.goodroad.ui.users.users.UserProfileScreen
import com.example.goodroad.ui.viewmodel.UserViewModel

@Composable
fun UserNav(onLogout: () -> Unit) {

    val api = ApiClient.userApi

    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(UserRepository(api)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val userViewModel: UserViewModel = viewModel(factory = factory)

    var screen by remember { mutableStateOf("profile") }

    LaunchedEffect(Unit) {
        userViewModel.getCurrentUser()
    }

    when (screen) {

        "profile" -> {

            val user by userViewModel.user
            val isLoading by userViewModel.isLoading

            when {
                isLoading || user == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                user!!.role.equals("ADMIN", ignoreCase = true) -> {
                    AdminProfileScreen(
                        userViewModel = userViewModel,
                        onModerators = { screen = "moderators" },
                        onReviews = { screen = "reviews" },
                        onLogout = onLogout
                    )
                }

                else -> {
                    UserProfileScreen(
                        userViewModel = userViewModel,
                        onEdit = { screen = "edit" },
                        onDelete = { screen = "delete" },
                        onLogout = onLogout,
                        onSelectObstacles = { screen = "obstacles" }
                    )
                }
            }
        }

        "edit" -> UserEditScreen(
            userViewModel = userViewModel,
            onBack = { screen = "profile" },
            onLogout = onLogout
        )

        "delete" -> UserDeleteAccountScreen(
            viewModel = userViewModel,
            onBack = { screen = "profile" },
            onExit = onLogout
        )

        "obstacles" -> MapsNav(
            onBackToProfile = { screen = "profile" },
            onSaveObstacles = { screen = "profile" }
        )

        "moderators" -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Экран модераторов")
                Spacer(Modifier.height(16.dp))
                Button(onClick = { screen = "profile" }) {
                    Text("Назад")
                }
            }
        }

        "reviews" -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Экран отзывов")
                Spacer(Modifier.height(16.dp))
                Button(onClick = { screen = "profile" }) {
                    Text("Назад")
                }
            }
        }
    }
}