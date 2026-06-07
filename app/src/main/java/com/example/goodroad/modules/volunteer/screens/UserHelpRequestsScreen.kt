package com.example.goodroad.modules.volunteer.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.volunteer.presentation.VolunteerViewModel
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.AlertRed

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
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(16.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(8.dp))
            }

            if (successMessage != null) {
                Text(
                    text = successMessage!!,
                    color = SafeGreen
                )
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(12.dp))

            if (isLoading && requests.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = UrbanBrown)
                }
            } else if (requests.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Пока нет заявок на помощь",
                        color = UrbanBrown
                    )
                }
            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(requests, key = { it.id }) { req ->

                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(2.dp, UrbanBrown),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = BackgroundLight
                            )
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                val parts = req.dateTime.split(" ")
                                val rawDate = parts.getOrNull(0) ?: req.dateTime
                                val datePart = rawDate.replace("-", ".")
                                val timePart = parts.getOrNull(1) ?: ""

                                Text(
                                    text = "${req.routeStart} → ${req.routeEnd}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = UrbanBrown
                                )

                                Spacer(Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "Дата",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = UrbanBrown
                                        )

                                        Text(
                                            text = datePart,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = TextPrimary
                                        )
                                    }

                                    if (timePart.isNotBlank()) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "Время",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = UrbanBrown
                                            )

                                            Text(
                                                text = timePart,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = TextPrimary
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(8.dp))

                                Text("Контакт", color = UrbanBrown, fontWeight = FontWeight.SemiBold)
                                Text(req.contact)

                                Spacer(Modifier.height(8.dp))

                                Text("Особенности", color = UrbanBrown, fontWeight = FontWeight.SemiBold)
                                Text(req.specialNotes)

                                Spacer(Modifier.height(8.dp))

                                Text("Комментарий", color = UrbanBrown, fontWeight = FontWeight.SemiBold)
                                Text(req.comment)

                                Spacer(Modifier.height(10.dp))

                                Text("Статус", color = UrbanBrown, fontWeight = FontWeight.SemiBold)
                                Text(statusText(req.status))

                                if (req.status != VolunteerViewModel.RequestStatus.COMPLETED) {

                                    Spacer(Modifier.height(12.dp))

                                    PrimaryButton(
                                        text = actionLabel(req.status),
                                        backgroundColor = AlertRed,
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
                    if (target.status == VolunteerViewModel.RequestStatus.ACCEPTED)
                        "Отменить эту заявку?"
                    else
                        "Удалить эту заявку?"
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