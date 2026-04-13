package com.example.goodroad.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.auth.AuthButton
import com.example.goodroad.ui.auth.AuthStatusText
import com.example.goodroad.ui.auth.PasswordField
import com.example.goodroad.ui.theme.*
import com.example.goodroad.ui.viewmodel.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun UserDeleteAccountScreen(
    viewModel: UserViewModel,
    onExit: () -> Unit,
    onBack: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            delay(5_000)
            viewModel.clearMessages()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        UserDecor()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Удаление аккаунта",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(20.dp))

        PasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Пароль"
        )

        Spacer(modifier = Modifier.height(8.dp))

        AuthStatusText(text = errorMessage)

        Spacer(modifier = Modifier.height(20.dp))

        AuthButton(
            text = "Удалить аккаунт",
            enabled = !isLoading
        ) {
            viewModel.deleteUser(password) {
                onExit()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AuthButton(
            text = "Назад в профиль",
            backgroundColor = UrbanBrown,
            contentColor = WhiteSoft,
            onClick = {
                viewModel.clearMessages()
                onBack()
            }
        )
    }
}