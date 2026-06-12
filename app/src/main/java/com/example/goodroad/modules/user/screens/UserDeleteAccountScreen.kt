package com.example.goodroad.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.buttons.*
import com.example.goodroad.ui.AuthStatusText
import com.example.goodroad.ui.fields.*
import com.example.goodroad.ui.theme.*
import com.example.goodroad.modules.user.presentation.UserViewModel
import kotlinx.coroutines.delay
import com.example.goodroad.ui.UserDecor

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

        PrimaryButton(
            text = "Удалить аккаунт",
            backgroundColor = AlertRed,
            contentColor = AlertRed,
            enabled = !isLoading
        ) {
            viewModel.deleteUser(password) {
                onExit()
            }
        }
    }
}