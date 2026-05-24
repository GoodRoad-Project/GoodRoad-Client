package com.example.goodroad.modules.volunteer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.volunteer.presentation.VolunteerViewModel
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.BackgroundLight

@Composable
fun HelpRequestCreateScreen(
    helpViewModel: VolunteerViewModel,
    onBack: () -> Unit,
    onCreated: () -> Unit
) {

    val isLoading by helpViewModel.isLoading
    val error by helpViewModel.errorMessage
    val success by helpViewModel.successMessage

    var routeStart by rememberSaveable { mutableStateOf("") }
    var routeEnd by rememberSaveable { mutableStateOf("") }
    var meetingDate by rememberSaveable { mutableStateOf("") }
    var meetingTime by rememberSaveable { mutableStateOf("") }
    var contact by rememberSaveable { mutableStateOf("") }
    var specialNotes by rememberSaveable { mutableStateOf("") }
    var comment by rememberSaveable { mutableStateOf("") }

    val scrollState = rememberScrollState()

    fun formatDate(input: String): String {
        val digits = input.filter { it.isDigit() }.take(8)
        val sb = StringBuilder()

        for (i in digits.indices) {
            sb.append(digits[i])
            if (i == 1 || i == 3) sb.append('.')
        }

        return sb.toString()
    }

    fun formatTime(input: String): String {
        val digits = input.filter { it.isDigit() }.take(4)
        val sb = StringBuilder()

        for (i in digits.indices) {
            sb.append(digits[i])
            if (i == 1) sb.append(':')
        }

        return sb.toString()
    }

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
                text = "Новая заявка",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = routeStart,
                onValueChange = { routeStart = it },
                label = {
                    Text("Начало маршрута с сопровождением")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = routeEnd,
                onValueChange = { routeEnd = it },
                label = {
                    Text("Конец маршрута с сопровождением")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = meetingDate,
                onValueChange = {
                    val digits = it.filter { ch -> ch.isDigit() }.take(8)
                    meetingDate = formatDate(digits)
                },
                label = {
                    Text("Дата (ДД.ММ.ГГГГ)")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = meetingTime,
                onValueChange = {
                    val digits = it.filter { ch -> ch.isDigit() }.take(4)
                    meetingTime = formatTime(digits)
                },
                label = {
                    Text("Время (ЧЧ:ММ)")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = contact,
                onValueChange = {
                    contact = it.filter { ch ->
                        ch.isLetterOrDigit() || ch in "+@._-() "
                    }
                },
                label = {
                    Text("Номер телефона")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = specialNotes,
                onValueChange = {
                    specialNotes = it
                },
                label = {
                    Text("Telegram / ВК / доп. контакт")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = {
                    comment = it
                },
                label = {
                    Text("Комментарий")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                minLines = 2
            )

            if (error != null) {

                Spacer(Modifier.height(12.dp))

                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (success != null) {

                Spacer(Modifier.height(12.dp))

                Text(
                    text = success!!,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = if (isLoading)
                    "Отправка..."
                else
                    "Отправить заявку",

                onClick = {

                    helpViewModel.createRequest(
                        routeStart = routeStart,
                        routeEnd = routeEnd,
                        meetingDate = meetingDate,
                        meetingTime = meetingTime,
                        contact = contact,
                        specialNotes = specialNotes,
                        comment = comment
                    ) {
                        helpViewModel.clearMessages()
                        onCreated()
                    }
                }
            )
        }
    }
}