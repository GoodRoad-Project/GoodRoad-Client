package com.example.goodroad.modules.volunteer.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.volunteer.presentation.VolunteerViewModel
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.BackgroundLight
import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.ui.text.input.TextRange

@Composable
fun HelpRequestCreateScreen(
    helpViewModel: VolunteerViewModel,
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
    var socialNickname by rememberSaveable { mutableStateOf("") }
    var comment by rememberSaveable { mutableStateOf("") }

    var routeStartError by rememberSaveable { mutableStateOf<String?>(null) }
    var routeEndError by rememberSaveable { mutableStateOf<String?>(null) }
    var meetingDateError by rememberSaveable { mutableStateOf<String?>(null) }
    var meetingTimeError by rememberSaveable { mutableStateOf<String?>(null) }
    var contactError by rememberSaveable { mutableStateOf<String?>(null) }
    var commentError by rememberSaveable { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

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

        meetingDateError = if (meetingDate.length != 8) {
            valid = false
            "Введите дату полностью"
        } else null

        meetingTimeError = if (meetingTime.length != 4) {
            valid = false
            "Введите время полностью"
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
                onValueChange = { input ->
                    meetingDate = input.filter { ch -> ch.isDigit() }.take(8)
                    meetingDateError = null
                },
                label = { Text("Дата (ДД.ММ.ГГГГ) *") },
                isError = meetingDateError != null,
                supportingText = { meetingDateError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = DateVisualTransformation
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = meetingTime,
                onValueChange = { input ->
                    meetingTime = input.filter { ch -> ch.isDigit() }.take(4)
                    meetingTimeError = null
                },
                label = { Text("Время (ЧЧ:ММ) *") },
                isError = meetingTimeError != null,
                supportingText = { meetingTimeError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = TimeVisualTransformation
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
                value = socialNickname,
                onValueChange = { socialNickname = it },
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
                    val formattedDate = if (meetingDate.length == 8) {
                        "${meetingDate.substring(0, 2)}-${meetingDate.substring(2, 4)}-${meetingDate.substring(4, 8)}"
                    } else {
                        meetingDate
                    }

                    val formattedTime = if (meetingTime.length == 4) {
                        "${meetingTime.substring(0, 2)}:${meetingTime.substring(2, 4)}"
                    } else {
                        meetingTime
                    }

                    helpViewModel.createRequest(
                        routeStart = routeStart,
                        routeEnd = routeEnd,
                        meetingDate = formattedDate,
                        meetingTime = formattedTime,
                        contact = contact,
                        socialNickname = socialNickname,
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

private object DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(8)

        val formatted = buildString {
            for (i in digits.indices) {
                append(digits[i])
                if (i == 1 || i == 3) append('.')
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, digits.length)
                return when {
                    safeOffset <= 2 -> safeOffset
                    safeOffset <= 4 -> safeOffset + 1
                    else -> safeOffset + 2
                }.coerceAtMost(formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, formatted.length)
                return when {
                    safeOffset <= 2 -> safeOffset
                    safeOffset <= 5 -> safeOffset - 1
                    else -> safeOffset - 2
                }.coerceIn(0, digits.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

private object TimeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(4)

        val formatted = buildString {
            for (i in digits.indices) {
                append(digits[i])
                if (i == 1) append(':')
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, digits.length)
                return when {
                    safeOffset <= 2 -> safeOffset
                    else -> safeOffset + 1
                }.coerceAtMost(formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, formatted.length)
                return when {
                    safeOffset <= 2 -> safeOffset
                    else -> safeOffset - 1
                }.coerceIn(0, digits.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
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