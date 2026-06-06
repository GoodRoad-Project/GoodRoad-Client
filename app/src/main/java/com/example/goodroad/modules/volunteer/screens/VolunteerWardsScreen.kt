package com.example.goodroad.modules.volunteer.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.volunteer.presentation.VolunteerViewModel
import com.example.goodroad.modules.volunteer.presentation.VolunteerViewModel.HelpRequest
import com.example.goodroad.ui.UserDecor
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
                    CircularProgressIndicator(
                        color = UrbanBrown
                    )
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
    onWithdraw: () -> Unit
) {
    val parts = item.dateTime.split(" ")

    val rawDate = parts.getOrNull(0) ?: item.dateTime
    val datePart = rawDate.replace("-", ".")
    val timePart = parts.getOrNull(1) ?: ""

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

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Комментарий:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = UrbanBrown
            )

            Text(
                text = item.comment,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Телефон:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = UrbanBrown
            )

            Text(
                text = item.contact.ifBlank { "Не указан" },
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )

            if (item.specialNotes.isNotBlank()) {

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "Дополнительно:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = UrbanBrown
                )

                Text(
                    text = item.specialNotes,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
            }

            Spacer(Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AlertRed.copy(alpha = 0.14f),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.5.dp, AlertRed),
                tonalElevation = 1.dp
            ) {
                Button(
                    onClick = onWithdraw,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = AlertRed
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    )
                ) {
                    Text("Отказаться от заявки")
                }
            }
        }
    }
}