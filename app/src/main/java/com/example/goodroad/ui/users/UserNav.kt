package com.example.goodroad.ui.users

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.user.UserRepository
import com.example.goodroad.ui.maps.MapsNav
import com.example.goodroad.ui.users.users.*
import com.example.goodroad.ui.viewmodel.UserViewModel

@Composable
fun UserNav(onLogout: () -> Unit) {

    val userViewModel: UserViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                    return UserViewModel(
                        UserRepository(ApiClient.userApi)
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    LaunchedEffect(Unit) {
        userViewModel.getCurrentUser()
    }

    val user by userViewModel.user
    val isLoading by userViewModel.isLoading

    if (isLoading || user == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val u = user!!

    when (u.role?.uppercase()) {

        "ADMIN", "MODERATOR", "MODERATOR_ADMIN" -> {
            Text("Admin in wrong flow")
        }

        else -> {
            UserProfileScreen(
                userViewModel = userViewModel,
                onEdit = {},
                onDelete = {},
                onLogout = onLogout,
                onSelectObstacles = {}
            )
        }
    }
}