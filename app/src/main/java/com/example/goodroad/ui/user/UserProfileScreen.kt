package com.example.goodroad.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.auth.AuthButton
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.viewmodel.UserViewModel

@Composable
fun UserProfileScreen(
    userViewModel: UserViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onLogout: () -> Unit
) {
    LaunchedEffect(Unit) {
        userViewModel.getCurrentUser()
    }

    when {
        userViewModel.isLoading.value -> {
            Text("Загрузка...", color = TextPrimary)
        }
        userViewModel.errorMessage.value != null -> {
            Text("Ошибка: ${userViewModel.errorMessage.value}", color = Color.Red)
        }
        userViewModel.user.value != null -> {
            val user = userViewModel.user.value!!
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
                    UserInfoBlock("Имя", user.firstName ?: "")
                    UserInfoBlock("Фамилия", user.lastName ?: "")
                    UserInfoBlock("Роль", user.role ?: "")
                    Spacer(Modifier.height(20.dp))
                    AuthButton(text = "Редактировать", onClick = onEdit)
                    Spacer(Modifier.height(10.dp))
                    AuthButton(text = "Удалить аккаунт", onClick = onDelete)
                    Spacer(Modifier.height(10.dp))
                    AuthButton(text = "Выйти", onClick = onLogout)
                }
            }
        }
    }
}