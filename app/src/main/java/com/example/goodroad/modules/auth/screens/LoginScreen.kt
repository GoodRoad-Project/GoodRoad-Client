package com.example.goodroad.modules.auth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.modules.auth.presentation.AuthViewModel
import com.example.goodroad.modules.auth.data.AuthResp
import com.example.goodroad.ui.buttons.*
import com.example.goodroad.ui.AuthFooter
import com.example.goodroad.ui.AuthScreenFrame
import com.example.goodroad.ui.AuthStatusText
import com.example.goodroad.ui.fields.*
import com.example.goodroad.validation.PHONE_FORMAT_WARNING
import com.example.goodroad.validation.formatPhoneForRequest
import com.example.goodroad.validation.isValidRussianPhoneDigits
import com.example.goodroad.validation.normalizeRequiredRussianPhone

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (AuthResp) -> Unit,
    onSignUp: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val error by viewModel.error.observeAsState()
    val loading by viewModel.isLoading.observeAsState(initial = false)
    val loginResult by viewModel.loginResult.observeAsState()

    LaunchedEffect(loginResult) {
        loginResult?.let { onLoginSuccess(it) }
    }

    val phoneValidation = remember(phone) {
        when {
            phone.isEmpty() -> PhoneValidation.Empty
            !isValidRussianPhoneDigits(phone.trim()) -> PhoneValidation.InvalidFormat
            phone.length > 11 -> PhoneValidation.InvalidFormat
            phone.first() !in listOf('7', '8') -> PhoneValidation.InvalidFormat
            else -> PhoneValidation.Valid(normalizeRequiredRussianPhone(phone)!!)
        }
    }

    AuthScreenFrame(
        title = "Вход",
        action = {
            PrimaryButton(
                text = if (loading) "Входим..." else "Войти",
                enabled = !loading
            ) {
                when (phoneValidation) {
                    is PhoneValidation.Empty -> {
                        viewModel.setError("Введите номер телефона")
                        return@PrimaryButton
                    }
                    is PhoneValidation.InvalidFormat -> {
                        viewModel.setError("Введите корректный номер телефона")
                        return@PrimaryButton
                    }
                    is PhoneValidation.Valid -> {
                        if (password.isBlank()) {
                            viewModel.setError("Введите пароль")
                            return@PrimaryButton
                        }
                        viewModel.login(
                            formatPhoneForRequest(phoneValidation.phoneDigits),
                            password
                        )
                    }
                }
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
            onValueChange = {
                phone = it
                viewModel.clearError()
            },
            label = "Телефон",
            warning = when (phoneValidation) {
                is PhoneValidation.InvalidFormat -> PHONE_FORMAT_WARNING
                else -> null
            }
        )

        Spacer(Modifier.height(12.dp))

        PasswordField(
            value = password,
            onValueChange = {
                password = it
                viewModel.clearError()
            },
            label = "Пароль"
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onForgotPassword) {
                Text("Забыли пароль?", color = UrbanBrown)
            }
        }

        AuthStatusText(
            text = error,
            onTimeout = viewModel::clearError
        )
    }
}

sealed class PhoneValidation {
    object Empty : PhoneValidation()
    object InvalidFormat : PhoneValidation()
    data class Valid(val phoneDigits: String) : PhoneValidation()
}