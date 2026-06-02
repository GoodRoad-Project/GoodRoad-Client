package com.example.goodroad.modules.moderator.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton

@Composable
fun VolunteerManagementScreen(
    onBack: () -> Unit
) {
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

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Волонтёры",
                style = MaterialTheme.typography.headlineLarge,
                color = UrbanBrown
            )

            Spacer(Modifier.height(24.dp))

            Text("Здесь будет список волонтёров")

            Spacer(Modifier.weight(1f))

            PrimaryButton(
                text = "Назад в профиль",
                onClick = onBack
            )
        }
    }
}