package com.example.goodroad.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goodroad.ui.common.validation.PHONE_FORMAT_WARNING
import com.example.goodroad.ui.common.validation.formatPhoneForRequest
import com.example.goodroad.ui.common.validation.isAllowedDigitsInput
import com.example.goodroad.ui.common.validation.isValidRussianPhoneDigits
import com.example.goodroad.ui.common.validation.normalizeRequiredRussianPhone
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onSignUp: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var phoneWarning by rememberSaveable { mutableStateOf<String?>(null) }
    var errorText by rememberSaveable { mutableStateOf<String?>(null) }

    val viewModel: AuthViewModel = viewModel()
    val loginResult by viewModel.loginResult.observeAsState()
    val error by viewModel.error.observeAsState()
    val loading by viewModel.isLoading.observeAsState(initial = false)

    LaunchedEffect(loginResult) {
        loginResult?.user?.role?.let { role ->
            onLoginSuccess(role)
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
                errorText = null
                viewModel.login(formatPhoneForRequest(phoneDigits), password)
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
                    !isAllowedDigitsInput(value) -> phoneWarning = PHONE_FORMAT_WARNING
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