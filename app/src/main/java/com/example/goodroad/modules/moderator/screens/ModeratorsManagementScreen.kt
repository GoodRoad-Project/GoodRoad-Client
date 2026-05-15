package com.example.goodroad.modules.moderator.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.moderator.data.ModeratorView
import com.example.goodroad.modules.moderator.presentation.ModeratorViewModel
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.fields.*
import com.example.goodroad.ui.theme.*
import com.example.goodroad.validation.*

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
        if (searchQuery.isBlank()) moderators
        else moderators.filter { moderator ->
            listOfNotNull(moderator.firstName, moderator.lastName)
                .joinToString(" ")
                .contains(searchQuery, ignoreCase = true)
        }
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
            Text(
                text = "Модераторы",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> CircularProgressIndicator(color = UrbanBrown)
                    error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Поиск модераторов") },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.large,
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, null)
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
                            }

                            if (filteredModerators.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillParentMaxSize()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            if (searchQuery.isBlank()) "Нет модераторов"
                                            else "Ничего не найдено"
                                        )
                                    }
                                }
                            } else {
                                items(filteredModerators, key = { it.id }) { moderator ->
                                    ModeratorCard(
                                        moderator = moderator,
                                        onDelete = { selectedForDelete = moderator }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryButton(
                    text = "+ Добавить модератора",
                    onClick = { showAddDialog = true }
                )
                PrimaryButton(
                    text = "Назад в профиль",
                    onClick = onBack
                )
            }
        }
    }

    if (showAddDialog) {
        AddModeratorDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { firstName, lastName, phone, password ->
                viewModel.addModerator(
                    firstName = firstName,
                    lastName = lastName,
                    phone = phone,
                    password = password,
                    onSuccess = {
                        showAddDialog = false
                        viewModel.loadModerators()
                    }
                )
            }
        )
    }

    selectedForDelete?.let { moderator ->
        AlertDialog(
            onDismissRequest = { selectedForDelete = null },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
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

@Composable
private fun ModeratorCard(
    moderator: ModeratorView,
    onDelete: () -> Unit
) {
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
                    text = listOfNotNull(moderator.firstName, moderator.lastName)
                        .joinToString(" ")
                        .ifBlank { "Без имени" }
                )
                Text(text = moderator.role ?: "", color = MaterialTheme.colorScheme.primary)
                Text(text = if (moderator.active) "ACTIVE" else "DISABLED")
            }
            when {
                isAdmin -> Text("ADMIN", color = MaterialTheme.colorScheme.primary)
                isDisabled -> Text("DISABLED", color = MaterialTheme.colorScheme.error)
                else -> Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = onDelete
                ) { Text("Удалить") }
            }
        }
    }
}

@Composable
private fun AddModeratorDialog(
    onDismiss: () -> Unit,
    onAdd: (firstName: String, lastName: String, phone: String, password: String) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneWarning by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val phoneDigits = normalizeRequiredRussianPhone(phone)
                    if (firstName.isBlank() || lastName.isBlank() || password.isBlank() || phoneDigits == null) {
                        errorText = "Заполните все поля корректно"
                        phoneWarning = if (phoneDigits == null && phone.isNotBlank()) PHONE_FORMAT_WARNING else null
                        return@Button
                    }
                    errorText = null
                    onAdd(firstName.trim(), lastName.trim(), formatPhoneForRequest(phoneDigits), password)
                }
            ) {
                Text("Создать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        },
        title = { Text("Добавить модератора") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                NameField(value = firstName, onValueChange = { firstName = it }, label = "Имя")
                NameField(value = lastName, onValueChange = { lastName = it }, label = "Фамилия")
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
                PasswordField(value = password, onValueChange = { password = it }, label = "Пароль")
                if (!errorText.isNullOrBlank()) {
                    Text(text = errorText!!, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}