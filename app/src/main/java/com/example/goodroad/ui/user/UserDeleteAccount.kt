package com.example.goodroad.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.auth.AuthButton
import com.example.goodroad.ui.auth.PasswordField
import com.example.goodroad.ui.theme.*
import com.example.goodroad.ui.viewmodel.UserViewModel

@Composable
fun UserDeleteAccountScreen(
    viewModel: UserViewModel,
    onExit: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

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
            text = "Выйти",
            backgroundColor = GrayButton,
            contentColor = WhiteSoft,
            enabled = !isLoading
        ) {
            viewModel.logout {
                onExit()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}