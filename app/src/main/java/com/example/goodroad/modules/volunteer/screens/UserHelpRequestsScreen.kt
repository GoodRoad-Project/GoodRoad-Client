package com.example.goodroad.modules.volunteer.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.volunteer.presentation.VolunteerViewModel
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.*

@Composable
fun UserHelpRequestsScreen(
    viewModel: VolunteerViewModel
) {
    val requests = viewModel.requests
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val successMessage by viewModel.successMessage

    var deleteTarget by remember { mutableStateOf<VolunteerViewModel.HelpRequest?>(null) }

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

                                Row(modifier = Modifier.fillMaxWidth()) {

                                    Column(modifier = Modifier.weight(1f)) {
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
                                        Column(modifier = Modifier.weight(1f)) {
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

                                Text("Telegram / ВК", color = UrbanBrown, fontWeight = FontWeight.SemiBold)
                                Text(req.socialNickname.ifBlank { "—" })

                                Spacer(Modifier.height(8.dp))

                                Text("Комментарий", color = UrbanBrown, fontWeight = FontWeight.SemiBold)
                                Text(req.comment)

                                Spacer(Modifier.height(10.dp))

                                Column {
                                    Text(
                                        text = "Статус",
                                        color = UrbanBrown,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    StatusBadge(status = req.status)
                                }

                                if (req.status == VolunteerViewModel.RequestStatus.ACCEPTED ||
                                    req.status == VolunteerViewModel.RequestStatus.OPEN
                                ) {

                                    Spacer(Modifier.height(12.dp))

                                    if (req.status == VolunteerViewModel.RequestStatus.ACCEPTED) {

                                        PrimaryButton(
                                            text = "Прогулка выполнена",
                                            backgroundColor = SafeGreen,
                                            onClick = {
                                                viewModel.finishWalk(req.id)
                                            }
                                        )

                                        Spacer(Modifier.height(8.dp))
                                    }

                                    PrimaryButton(
                                        text = "Удалить заявку",
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
                Text("Удалить эту заявку?")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.cancelOrDeleteRequest(target.id, target.status)
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

@Composable
fun StatusBadge(status: VolunteerViewModel.RequestStatus) {
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

    fun statusColor(status: VolunteerViewModel.RequestStatus): Color {
        return when (status) {
            VolunteerViewModel.RequestStatus.PENDING -> Color(0xFFFFA000)
            VolunteerViewModel.RequestStatus.OPEN -> SafeGreen
            VolunteerViewModel.RequestStatus.APPROVED -> SafeGreen
            VolunteerViewModel.RequestStatus.ACCEPTED -> SafeGreen
            VolunteerViewModel.RequestStatus.REJECTED -> AlertRed
            VolunteerViewModel.RequestStatus.CANCELLED -> Color(0xFF757575)
            VolunteerViewModel.RequestStatus.COMPLETED -> Color(0xFF2E7D32)
            VolunteerViewModel.RequestStatus.UNKNOWN -> Color.Gray
        }
    }

    val color = statusColor(status)

    Surface(
        modifier = Modifier.wrapContentWidth(),
        color = color.copy(alpha = 0.14f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, color)
    ) {
        Text(
            text = statusText(status),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = color,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}