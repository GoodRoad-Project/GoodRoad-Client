package com.example.goodroad.ui.auth

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goodroad.BuildConfig
import com.example.goodroad.ui.common.validation.*
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.viewmodel.AuthViewModel
@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onLogin: () -> Unit
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var firstNameWarning by rememberSaveable { mutableStateOf<String?>(null) }
    var lastNameWarning by rememberSaveable { mutableStateOf<String?>(null) }
    var phoneWarning by rememberSaveable { mutableStateOf<String?>(null) }
    var errorText by rememberSaveable { mutableStateOf<String?>(null) }

    val viewModel: AuthViewModel = viewModel()
    val registerResult by viewModel.loginResult.observeAsState()
    val error by viewModel.error.observeAsState()
    val loading by viewModel.isLoading.observeAsState(initial = false)

    LaunchedEffect(registerResult) {
        registerResult?.user?.role?.let { role ->
            onRegisterSuccess(role)
        }
    }

    AuthScreenFrame(
        title = "Создать аккаунт",
        action = {
            AuthButton(
                text = if (loading) "Создаем..." else "Зарегистрироваться",
                enabled = !loading
            ) {
                val firstNameNormalized = normalizeRequiredCyrillic(firstName)
                if (firstNameNormalized == null) {
                    firstNameWarning = CYRILLIC_WARNING
                    errorText = "Имя обязательно и должно содержать только кириллицу, пробел и -"
                    return@AuthButton
                }

                val lastNameNormalized = normalizeRequiredCyrillic(lastName)
                if (lastNameNormalized == null) {
                    lastNameWarning = CYRILLIC_WARNING
                    errorText = "Фамилия обязательна и должна содержать только кириллицу, пробел и -"
                    return@AuthButton
                }

                val phoneDigits = normalizeRequiredRussianPhone(phone)
                if (phoneDigits == null || password.isBlank()) {
                    phoneWarning = PHONE_FORMAT_WARNING
                    errorText = "Телефон и пароль обязательны"
                    return@AuthButton
                }

                if (password != confirmPassword) {
                    errorText = "Пароли не совпадают"
                    return@AuthButton
                }

                if (BuildConfig.MOCK_AUTH) {
                    errorText = null
                    onRegisterSuccess("USER")
                    return@AuthButton
                }

                viewModel.register(
                    firstNameNormalized,
                    lastNameNormalized,
                    formatPhoneForRequest(phoneDigits),
                    password
                )
            }
        },
        footer = {
            AuthFooter(
                prefix = "Уже есть аккаунт?",
                action = "Войти",
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
                Icon(
                    imageVector = Icons.Default.Person,
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
                Icon(
                    imageVector = Icons.Default.Person,
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
            value = password,
            onValueChange = { password = it },
            label = "Пароль"
        )

        Spacer(Modifier.height(12.dp))

        PasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Подтвердите пароль"
        )

        AuthStatusText(text = error ?: errorText)
    }
}