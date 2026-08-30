package com.example.goodroad.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.user.presentation.UserViewModel
import com.example.goodroad.ui.AuthStatusText
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.fields.PasswordField
import com.example.goodroad.ui.fields.PhoneField
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.validation.PHONE_FORMAT_WARNING
import com.example.goodroad.validation.formatPhoneForRequest
import com.example.goodroad.validation.isValidRussianPhoneDigits
import com.example.goodroad.validation.normalizeRequiredRussianPhone

@Composable
fun ChangePhoneScreen(
    userViewModel: UserViewModel,
    onBack: () -> Unit
) {
    var phone by rememberSaveable { mutableStateOf("") }
    var currentPassword by rememberSaveable { mutableStateOf("") }

    var localError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val serverError by userViewModel.errorMessage
    val isLoading by userViewModel.isLoading

    val phoneValidation = remember(phone) {
        when {
            phone.isEmpty() -> PhoneValidation.Empty

            !isValidRussianPhoneDigits(phone.trim()) ->
                PhoneValidation.InvalidFormat

            phone.length > 11 ->
                PhoneValidation.InvalidFormat

            phone.first() !in listOf('7', '8') ->
                PhoneValidation.InvalidFormat

            else -> {
                val normalized = normalizeRequiredRussianPhone(phone)

                if (normalized != null) {
                    PhoneValidation.Valid(normalized)
                } else {
                    PhoneValidation.InvalidFormat
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.clearMessages()
        localError = null
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Смена телефона",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onBack,
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = UrbanBrown.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            PhoneField(
                value = phone,
                onValueChange = {
                    phone = it
                    localError = null
                    userViewModel.clearMessages()
                },
                label = "Новый телефон",
                warning = when (phoneValidation) {
                    is PhoneValidation.InvalidFormat ->
                        PHONE_FORMAT_WARNING

                    else ->
                        null
                }
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            PasswordField(
                value = currentPassword,
                onValueChange = {
                    currentPassword = it
                    localError = null
                    userViewModel.clearMessages()
                },
                label = "Текущий пароль"
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            AuthStatusText(
                text = serverError ?: localError,
                onTimeout = {
                    localError = null
                    userViewModel.clearMessages()
                }
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            PrimaryButton(
                text = if (isLoading) {
                    "Меняем..."
                } else {
                    "Сменить номер"
                },
                enabled = !isLoading
            ) {
                when (phoneValidation) {
                    is PhoneValidation.Empty -> {
                        localError = "Введите номер телефона"
                    }

                    is PhoneValidation.InvalidFormat -> {
                        localError = "Введите корректный номер телефона"
                    }

                    is PhoneValidation.Valid -> {
                        if (currentPassword.isBlank()) {
                            localError = "Введите текущий пароль"
                            return@PrimaryButton
                        }

                        localError = null

                        userViewModel.changePhone(
                            newPhone = formatPhoneForRequest(
                                phoneValidation.phoneDigits
                            ),
                            currentPassword = currentPassword,
                            onSuccess = onBack
                        )
                    }
                }
            }
        }
    }
}

private sealed class PhoneValidation {
    data object Empty : PhoneValidation()

    data object InvalidFormat : PhoneValidation()

    data class Valid(
        val phoneDigits: String
    ) : PhoneValidation()
}