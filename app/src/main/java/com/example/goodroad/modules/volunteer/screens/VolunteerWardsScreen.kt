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
import com.example.goodroad.modules.volunteer.presentation.VolunteerViewModel.HelpRequest
import com.example.goodroad.modules.volunteer.presentation.VolunteerViewModel.RequestStatus
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.*

@Composable
fun VolunteerWardsScreen(
    viewModel: VolunteerViewModel,
    onBack: () -> Unit
) {
    val wards = viewModel.wards
    val isLoading = viewModel.isLoading.value
    val error = viewModel.errorMessage.value

    LaunchedEffect(Unit) {
        viewModel.loadMyWards()
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

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Мои подопечные",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(20.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = UrbanBrown)
                }
            } else {

                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(8.dp))
                }

                if (wards.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "У вас пока нет активных подопечных",
                            color = UrbanBrown
                        )
                    }
                } else {

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = wards,
                            key = { it.id }
                        ) { item ->

                            WardRequestCard(
                                item = item,
                                onWithdraw = {
                                    viewModel.withdrawRequest(item.id)
                                },
                                onFinish = {
                                    viewModel.finishWalk(item.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WardRequestCard(
    item: HelpRequest,
    onWithdraw: () -> Unit,
    onFinish: () -> Unit
) {
    val parts = item.dateTime.split(" ")

    val rawDate = parts.getOrNull(0) ?: item.dateTime
    val datePart = rawDate.replace("-", ".")
    val timePart = parts.getOrNull(1) ?: ""

    val isActive = item.status == RequestStatus.ACCEPTED
    val isCancelled = item.status == RequestStatus.CANCELLED
    val isCompleted = item.status == RequestStatus.COMPLETED

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

            Text(
                text = "${item.routeStart} → ${item.routeEnd}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = UrbanBrown
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text("Дата", color = UrbanBrown, fontWeight = FontWeight.SemiBold)
                    Text(datePart, color = TextPrimary)
                }

                if (timePart.isNotBlank()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Время", color = UrbanBrown, fontWeight = FontWeight.SemiBold)
                        Text(timePart, color = TextPrimary)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text("Комментарий:", color = UrbanBrown, fontWeight = FontWeight.SemiBold)
            Text(item.comment, color = TextPrimary)

            Spacer(Modifier.height(10.dp))

            Text("Телефон:", color = UrbanBrown, fontWeight = FontWeight.SemiBold)
            Text(item.contact.ifBlank { "Не указан" }, color = TextPrimary)

            if (item.specialNotes.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text("Дополнительно:", color = UrbanBrown, fontWeight = FontWeight.SemiBold)
                Text(item.specialNotes, color = TextPrimary)
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = when (item.status) {
                    RequestStatus.CANCELLED -> "Заявка отменена"
                    RequestStatus.COMPLETED -> "Заявка выполнена"
                    RequestStatus.ACCEPTED -> "Вы приняли заявку на выполнение"
                    else -> "Активная заявка"
                },
                color = when (item.status) {
                    RequestStatus.CANCELLED -> AlertRed
                    RequestStatus.COMPLETED -> SafeGreen
                    RequestStatus.ACCEPTED -> SafeGreen
                    else -> UrbanBrown
                },
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(14.dp))

            if (isActive) {

                PrimaryButton(
                    text = "Прогулка завершена",
                    backgroundColor = SafeGreen,
                    onClick = onFinish
                )

                Spacer(Modifier.height(10.dp))

                PrimaryButton(
                    text = "Отказаться от заявки",
                    backgroundColor = AlertRed,
                    onClick = onWithdraw
                )
            }
        }
    }
}