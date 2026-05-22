package com.example.goodroad.modules.help.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.help.presentation.HelpViewModel
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.*
import androidx.compose.ui.text.font.FontWeight

@Composable
fun HelpScreen(
    helpViewModel: HelpViewModel,
    onCreateRequest: () -> Unit,
    onMyRequests: () -> Unit
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

            Text(
                text = "Помощь волонтёров",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = UrbanBrown.copy(alpha = 0.06f)
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Нуждаетесь в сопровождении?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = UrbanBrown
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Оставьте заявку для волонтёра, и вам помогут с маршрутом, передвижением или сопровождением.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            PrimaryButton(
                text = "Оставить заявку",
                onClick = onCreateRequest
            )

            Spacer(Modifier.height(12.dp))

            PrimaryButton(
                text = "Мои заявки",
                onClick = onMyRequests
            )
        }
    }
}