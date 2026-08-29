package com.example.goodroad.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.user.presentation.UserViewModel
import com.example.goodroad.ui.AuthStatusText
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.fields.PasswordField
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown

@Composable
fun ChangePasswordScreen(
    userViewModel: UserViewModel,
    onBack: () -> Unit
) {
    var oldPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }

    val isLoading by userViewModel.isLoading
    val serverError by userViewModel.errorMessage

    LaunchedEffect(Unit) {
        userViewModel.clearMessages()
        localError = null
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Сменить пароль",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onBack,
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = UrbanBrown.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            PasswordField(
                value = oldPassword,
                onValueChange = {
                    oldPassword = it
                    localError = null
                    userViewModel.clearMessages()
                },
                label = "Старый пароль"
            )

            Spacer(Modifier.height(12.dp))

            PasswordField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    localError = null
                    userViewModel.clearMessages()
                },
                label = "Новый пароль"
            )

            Spacer(Modifier.height(12.dp))

            PasswordField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    localError = null
                    userViewModel.clearMessages()
                },
                label = "Подтвердите новый пароль"
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Введите текущий пароль, затем новый пароль и повторите его.",
                style = MaterialTheme.typography.bodySmall,
                color = UrbanBrown
            )

            Spacer(Modifier.height(16.dp))

            AuthStatusText(
                text = localError ?: serverError,
                onTimeout = {
                    localError = null
                    userViewModel.clearMessages()
                }
            )

            Spacer(Modifier.height(16.dp))

            PrimaryButton(
                text = if (isLoading) {
                    "Меняем..."
                } else {
                    "Сменить пароль"
                },
                enabled = !isLoading
            ) {
                when {
                    oldPassword.isBlank() -> {
                        localError = "Введите текущий пароль"
                    }

                    newPassword.isBlank() -> {
                        localError = "Введите новый пароль"
                    }

                    confirmPassword.isBlank() -> {
                        localError = "Подтвердите новый пароль"
                    }

                    newPassword != confirmPassword -> {
                        localError = "Пароли не совпадают"
                    }

                    else -> {
                        localError = null

                        userViewModel.changePassword(
                            oldPassword = oldPassword,
                            newPassword = newPassword,
                            onSuccess = onBack
                        )
                    }
                }
            }
        }
    }
}