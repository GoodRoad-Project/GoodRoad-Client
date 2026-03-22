package com.example.goodroad.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.auth.AuthButton
import com.example.goodroad.ui.auth.PlainField
import com.example.goodroad.ui.theme.GrayButton
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.WhiteSoft


@Composable
fun UserDeleteAccountScreen(
    onDelete: (String) -> Unit,
    onExit: () -> Unit
) {
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(24.dp)) {

        UserDecor()

        Text(
            "Удаление аккаунта",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )

        PlainField(
            value = password,
            onValueChange = { password = it },
            label = "Пароль"
        )

        Spacer(Modifier.height(20.dp))

        AuthButton(
            text = "Удалить аккаунт"
        ) {
            onDelete(password)
        }

        Spacer(Modifier.height(16.dp))

        AuthButton(
            text = "Выйти",
            backgroundColor = GrayButton,
            contentColor = WhiteSoft
        ) {
            onExit()
        }
    }
}