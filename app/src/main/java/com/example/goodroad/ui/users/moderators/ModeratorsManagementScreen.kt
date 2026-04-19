package com.example.goodroad.ui.users.moderators

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.goodroad.data.moderator.ModeratorView
import com.example.goodroad.ui.auth.AuthScreenFrame
import com.example.goodroad.ui.auth.PhoneField
import com.example.goodroad.ui.auth.PasswordField
import com.example.goodroad.ui.common.validation.PHONE_FORMAT_WARNING
import com.example.goodroad.ui.common.validation.formatPhoneForRequest
import com.example.goodroad.ui.common.validation.isAllowedDigitsInput
import com.example.goodroad.ui.common.validation.normalizeRequiredRussianPhone
import com.example.goodroad.ui.viewmodel.ModeratorViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.goodroad.ui.theme.UrbanBrown

@Composable
private fun NameField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    com.example.goodroad.ui.auth.PlainField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        icon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = UrbanBrown
            )
        }
    )
}

@Composable
fun ModeratorsManagementScreen(
    viewModel: ModeratorViewModel,
    onBack: () -> Unit
) {

    val moderators by viewModel.moderators.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedForDelete by remember { mutableStateOf<ModeratorView?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadModerators()
    }

    AuthScreenFrame(
        title = "Модераторы",

        action = {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("+ Добавить модератора")
            }
        },

        footer = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = onBack
            ) {
                Text("Назад в профиль")
            }
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (moderators.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет модераторов")
                }
            } else {

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    moderators.forEach { moderator ->

                        val isAdmin = moderator.role == "MODERATOR_ADMIN"
                        val isDisabled = !moderator.active

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column {

                                    Text(
                                        text = listOfNotNull(
                                            moderator.firstName,
                                            moderator.lastName
                                        ).joinToString(" ")
                                            .ifBlank { "Без имени" }
                                    )

                                    Text(
                                        text = moderator.role ?: "",
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Text(
                                        text = if (moderator.active) "ACTIVE" else "DISABLED"
                                    )
                                }

                                when {
                                    isAdmin -> {
                                        Text(
                                            text = "ADMIN",
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    isDisabled -> {
                                        Text(
                                            text = "DISABLED",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }

                                    else -> {
                                        Button(
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error
                                            ),
                                            onClick = {
                                                selectedForDelete = moderator
                                            }
                                        ) {
                                            Text("Удалить")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {

        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        var phoneWarning by remember { mutableStateOf<String?>(null) }
        var errorText by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                Button(
                    onClick = {

                        val phoneDigits = normalizeRequiredRussianPhone(phone)

                        if (firstName.isBlank() ||
                            lastName.isBlank() ||
                            password.isBlank() ||
                            phoneDigits == null
                        ) {
                            errorText = "Заполните все поля корректно"

                            phoneWarning = if (phoneDigits == null && phone.isNotBlank()) {
                                PHONE_FORMAT_WARNING
                            } else null

                            return@Button
                        }

                        errorText = null

                        viewModel.addModerator(
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            phone = formatPhoneForRequest(phoneDigits),
                            password = password,
                            onSuccess = {
                                showAddDialog = false
                                viewModel.loadModerators()
                            }
                        )

                        showAddDialog = false
                    }
                ) {
                    Text("Создать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Отмена")
                }
            },
            title = { Text("Добавить модератора") },
            text = {

                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    NameField(
                        value = firstName,
                        onValueChange = {
                            firstName = it
                            errorText = null
                        },
                        label = "Имя"
                    )

                    NameField(
                        value = lastName,
                        onValueChange = {
                            lastName = it
                            errorText = null
                        },
                        label = "Фамилия"
                    )

                    PhoneField(
                        value = phone,
                        onValueChange = { value ->
                            phone = value
                            errorText = null

                            phoneWarning = when {
                                value.isEmpty() -> null
                                !isAllowedDigitsInput(value) -> PHONE_FORMAT_WARNING
                                value.length > 11 -> PHONE_FORMAT_WARNING
                                value.firstOrNull() !in listOf('7', '8') -> PHONE_FORMAT_WARNING
                                else -> null
                            }
                        },
                        label = "Телефон",
                        warning = phoneWarning
                    )

                    PasswordField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorText = null
                        },
                        label = "Пароль"
                    )

                    if (!errorText.isNullOrBlank()) {
                        Text(
                            text = errorText!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        )
    }

    selectedForDelete?.let { moderator ->

        AlertDialog(
            onDismissRequest = { selectedForDelete = null },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = {
                        viewModel.disableModerator(moderator.id)
                        selectedForDelete = null
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedForDelete = null }) {
                    Text("Отмена")
                }
            },
            title = { Text("Удаление") },
            text = { Text("Удалить этого модератора?") }
        )
    }
}