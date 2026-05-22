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

@Composable
fun HelpScreen(
    helpViewModel: HelpViewModel,
    onCreateRequest: () -> Unit
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
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor =
                        UrbanBrown.copy(alpha = 0.08f)
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        "Нужна помощь в сопровождении маршрута?"
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(
                        "Здесь можно оставить заявку для волонтёра"
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            PrimaryButton(
                text = "Оставить заявку",
                onClick = onCreateRequest
            )
        }
    }
}