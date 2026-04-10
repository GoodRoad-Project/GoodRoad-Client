package com.example.goodroad.ui.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.auth.AuthButton
import com.example.goodroad.ui.auth.PasswordField
import com.example.goodroad.ui.auth.PhoneField
import com.example.goodroad.ui.auth.PlainField
import com.example.goodroad.ui.common.validation.CYRILLIC_WARNING
import com.example.goodroad.ui.common.validation.PHONE_CHARS_WARNING
import com.example.goodroad.ui.common.validation.PHONE_FORMAT_WARNING
import com.example.goodroad.ui.common.validation.formatPhoneForRequest
import com.example.goodroad.ui.common.validation.isAllowedCyrillicInput
import com.example.goodroad.ui.common.validation.isAllowedDigitsInput
import com.example.goodroad.ui.common.validation.normalizeRequiredCyrillic
import com.example.goodroad.ui.common.validation.normalizeRequiredRussianPhone
import com.example.goodroad.ui.theme.GrayButton
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.theme.WhiteSoft
import com.example.goodroad.ui.viewmodel.UserViewModel

@Composable
fun UserEditScreen(
    userViewModel: UserViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val user = userViewModel.user.value ?: return

    var firstName by remember { mutableStateOf(user.firstName ?: "") }
    var lastName by remember { mutableStateOf(user.lastName ?: "") }
    var photoUrl by remember { mutableStateOf(user.photoUrl ?: "") }
    var phone by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    var firstNameWarning by remember { mutableStateOf<String?>(null) }
    var lastNameWarning by remember { mutableStateOf<String?>(null) }
    var phoneWarning by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val errorMessage by remember { derivedStateOf { userViewModel.errorMessage.value } }
    val finalError = errorMessage ?: errorText

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        UserDecor()

        Text(
            "Редактирование профиля",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )

        Spacer(Modifier.height(12.dp))

        PlainField(
            value = firstName,
            onValueChange = { value ->
                when {
                    !isAllowedCyrillicInput(value) -> firstNameWarning = CYRILLIC_WARNING
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
                    !isAllowedCyrillicInput(value) -> lastNameWarning = CYRILLIC_WARNING
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

        PlainField(
            value = photoUrl,
            onValueChange = { photoUrl = it },
            label = "URL фото",
            icon = {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = null,
                    tint = UrbanBrown
                )
            }
        )

        Spacer(Modifier.height(12.dp))

        PhoneField(
            value = phone,
            onValueChange = { value ->
                phone = value
                phoneWarning = when {
                    !isAllowedDigitsInput(value) -> PHONE_CHARS_WARNING
                    value.length > 11 -> PHONE_FORMAT_WARNING
                    value.isNotEmpty() && value.first() !in listOf('7', '8') -> PHONE_FORMAT_WARNING
                    else -> null
                }
            },
            label = "Новый телефон",
            warning = phoneWarning
        )

        Spacer(Modifier.height(4.dp))
        Text(
            text = "Оставьте поле пустым, если номер менять не нужно.",
            style = MaterialTheme.typography.bodySmall,
            color = UrbanBrown
        )

        Spacer(Modifier.height(12.dp))

        PasswordField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = "Старый пароль"
        )

        Spacer(Modifier.height(12.dp))

        PasswordField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = "Новый пароль"
        )

        Spacer(Modifier.height(4.dp))
        Text(
            text = "Чтобы сменить пароль, заполните оба поля. Иначе оставьте их пустыми.",
            style = MaterialTheme.typography.bodySmall,
            color = UrbanBrown
        )

        if (!finalError.isNullOrBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = finalError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(20.dp))

        AuthButton(text = "Сохранить") {
            val firstNameNormalized = normalizeRequiredCyrillic(firstName)
            if (firstNameNormalized == null) {
                firstNameWarning = CYRILLIC_WARNING
                errorText = "Имя должно содержать только кириллицу"
                return@AuthButton
            }

            val lastNameNormalized = normalizeRequiredCyrillic(lastName)
            if (lastNameNormalized == null) {
                lastNameWarning = CYRILLIC_WARNING
                errorText = "Фамилия должна содержать только кириллицу"
                return@AuthButton
            }

            val phoneDigits =
                phone.takeIf { it.isNotBlank() }?.let { normalizeRequiredRussianPhone(it) }

            if (phone.isNotBlank() && phoneDigits == null) {
                phoneWarning = PHONE_FORMAT_WARNING
                errorText = "Некорректный телефон"
                return@AuthButton
            }

            val oldPass = oldPassword.takeIf { it.isNotBlank() }
            val newPass = newPassword.takeIf { it.isNotBlank() }

            userViewModel.updateUser(
                firstName = firstNameNormalized,
                lastName = lastNameNormalized,
                photoUrl = photoUrl.ifBlank { null },
                phone = phoneDigits?.let { formatPhoneForRequest(it) },
                oldPassword = oldPass,
                newPassword = newPass
            )

            oldPassword = ""
            newPassword = ""
            errorText = null
        }

        Spacer(Modifier.height(12.dp))

        AuthButton(
            text = "Назад",
            backgroundColor = GrayButton,
            contentColor = WhiteSoft,
            onClick = onBack
        )

        Spacer(Modifier.height(12.dp))

        AuthButton(
            text = "Выйти",
            backgroundColor = GrayButton,
            contentColor = WhiteSoft
        ) {
            userViewModel.logout(onLogout)
        }
    }
}