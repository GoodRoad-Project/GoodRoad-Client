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
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.fields.PlainField
import com.example.goodroad.ui.theme.*
import com.example.goodroad.validation.CYRILLIC_WARNING
import com.example.goodroad.validation.NAME_MAX_LENGTH
import com.example.goodroad.validation.isAllowedCyrillicInput
import com.example.goodroad.validation.normalizeRequiredCyrillic

@Composable
fun UserEditScreen(
    userViewModel: UserViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val user = userViewModel.user.value

    LaunchedEffect(userViewModel.successMessage.value) {
        if (userViewModel.successMessage.value != null) {
            userViewModel.clearMessages()
            onBack()
        }
    }

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

        var firstNameWarning by remember { mutableStateOf<String?>(null) }
        var lastNameWarning by remember { mutableStateOf<String?>(null) }
        var errorText by remember { mutableStateOf<String?>(null) }

        val errorMessage by userViewModel.errorMessage
        val isLoading by userViewModel.isLoading

        val finalError = errorMessage ?: errorText

        val hasProfileChanges = firstName != (user.firstName ?: "") ||
                lastName != (user.lastName ?: "")

        val canSave = (hasProfileChanges || selectedPhotoUri != null) && !isLoading

        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                selectedPhotoUri = uri
                userViewModel.uploadAvatar(context, uri) {
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
            UserDecor()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Редактирование профиля",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = UrbanBrown
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            PlainField(
                value = firstName,
                onValueChange = { value ->
                    when {
                        !isAllowedCyrillicInput(value) -> {
                            firstNameWarning = CYRILLIC_WARNING
                        }
                        else -> {
                            firstName = value
                            firstNameWarning = null
                            errorText = null
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
                        else -> {
                            lastName = value
                            lastNameWarning = null
                            errorText = null
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

            Spacer(Modifier.height(20.dp))

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
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
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
                onClick = {
                    photoPickerLauncher.launch("image/*")
                },
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
                    style = MaterialTheme.typography.titleSmall,
                    color = UrbanBrown
                )
            }

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

                if (hasProfileChanges) {
                    userViewModel.updateUser(
                        firstName = firstNameNormalized,
                        lastName = lastNameNormalized
                    )
                }

                errorText = null
            }
        }
    }
}