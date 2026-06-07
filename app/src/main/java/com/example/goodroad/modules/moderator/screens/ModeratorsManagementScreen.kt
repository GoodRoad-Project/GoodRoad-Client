package com.example.goodroad.modules.moderator.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.moderator.data.ModeratorView
import com.example.goodroad.modules.moderator.presentation.ModeratorViewModel
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.fields.PasswordField
import com.example.goodroad.ui.fields.PhoneField
import com.example.goodroad.ui.fields.PlainField
import androidx.compose.foundation.layout.size
import com.example.goodroad.ui.theme.AlertRed
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.BorderWarm
import com.example.goodroad.ui.theme.InclusiveViolet
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.SurfaceWarm
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.theme.WarningOrange
import com.example.goodroad.validation.PHONE_FORMAT_WARNING
import com.example.goodroad.validation.formatPhoneForRequest
import com.example.goodroad.validation.isAllowedDigitsInput
import com.example.goodroad.validation.normalizeRequiredRussianPhone

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
        moderators
            .filter { it.role != "MODERATOR_ADMIN" }
            .filter { moderator ->
                if (searchQuery.isBlank()) {
                    true
                } else {
                    listOfNotNull(moderator.firstName, moderator.lastName)
                        .joinToString(" ")
                        .contains(searchQuery, ignoreCase = true)
                }
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
            ) {
                when {
                    isLoading -> CircularProgressIndicator(color = UrbanBrown)
                    error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
                    else -> {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
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
                                    focusedBorderColor = SafeGreen,
                                    unfocusedBorderColor = SafeGreen,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    cursorColor = SafeGreen,
                                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
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
                    backgroundColor = UrbanBrown,
                    contentColor = UrbanBrown,
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = {
                        viewModel.disableModerator(moderator.id)
                        selectedForDelete = null
                    }
                ) {
                    Text("Отключить")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedForDelete = null }) {
                    Text("Отмена")
                }
            },
            title = { Text("Удаление") },
            text = { Text("Отключить этого модератора?") }
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWarm
        ),
        border = BorderStroke(1.dp, BorderWarm),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = listOfNotNull(moderator.firstName, moderator.lastName)
                        .joinToString(" ")
                        .ifBlank { "Без имени" },
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = when (moderator.role) {
                        "MODERATOR_ADMIN" -> "Администратор"
                        "MODERATOR" -> "Модератор"
                        else -> moderator.role ?: ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = UrbanBrown
                )
            }

            Spacer(Modifier.width(12.dp))

            when {
                isAdmin -> {
                    Surface(
                        modifier = Modifier.size(width = 128.dp, height = 40.dp),
                        color = InclusiveViolet.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, InclusiveViolet)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "АДМИН",
                                color = InclusiveViolet,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                isDisabled -> {
                    Surface(
                        modifier = Modifier.size(width = 128.dp, height = 40.dp),
                        color = WarningOrange.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, WarningOrange)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ОТКЛЮЧЕН",
                                color = WarningOrange,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                else -> {
                    Surface(
                        modifier = Modifier
                            .size(width = 128.dp, height = 40.dp)
                            .clickable { onDelete() },
                        color = AlertRed.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AlertRed)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ОТКЛЮЧИТЬ",
                                color = AlertRed,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
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
                    onAdd(
                        firstName.trim(),
                        lastName.trim(),
                        formatPhoneForRequest(phoneDigits),
                        password
                    )
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