package com.example.goodroad.ui.auth
import com.example.goodroad.ui.theme.*

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.BuildConfig
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.network.LoginReq
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

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
    var loading by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                    } else {
                        phoneWarning
                    }
                    errorText = "Заполните телефон и пароль"
                    return@AuthButton
                }

                if (BuildConfig.MOCK_AUTH) {
                    errorText = null
                    onLoginSuccess("USER")
                    return@AuthButton
                }

                scope.launch {
                    loading = true
                    errorText = null
                    try {
                        val resp = ApiClient.authApi.login(
                            LoginReq(
                                phone = formatPhoneForRequest(phoneDigits),
                                password = password
                            )
                        )
                        val role = resp.user?.role
                        if (role.isNullOrBlank()) {
                            errorText = "Не удалось определить роль пользователя"
                        } else {
                            onLoginSuccess(role)
                        }
                    } catch (_: HttpException) {
                        errorText = "Неверный телефон или пароль"
                    } catch (_: IOException) {
                        errorText = "Нет соединения с сервером"
                    } catch (_: Exception) {
                        errorText = "Ошибка входа"
                    } finally {
                        loading = false
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
            onValueChange = { value ->
                when {
                    !isAllowedDigitsInput(value) -> {
                        phoneWarning = PHONE_CHARS_WARNING
                    }
                    value.length > 11 -> {
                        phoneWarning = PHONE_FORMAT_WARNING
                    }
                    value.isNotEmpty() && value.first() !in listOf('7', '8') -> {
                        phoneWarning = PHONE_FORMAT_WARNING
                    }
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

        AuthStatusText(text = errorText)
    }
}