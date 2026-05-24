package com.example.goodroad.modules.volunteer.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.volunteer.presentation.VolunteerViewModel
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.UrbanBrown

@Composable
fun UserHelpRequestsScreen(
    viewModel: VolunteerViewModel
) {
    val requests = viewModel.requests
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val successMessage by viewModel.successMessage

    var deleteTarget by remember { mutableStateOf<VolunteerViewModel.HelpRequest?>(null) }

    fun statusText(status: VolunteerViewModel.RequestStatus): String {
        return when (status) {
            VolunteerViewModel.RequestStatus.PENDING -> "В обработке"
            VolunteerViewModel.RequestStatus.APPROVED -> "Одобрено"
            VolunteerViewModel.RequestStatus.REJECTED -> "Отклонено"
            VolunteerViewModel.RequestStatus.OPEN -> "Открыта"
            VolunteerViewModel.RequestStatus.ACCEPTED -> "Принята"
            VolunteerViewModel.RequestStatus.CANCELLED -> "Отменена"
            VolunteerViewModel.RequestStatus.COMPLETED -> "Выполнена"
            VolunteerViewModel.RequestStatus.UNKNOWN -> "Неизвестно"
        }
    }

    fun actionLabel(status: VolunteerViewModel.RequestStatus): String {
        return when (status) {
            VolunteerViewModel.RequestStatus.ACCEPTED -> "Отменить"
            VolunteerViewModel.RequestStatus.COMPLETED -> ""
            else -> "Удалить"
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadOwnRequests()
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
            UserDecor()

            Text(
                text = "Мои заявки",
                style = MaterialTheme.typography.headlineLarge
            )

            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (successMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = successMessage!!,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(16.dp))

            if (isLoading && requests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (requests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Пока нет заявок")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(requests, key = { it.id }) { req ->
                        OutlinedCard(
                            border = BorderStroke(2.dp, UrbanBrown.copy(alpha = 0.4f)),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = BackgroundLight
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = "Маршрут",
                                    color = UrbanBrown,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text("${req.routeStart} ➜ ${req.routeEnd}")

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = "Дата",
                                    color = UrbanBrown,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(req.dateTime)

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = "Контакт",
                                    color = UrbanBrown,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(req.contact)

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = "Особенности",
                                    color = UrbanBrown,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(req.specialNotes)

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = "Комментарий",
                                    color = UrbanBrown,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(req.comment)

                                Spacer(Modifier.height(10.dp))

                                Text(
                                    text = "Статус",
                                    color = UrbanBrown,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(statusText(req.status))

                                if (req.status != VolunteerViewModel.RequestStatus.COMPLETED) {
                                    Spacer(Modifier.height(12.dp))

                                    PrimaryButton(
                                        text = actionLabel(req.status),
                                        onClick = { deleteTarget = req }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (deleteTarget != null) {
        val target = deleteTarget!!

        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Подтверждение") },
            text = {
                Text(
                    if (target.status == VolunteerViewModel.RequestStatus.ACCEPTED) {
                        "Отменить эту заявку?"
                    } else {
                        "Удалить эту заявку?"
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteRequest(target.id)
                    deleteTarget = null
                }) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Нет")
                }
            }
        )
    }
}