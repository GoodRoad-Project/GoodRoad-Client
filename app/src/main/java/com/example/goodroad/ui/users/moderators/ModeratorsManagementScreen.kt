package com.example.goodroad.ui.users.moderators

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.data.moderator.ModeratorView
import com.example.goodroad.ui.auth.AuthScreenFrame
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.viewmodel.ModeratorViewModel

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
            Button(onClick = { showAddDialog = true }) {
                Text("+ Добавить")
            }
        },
        footer = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = onBack
            ) {
                Text("Назад")
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

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    moderators.forEach { moderator ->

                        val isAdmin = moderator.role == "MODERATOR_ADMIN"

                        Card(
                            modifier = Modifier.fillMaxWidth()
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
                                        color = UrbanBrown
                                    )

                                    Text(
                                        text = if (moderator.active) "ACTIVE" else "DISABLED"
                                    )
                                }

                                if (!isAdmin) {
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
                                } else {
                                    Text(
                                        text = "ADMIN",
                                        color = MaterialTheme.colorScheme.primary
                                    )
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

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                Button(onClick = {
                    viewModel.addModerator(firstName, lastName, phone, password)
                    showAddDialog = false
                }) {
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
                Column {
                    OutlinedTextField(firstName, { firstName = it }, label = { Text("Имя") })
                    OutlinedTextField(lastName, { lastName = it }, label = { Text("Фамилия") })
                    OutlinedTextField(phone, { phone = it }, label = { Text("Телефон") })
                    OutlinedTextField(password, { password = it }, label = { Text("Пароль") })
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