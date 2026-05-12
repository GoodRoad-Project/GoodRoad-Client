package com.example.goodroad.modules.moderator.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.moderator.data.ModeratorView
import com.example.goodroad.ui.AuthScreenFrame
import com.example.goodroad.ui.fields.*
import com.example.goodroad.validation.PHONE_FORMAT_WARNING
import com.example.goodroad.validation.formatPhoneForRequest
import com.example.goodroad.validation.isAllowedDigitsInput
import com.example.goodroad.validation.normalizeRequiredRussianPhone
import com.example.goodroad.modules.moderator.presentation.ModeratorViewModel
import com.example.goodroad.ui.theme.UrbanBrown

private val Green = Color(0xFF2E7D32)

@Composable
private fun NameField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    PlainField(
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
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadModerators()
    }

    val filteredModerators = remember(moderators, searchQuery) {
        if (searchQuery.isBlank()) {
            moderators
        } else {
            moderators.filter { moderator ->
                val fullName = listOfNotNull(
                    moderator.firstName,
                    moderator.lastName
                ).joinToString(" ")

                fullName.contains(searchQuery, ignoreCase = true)
            }
        }
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
                .padding(horizontal = 12.dp, vertical = 0.dp)
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

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Поиск модераторов") },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green,
                    unfocusedBorderColor = Green,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = Green,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredModerators.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (searchQuery.isBlank())
                            "Нет модераторов"
                        else
                            "Ничего не найдено"
                    )
                }
            } else {

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    filteredModerators.forEach { moderator ->

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
                        onValueChange = { firstName = it },
                        label = "Имя"
                    )

                    NameField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = "Фамилия"
                    )

                    PhoneField(
                        value = phone,
                        onValueChange = { value ->
                            phone = value

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
                        onValueChange = { password = it },
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