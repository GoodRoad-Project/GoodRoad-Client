package com.example.goodroad.modules.volunteer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.volunteer.presentation.VolunteerViewModel
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.theme.*

@Composable
fun VolunteerScreen(
    helpViewModel: VolunteerViewModel,
    onCreateRequest: () -> Unit,
    onMyRequests: () -> Unit,
    onVolunteerFeed: () -> Unit,
    onMyWards: () -> Unit
) {

    val menuState = helpViewModel.volunteerMenu.value
    val isVolunteer = menuState?.isVolunteer == true

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
                text = "Помощь волонтёров",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(16.dp))

            SectionTitle("Помощь")

            ServiceCard(
                title = "Оставить заявку",
                description = "Получите сопровождение от волонтёра",
                onClick = onCreateRequest
            )

            Spacer(Modifier.height(12.dp))

            ServiceCard(
                title = "Мои заявки",
                description = "Посмотреть статус и историю заявок",
                onClick = onMyRequests
            )

            Spacer(Modifier.height(20.dp))

            if (isVolunteer) {

                SectionTitle("Волонтёрство")

                ServiceCard(
                    title = "Лента волонтёра",
                    description = "Доступные заявки для помощи",
                    onClick = onVolunteerFeed
                )

                Spacer(Modifier.height(12.dp))

                ServiceCard(
                    title = "Мои подопечные",
                    description = "Люди, которым вы помогаете",
                    onClick = onMyWards
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = UrbanBrown
    )

    Spacer(Modifier.height(8.dp))
}

@Composable
private fun ServiceCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWarm
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}