package com.example.goodroad.ui.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goodroad.ui.common.validation.CYRILLIC_WARNING
import com.example.goodroad.ui.common.validation.PHONE_CHARS_WARNING
import com.example.goodroad.ui.common.validation.PHONE_FORMAT_WARNING
import com.example.goodroad.ui.common.validation.formatPhoneForRequest
import com.example.goodroad.ui.common.validation.isAllowedCyrillicInput
import com.example.goodroad.ui.common.validation.isAllowedDigitsInput
import com.example.goodroad.ui.common.validation.normalizeRequiredCyrillic
import com.example.goodroad.ui.common.validation.normalizeRequiredRussianPhone
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.viewmodel.AuthViewModel

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

    val viewModel: AuthViewModel = viewModel()
    val recoverResult by viewModel.recoverResult.observeAsState()
    val error by viewModel.error.observeAsState()

    AuthScreenFrame(
        title = "Смена пароля",
        subtitle = "Для восстановления введите имя, фамилию, номер телефона и новый пароль.",
        action = {
            AuthButton(
                text = "Сменить пароль",
                enabled = recoverResult != true
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
                if (phoneDigits == null || newPassword.isBlank() || confirmPassword.isBlank()) {
                    phoneWarning = PHONE_FORMAT_WARNING
                    errorText = "Заполните все поля"
                    return@AuthButton
                }

                if (newPassword != confirmPassword) {
                    errorText = "Пароли не совпадают"
                    return@AuthButton
                }

                errorText = null
                viewModel.recoverPassword(
                    phone = formatPhoneForRequest(phoneDigits),
                    firstName = firstNameNormalized,
                    lastName = lastNameNormalized,
                    newPassword = newPassword
                )
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
                    !isAllowedCyrillicInput(value) -> firstNameWarning = CYRILLIC_WARNING
                    else -> {
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
                    !isAllowedCyrillicInput(value) -> lastNameWarning = CYRILLIC_WARNING
                    else -> {
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
                    !isAllowedDigitsInput(value) -> phoneWarning = PHONE_CHARS_WARNING
                    value.length > 11 || value.isNotEmpty() && value.first() !in listOf('7', '8') ->
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

        AuthStatusText(text = error ?: errorText)
    }
}