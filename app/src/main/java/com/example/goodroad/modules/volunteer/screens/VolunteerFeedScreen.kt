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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goodroad.modules.volunteer.presentation.VolunteerViewModel
import com.example.goodroad.modules.volunteer.presentation.VolunteerViewModel.HelpRequest
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.theme.*

@Composable
fun VolunteerFeedScreen(
    viewModel: VolunteerViewModel = viewModel(),
    onBack: () -> Unit
) {
    val feed = viewModel.feed
    val isLoading = viewModel.isLoading.value
    val error = viewModel.errorMessage.value

    LaunchedEffect(Unit) {
        viewModel.loadFeed()
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
                text = "Лента волонтёра",
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

                if (feed.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Нет доступных заявок",
                            color = UrbanBrown
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(feed, key = { it.id }) { item ->
                            VolunteerRequestCard(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VolunteerRequestCard(
    item: HelpRequest
) {
    val parts = item.dateTime.split(" ")
    val rawDate = parts.getOrNull(0) ?: item.dateTime
    val datePart = rawDate.replace("-", ".")
    val timePart = parts.getOrNull(1) ?: ""

    val dateTimeText = if (timePart.isNotBlank()) {
        "$datePart $timePart"
    } else {
        datePart
    }

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

            Text(
                text = "Дата и время:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = UrbanBrown
            )

            Text(
                text = dateTimeText,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )

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
                text = "Контакт:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = UrbanBrown
            )

            Text(
                text = item.contact,
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
        }
    }
}