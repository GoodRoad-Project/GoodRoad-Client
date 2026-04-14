package com.example.goodroad.ui.auth

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goodroad.ui.common.validation.*
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.viewmodel.AuthViewModel
import com.example.goodroad.data.auth.AuthResp

@Composable
fun LoginScreen(
    onLoginSuccess: (AuthResp) -> Unit,
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
        Log.d("LOGIN_DEBUG", "RAW RESPONSE = $loginResult")
        Log.d("LOGIN_DEBUG", "ROLE = ${loginResult?.user?.role}")

        loginResult?.let { resp ->
            onLoginSuccess(resp)
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
                    phoneWarning =
                        if (phone.isNotBlank() && !isValidRussianPhoneDigits(phone.trim())) {
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
                    value.isNotEmpty() && value.first() !in listOf('7', '8') ->
                        phoneWarning = PHONE_FORMAT_WARNING
                    else -> {
                        phone = value
                        phoneWarning = null
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
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onForgotPassword) {
                Text("Забыли пароль?", color = UrbanBrown)
            }
        }

        AuthStatusText(
            text = error ?: errorText,
            onTimeout = {
                errorText = null
                viewModel.clearError()
            }
        )
    }
}