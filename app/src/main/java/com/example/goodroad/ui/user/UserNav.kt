package com.example.goodroad.ui.user

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.repository.UserRepository
import com.example.goodroad.ui.viewmodel.UserViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.Composable

@Composable
fun UserNav(onLogout: () -> Unit) {
    val api = ApiClient.userApi
    val factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(UserRepository(api)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val userViewModel: UserViewModel = viewModel(factory = factory)

    var screen by remember { mutableStateOf("profile") }

    when (screen) {
        "profile" -> UserProfileScreen(
            userViewModel = userViewModel,
            onEdit = { screen = "edit" },
            onDelete = { screen = "delete" },
            onLogout = onLogout
        )

        "edit" -> UserEditScreen(
            userViewModel = userViewModel,
            onBack = { screen = "profile" }
        )

        "delete" -> UserDeleteAccountScreen(
            onDelete = {
                userViewModel.logout()
                onLogout()
            },
            onExit = { screen = "profile" }
        )
    }
}