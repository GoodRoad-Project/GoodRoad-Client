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

    var routeStartError by rememberSaveable { mutableStateOf<String?>(null) }
    var routeEndError by rememberSaveable { mutableStateOf<String?>(null) }
    var meetingDateError by rememberSaveable { mutableStateOf<String?>(null) }
    var meetingTimeError by rememberSaveable { mutableStateOf<String?>(null) }
    var contactError by rememberSaveable { mutableStateOf<String?>(null) }
    var commentError by rememberSaveable { mutableStateOf<String?>(null) }

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

    fun validate(): Boolean {
        var valid = true

        routeStartError = if (routeStart.isBlank()) {
            valid = false
            "Обязательное поле"
        } else null

        routeEndError = if (routeEnd.isBlank()) {
            valid = false
            "Обязательное поле"
        } else null

        meetingDateError = if (meetingDate.isBlank()) {
            valid = false
            "Обязательное поле"
        } else null

        meetingTimeError = if (meetingTime.isBlank()) {
            valid = false
            "Обязательное поле"
        } else null

        contactError = if (contact.isBlank()) {
            valid = false
            "Обязательное поле"
        } else null

        commentError = if (comment.isBlank()) {
            valid = false
            "Обязательное поле"
        } else null

        return valid
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
                onValueChange = {
                    routeStart = it
                    routeStartError = null
                },
                label = { Text("Начало маршрута *") },
                isError = routeStartError != null,
                supportingText = { routeStartError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = routeEnd,
                onValueChange = {
                    routeEnd = it
                    routeEndError = null
                },
                label = { Text("Конец маршрута *") },
                isError = routeEndError != null,
                supportingText = { routeEndError?.let { Text(it) } },
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
                    meetingDateError = null
                },
                label = { Text("Дата (ДД.ММ.ГГГГ) *") },
                isError = meetingDateError != null,
                supportingText = { meetingDateError?.let { Text(it) } },
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
                    meetingTimeError = null
                },
                label = { Text("Время (ЧЧ:ММ) *") },
                isError = meetingTimeError != null,
                supportingText = { meetingTimeError?.let { Text(it) } },
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
                    contactError = null
                },
                label = { Text("Номер телефона *") },
                isError = contactError != null,
                supportingText = { contactError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = specialNotes,
                onValueChange = { specialNotes = it },
                label = { Text("Telegram / ВК / доп. контакт") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = {
                    comment = it
                    commentError = null
                },
                label = { Text("Комментарий *") },
                isError = commentError != null,
                supportingText = { commentError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                minLines = 2
            )

            if (error != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = mapErrorToUserMessage(error),
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
                text = if (isLoading) "Отправка..." else "Отправить заявку",
                onClick = {
                    if (!validate()) return@PrimaryButton

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

private fun mapErrorToUserMessage(error: String?): String {
    val msg = error?.lowercase() ?: return "Произошла неизвестная ошибка"

    return when {
        msg.contains("timeout") -> "Сервер не отвечает. Попробуйте позже"
        msg.contains("unable to resolve host") -> "Нет соединения с интернетом"
        msg.contains("400") -> "Проверьте заполнение обязательных полей"
        msg.contains("401") -> "Необходима повторная авторизация"
        msg.contains("403") -> "У вас нет доступа к этой операции"
        msg.contains("404") -> "Сервис временно недоступен"
        msg.contains("500") -> "Ошибка сервера. Попробуйте позже"
        msg.contains("validation") -> "Некоторые поля заполнены неверно"
        msg.contains("illegal") -> "Проверьте введённые данные"
        else -> "Не удалось отправить заявку. Попробуйте ещё раз"
    }
}