package com.example.goodroad.ui.user.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.UrbanBrown

@Composable
fun VolunteerApplicationFormScreen(
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    var motivation by rememberSaveable { mutableStateOf("") }
    var dobroLink by rememberSaveable { mutableStateOf("") }
    var contacts by rememberSaveable { mutableStateOf("") }
    var certificates by rememberSaveable { mutableStateOf("") }

    var error by rememberSaveable { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {

            UserDecor()

            Text(
                text = "Заявка на волонтёрство",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black
            )

            Spacer(Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = UrbanBrown.copy(alpha = 0.06f)
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Заполните заявку и мы свяжемся с вами в течение недели",
                        style = MaterialTheme.typography.bodyLarge,
                        color = UrbanBrown
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = motivation,
                onValueChange = {
                    motivation = it
                    error = null
                },
                label = { Text("Мотивация") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = dobroLink,
                onValueChange = {
                    dobroLink = it
                    error = null
                },
                label = { Text("Ссылка на dobro.ru") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = contacts,
                onValueChange = {
                    contacts = it
                    error = null
                },
                label = { Text("Контакты (телефон / telegram / ВК)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = certificates,
                onValueChange = {
                    certificates = it
                    error = null
                },
                label = { Text("Сертификаты, что я молодец!!") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = "Отправить заявку",
                onClick = {
                    if (motivation.isBlank() || dobroLink.isBlank() || contacts.isBlank()) {
                        error = "Заполните обязательные поля"
                        return@PrimaryButton
                    }
                    onSubmitted()
                }
            )
        }
    }
}