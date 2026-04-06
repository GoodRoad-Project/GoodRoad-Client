package com.example.goodroad.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goodroad.BuildConfig
import com.example.goodroad.ui.theme.*
import com.example.goodroad.ui.viewmodel.AuthViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.goodroad.data.network.ApiClient

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUp: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var phoneWarning by rememberSaveable { mutableStateOf<String?>(null) }
    var errorText by rememberSaveable { mutableStateOf<String?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }

    val viewModel: AuthViewModel = viewModel()
    val loginResult by viewModel.loginResult.observeAsState()
    val error by viewModel.error.observeAsState()

    LaunchedEffect(loginResult) {
        if (loginResult?.user != null) {
            ApiClient.setCredentials(
                phone = formatPhoneForRequest(normalizeRequiredRussianPhone(phone)!!),
                password = password
            )
            onLoginSuccess()
        }
    }

    AuthScreenFrame(
        title = "Вход",
        action = {
            AuthButton(
                text = if (loading) "Входим..." else "Войти",
                enabled = !loading
            ) {
                val phoneDigits = normalizeRequiredRussianPhone(phone)
                if (phoneDigits == null || password.isBlank()) {
                    phoneWarning = if (phone.isNotBlank() && !isValidRussianPhoneDigits(phone.trim())) {
                        PHONE_FORMAT_WARNING
                    } else null
                    errorText = "Заполните телефон и пароль"
                    return@AuthButton
                }

                if (BuildConfig.MOCK_AUTH) {
                    errorText = null
                    onLoginSuccess()
                    return@AuthButton
                }

                loading = true
                viewModel.login(formatPhoneForRequest(phoneDigits), password)
                loading = false
            }
        },
        footer = {
            AuthFooter(
                prefix = "Нет аккаунта?",
                action = "Зарегистрироваться",
                onClick = onSignUp
            )
        }
    ) {
        PhoneField(
            value = phone,
            onValueChange = { value ->
                when {
                    !isAllowedDigitsInput(value) -> phoneWarning = PHONE_CHARS_WARNING
                    value.length > 11 -> phoneWarning = PHONE_FORMAT_WARNING
                    value.isNotEmpty() && value.first() !in listOf('7', '8') -> phoneWarning = PHONE_FORMAT_WARNING
                    else -> {
                        if (value != phone) {
                            phone = value
                            phoneWarning = null
                        }
                    }
                }
            },
            label = "Телефон",
            warning = phoneWarning
        )

        Spacer(Modifier.height(12.dp))

        PasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Пароль"
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onForgotPassword,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Забыли пароль?",
                    color = UrbanBrown
                )
            }
        }

        AuthStatusText(text = error ?: errorText)
    }
}