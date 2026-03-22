package com.example.goodroad.ui.auth
import com.example.goodroad.ui.theme.*

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Person
import com.example.goodroad.BuildConfig
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.network.RecoverPasswordReq
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@Composable
fun RecoverPasswordScreen(
    onLogin: () -> Unit
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var firstNameWarning by rememberSaveable { mutableStateOf<String?>(null) }
    var lastNameWarning by rememberSaveable { mutableStateOf<String?>(null) }
    var phoneWarning by rememberSaveable { mutableStateOf<String?>(null) }
    var errorText by rememberSaveable { mutableStateOf<String?>(null) }
    var successText by rememberSaveable { mutableStateOf<String?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AuthScreenFrame(
        title = "Смена пароля",
        subtitle = "Для восстановления введите имя, фамилию, номер телефона и новый пароль.",
        action = {
            AuthButton(
                text = if (loading) "Сохраняем..." else "Сменить пароль",
                enabled = !loading
            ) {
                val firstNameNormalized = normalizeRequiredCyrillic(firstName)
                if (firstNameNormalized == null) {
                    firstNameWarning = CYRILLIC_WARNING
                    errorText = "Имя обязательно и должно содержать только кириллицу, пробел и -"
                    successText = null
                    return@AuthButton
                }

                val lastNameNormalized = normalizeRequiredCyrillic(lastName)
                if (lastNameNormalized == null) {
                    lastNameWarning = CYRILLIC_WARNING
                    errorText = "Фамилия обязательна и должна содержать только кириллицу, пробел и -"
                    successText = null
                    return@AuthButton
                }

                val phoneDigits = normalizeRequiredRussianPhone(phone)
                if (phoneDigits == null || newPassword.isBlank() || confirmPassword.isBlank()) {
                    phoneWarning = PHONE_FORMAT_WARNING
                    errorText = "Заполните все поля"
                    successText = null
                    return@AuthButton
                }

                if (newPassword != confirmPassword) {
                    errorText = "Пароли не совпадают"
                    successText = null
                    return@AuthButton
                }

                if (BuildConfig.MOCK_AUTH) {
                    errorText = null
                    successText = "Пароль успешно изменен. Теперь можно войти."
                    firstName = ""
                    lastName = ""
                    phone = ""
                    newPassword = ""
                    confirmPassword = ""
                    firstNameWarning = null
                    lastNameWarning = null
                    phoneWarning = null
                    return@AuthButton
                }

                scope.launch {
                    loading = true
                    errorText = null
                    successText = null
                    try {
                        ApiClient.authApi.recoverPassword(
                            RecoverPasswordReq(
                                phone = formatPhoneForRequest(phoneDigits),
                                firstName = firstNameNormalized,
                                lastName = lastNameNormalized,
                                newPassword = newPassword
                            )
                        )
                        successText = "Пароль успешно изменен. Теперь можно войти."
                        firstName = ""
                        lastName = ""
                        phone = ""
                        newPassword = ""
                        confirmPassword = ""
                        firstNameWarning = null
                        lastNameWarning = null
                        phoneWarning = null
                    } catch (_: HttpException) {
                        errorText = "Не удалось восстановить пароль"
                    } catch (_: IOException) {
                        errorText = "Нет соединения с сервером"
                    } catch (_: Exception) {
                        errorText = "Ошибка смены пароля"
                    } finally {
                        loading = false
                    }
                }
            }
        },
        footer = {
            AuthFooter(
                prefix = "Вспомнили пароль?",
                action = "Вернуться ко входу",
                onClick = onLogin
            )
        }
    ) {
        PlainField(
            value = firstName,
            onValueChange = { value ->
                when {
                    !isAllowedCyrillicInput(value) -> {
                        firstNameWarning = CYRILLIC_WARNING
                    }
                    value != firstName -> {
                        firstName = value
                        firstNameWarning = null
                    }
                }
            },
            label = "Имя",
            icon = {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Person,
                    contentDescription = null,
                    tint = UrbanBrown
                )
            },
            warning = firstNameWarning
        )

        Spacer(Modifier.height(12.dp))

        PlainField(
            value = lastName,
            onValueChange = { value ->
                when {
                    !isAllowedCyrillicInput(value) -> {
                        lastNameWarning = CYRILLIC_WARNING
                    }
                    value != lastName -> {
                        lastName = value
                        lastNameWarning = null
                    }
                }
            },
            label = "Фамилия",
            icon = {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Person,
                    contentDescription = null,
                    tint = UrbanBrown
                )
            },
            warning = lastNameWarning
        )

        Spacer(Modifier.height(12.dp))

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
            value = newPassword,
            onValueChange = { newPassword = it },
            label = "Новый пароль"
        )

        Spacer(Modifier.height(12.dp))

        PasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Подтвердите пароль"
        )

        AuthStatusText(text = errorText)
        AuthSuccessText(text = successText)
    }
}