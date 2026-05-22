package com.example.goodroad.modules.user.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.goodroad.modules.user.presentation.UserViewModel
import com.example.goodroad.ui.AuthStatusText
import com.example.goodroad.ui.AuthSuccessText
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.fields.PasswordField
import com.example.goodroad.ui.fields.PhoneField
import com.example.goodroad.ui.fields.PlainField
import com.example.goodroad.ui.theme.*
import com.example.goodroad.validation.CYRILLIC_WARNING
import com.example.goodroad.validation.NAME_MAX_LENGTH
import com.example.goodroad.validation.PHONE_CHARS_WARNING
import com.example.goodroad.validation.PHONE_FORMAT_WARNING
import com.example.goodroad.validation.formatPhoneForRequest
import com.example.goodroad.validation.isAllowedCyrillicInput
import com.example.goodroad.validation.isAllowedDigitsInput
import com.example.goodroad.validation.normalizeRequiredCyrillic
import com.example.goodroad.validation.normalizeRequiredRussianPhone

@Composable
fun UserEditScreen(
    userViewModel: UserViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val user = userViewModel.user.value

    if (user == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        var firstName by remember { mutableStateOf(user.firstName ?: "") }
        var lastName by remember { mutableStateOf(user.lastName ?: "") }
        var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
        var phone by remember { mutableStateOf("") }
        var oldPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmNewPassword by remember { mutableStateOf("") }

        var firstNameWarning by remember { mutableStateOf<String?>(null) }
        var lastNameWarning by remember { mutableStateOf<String?>(null) }
        var phoneWarning by remember { mutableStateOf<String?>(null) }
        var errorText by remember { mutableStateOf<String?>(null) }

        val errorMessage by remember { derivedStateOf { userViewModel.errorMessage.value } }
        val successMessage by remember { derivedStateOf { userViewModel.successMessage.value } }
        val isLoading by remember { derivedStateOf { userViewModel.isLoading.value } }
        val finalError = errorMessage ?: errorText

        val hasProfileChanges by remember(firstName, lastName, phone, selectedPhotoUri, user) {
            derivedStateOf {
                firstName != (user.firstName ?: "") ||
                        lastName != (user.lastName ?: "") ||
                        phone.isNotBlank() ||
                        selectedPhotoUri != null
            }
        }

        val hasPasswordChanges by remember(oldPassword, newPassword, confirmNewPassword) {
            derivedStateOf {
                oldPassword.isNotBlank() || newPassword.isNotBlank() || confirmNewPassword.isNotBlank()
            }
        }

        val canSave by remember(hasProfileChanges, hasPasswordChanges, isLoading) {
            derivedStateOf {
                (hasProfileChanges || hasPasswordChanges) && !isLoading
            }
        }

        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                selectedPhotoUri = uri
                userViewModel.uploadAvatar(context, uri) { _ ->
                    selectedPhotoUri = null
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {

            Text(
                text = "Редактирование профиля",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(12.dp))

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
                warning = firstNameWarning,
                maxLength = NAME_MAX_LENGTH
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
                warning = lastNameWarning,
                maxLength = NAME_MAX_LENGTH
            )

            Spacer(Modifier.height(12.dp))
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    selectedPhotoUri != null -> {
                        AsyncImage(
                            model = selectedPhotoUri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    user.photoUrl?.isNotBlank() == true -> {
                        AsyncImage(
                            model = user.photoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    else -> {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = WhiteSoft,
                            tonalElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Photo,
                                    contentDescription = null,
                                    tint = UrbanBrown,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = { photoPickerLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = BackgroundLight,
                    contentColor = UrbanBrown
                ),
                border = BorderStroke(1.dp, BorderWarm)
            ) {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = null,
                    tint = UrbanBrown,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Выбрать фото профиля",
                    style = MaterialTheme.typography.titleMedium,
                    color = UrbanBrown
                )
            }

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
                label = "Телефон",
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

            Spacer(Modifier.height(12.dp))

            PasswordField(
                value = confirmNewPassword,
                onValueChange = { confirmNewPassword = it },
                label = "Подтвердите новый пароль"
            )

            Spacer(Modifier.height(4.dp))
            Text(
                text = "Чтобы сменить пароль, заполните старый пароль и дважды введите новый.",
                style = MaterialTheme.typography.bodySmall,
                color = UrbanBrown
            )

            AuthSuccessText(
                text = successMessage,
                onTimeout = {
                    errorText = null
                    userViewModel.clearMessages()
                }
            )
            AuthStatusText(
                text = finalError,
                onTimeout = {
                    errorText = null
                    userViewModel.clearMessages()
                }
            )

            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                text = if (isLoading) "Сохраняем..." else "Сохранить",
                enabled = canSave
            ) {
                val firstNameNormalized = normalizeRequiredCyrillic(firstName)
                if (firstNameNormalized == null) {
                    firstNameWarning = CYRILLIC_WARNING
                    errorText = "Имя должно содержать только кириллицу"
                    return@PrimaryButton
                }

                val lastNameNormalized = normalizeRequiredCyrillic(lastName)
                if (lastNameNormalized == null) {
                    lastNameWarning = CYRILLIC_WARNING
                    errorText = "Фамилия должна содержать только кириллицу"
                    return@PrimaryButton
                }

                val phoneDigits =
                    phone.takeIf { it.isNotBlank() }?.let { normalizeRequiredRussianPhone(it) }

                if (phone.isNotBlank() && phoneDigits == null) {
                    phoneWarning = PHONE_FORMAT_WARNING
                    errorText = "Некорректный телефон"
                    return@PrimaryButton
                }

                val oldPass = oldPassword.takeIf { it.isNotBlank() }
                val newPass = newPassword.takeIf { it.isNotBlank() }
                val confirmPass = confirmNewPassword.takeIf { it.isNotBlank() }

                if (!newPass.isNullOrBlank() || !confirmPass.isNullOrBlank() || !oldPass.isNullOrBlank()) {
                    if (oldPass.isNullOrBlank() || newPass.isNullOrBlank() || confirmPass.isNullOrBlank()) {
                        errorText = "Для смены пароля заполните все три поля"
                        return@PrimaryButton
                    }
                    if (newPass != confirmPass) {
                        errorText = "Новые пароли не совпадают"
                        return@PrimaryButton
                    }
                }

                userViewModel.updateUser(
                    firstName = firstNameNormalized,
                    lastName = lastNameNormalized,
                    phone = phoneDigits?.let { formatPhoneForRequest(it) },
                    oldPassword = oldPass,
                    newPassword = newPass
                )

                oldPassword = ""
                newPassword = ""
                confirmNewPassword = ""
                errorText = null
            }
        }
    }
}