package com.example.goodroad.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.auth.AuthButton
import com.example.goodroad.ui.theme.*
import com.example.goodroad.ui.viewmodel.UserViewModel

@Composable
fun UserProfileScreen(
    userViewModel: UserViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onLogout: () -> Unit
) {
    val user by userViewModel.user
    val isLoading by userViewModel.isLoading
    val errorMessage by userViewModel.errorMessage

    LaunchedEffect(userViewModel) {
        if (user == null && !userViewModel.isDeleted) {
            userViewModel.getCurrentUser()
        }
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        errorMessage != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ошибка: $errorMessage", color = Color.Red)
            }
        }
        user != null -> {
            val u = user!!
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = BackgroundLight
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    UserDecor()
                    Text(
                        "Профиль",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(20.dp))
                    UserInfoBlock("Имя", u.firstName ?: "")
                    UserInfoBlock("Фамилия", u.lastName ?: "")
                    UserInfoBlock("Роль", u.role ?: "")
                    Spacer(Modifier.height(20.dp))
                    AuthButton(text = "Редактировать", onClick = onEdit)
                    Spacer(Modifier.height(10.dp))
                    AuthButton(text = "Удалить аккаунт") {
                        onDelete()
                    }
                    Spacer(Modifier.height(10.dp))
                    AuthButton(text = "Выйти") {
                        userViewModel.logout {
                            onLogout()
                        }
                    }
                }
            }
        }
        else -> {
            LaunchedEffect(Unit) {
                if (userViewModel.isDeleted) {
                    onLogout()
                }
            }
        }
    }
}