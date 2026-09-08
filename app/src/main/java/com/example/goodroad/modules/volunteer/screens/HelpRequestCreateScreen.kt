package com.example.goodroad.modules.volunteer.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.volunteer.presentation.VolunteerViewModel
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.fields.PhoneField
import com.example.goodroad.ui.fields.PhoneValidation
import com.example.goodroad.ui.fields.validatePhone
import com.example.goodroad.ui.fields.toFormattedPhone
import com.example.goodroad.ui.theme.AlertRed
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown
import java.time.LocalDateTime

@Composable
fun HelpRequestCreateScreen(
    helpViewModel: VolunteerViewModel,
    onCreated: () -> Unit,
    onBack: () -> Unit
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
    var socialNicknameError by rememberSaveable { mutableStateOf<String?>(null) }
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

        meetingDateError = when {
            meetingDate.length != 8 -> {
                valid = false
                "Введите дату полностью (ДДММГГГГ)"
            }
            !isValidDate(meetingDate) -> {
                valid = false
                "Некорректная дата"
            }
            else -> null
        }

        meetingTimeError = when {
            meetingTime.length != 4 -> {
                valid = false
                "Введите время полностью (ЧЧММ)"
            }
            !isValidTime(meetingTime) -> {
                valid = false
                "Некорректное время"
            }
            else -> null
        }

        if (meetingDateError == null && meetingTimeError == null) {
            val dateTimeError = isDateTimeInPast(meetingDate, meetingTime)
            if (dateTimeError != null) {
                valid = false
                meetingDateError = dateTimeError
            }
        }

        val phoneValidation = validatePhone(contact)
        contactError = when (phoneValidation) {
            is PhoneValidation.Empty -> {
                valid = false
                "Обязательное поле"
            }
            is PhoneValidation.InvalidChars -> {
                valid = false
                "Телефон должен содержать только цифры"
            }
            is PhoneValidation.InvalidFormat -> {
                valid = false
                "Введите корректный номер телефона"
            }
            is PhoneValidation.Valid -> null
        }

        socialNicknameError = null

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

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Новая заявка",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(24.dp))

            LineField(
                value = routeStart,
                onValueChange = {
                    routeStart = it
                    routeStartError = null
                },
                label = "Начало маршрута *",
                icon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = UrbanBrown
                    )
                },
                error = routeStartError
            )

            Spacer(Modifier.height(12.dp))

            LineField(
                value = routeEnd,
                onValueChange = {
                    routeEnd = it
                    routeEndError = null
                },
                label = "Конец маршрута *",
                icon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = UrbanBrown
                    )
                },
                error = routeEndError
            )

            Spacer(Modifier.height(12.dp))

            LineField(
                value = meetingDate,
                onValueChange = { input ->
                    meetingDate = input.filter { it.isDigit() }.take(8)
                    meetingDateError = null
                },
                label = "Дата (ДД.ММ.ГГГГ) *",
                icon = {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = UrbanBrown
                    )
                },
                error = meetingDateError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                visualTransformation = DateVisualTransformation
            )

            Spacer(Modifier.height(12.dp))

            LineField(
                value = meetingTime,
                onValueChange = { input ->
                    meetingTime = input.filter { it.isDigit() }.take(4)
                    meetingTimeError = null
                },
                label = "Время (ЧЧ:ММ) *",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = UrbanBrown
                    )
                },
                error = meetingTimeError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                visualTransformation = TimeVisualTransformation
            )

            Spacer(Modifier.height(12.dp))

            PhoneField(
                value = contact,
                onValueChange = {
                    contact = it
                    contactError = null
                },
                label = "Номер телефона *",
                showPrefix = false,
                showIcon = true,
                allowEmptyWarning = false
            )

            Spacer(Modifier.height(12.dp))

            LineField(
                value = socialNickname,
                onValueChange = {
                    socialNickname = it
                    socialNicknameError = null
                },
                label = "Telegram / ВК / доп.контакт с указанием, к чему относится",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = UrbanBrown
                    )
                },
                error = socialNicknameError
            )

            Spacer(Modifier.height(12.dp))

            LineField(
                value = comment,
                onValueChange = {
                    comment = it
                    commentError = null
                },
                label = "Комментарий *",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = null,
                        tint = UrbanBrown
                    )
                },
                error = commentError,
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

                    val phoneValidation = validatePhone(contact)
                    val formattedPhone = when (phoneValidation) {
                        is PhoneValidation.Valid -> phoneValidation.toFormattedPhone()!!
                        else -> contact
                    }

                    helpViewModel.createRequest(
                        routeStart = routeStart,
                        routeEnd = routeEnd,
                        meetingDate = formattedDate,
                        meetingTime = formattedTime,
                        contact = formattedPhone,
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

@Composable
private fun LineField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: @Composable (() -> Unit)? = null,
    error: String? = null,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    minLines: Int = 1
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = {
            Text(
                text = label,
                color = if (error != null) AlertRed else UrbanBrown
            )
        },
        leadingIcon = icon,
        singleLine = minLines == 1,
        minLines = minLines,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        isError = error != null,
        supportingText = {
            error?.let {
                Text(
                    text = it,
                    color = AlertRed
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = BackgroundLight,
            unfocusedContainerColor = BackgroundLight,
            disabledContainerColor = BackgroundLight,
            errorContainerColor = BackgroundLight,
            focusedIndicatorColor = SafeGreen,
            unfocusedIndicatorColor = UrbanBrown.copy(alpha = 0.5f),
            errorIndicatorColor = AlertRed,
            cursorColor = SafeGreen,
            focusedLabelColor = UrbanBrown,
            unfocusedLabelColor = UrbanBrown
        )
    )
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

private fun isValidDate(dateStr: String): Boolean {
    if (dateStr.length != 8) return false

    return try {
        val day = dateStr.substring(0, 2).toInt()
        val month = dateStr.substring(2, 4).toInt()
        val year = dateStr.substring(4, 8).toInt()

        if (year !in 2020..2100 || month !in 1..12) return false

        val daysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 29 else 28
            else -> return false
        }

        day in 1..daysInMonth
    } catch (_: Exception) {
        false
    }
}

private fun isValidTime(timeStr: String): Boolean {
    if (timeStr.length != 4) return false

    return try {
        val hour = timeStr.substring(0, 2).toInt()
        val minute = timeStr.substring(2, 4).toInt()
        hour in 0..23 && minute in 0..59
    } catch (_: Exception) {
        false
    }
}

private fun isDateTimeInPast(dateStr: String, timeStr: String): String? {
    if (dateStr.length != 8 || timeStr.length != 4) return null

    return try {
        val day = dateStr.substring(0, 2).toInt()
        val month = dateStr.substring(2, 4).toInt()
        val year = dateStr.substring(4, 8).toInt()
        val hour = timeStr.substring(0, 2).toInt()
        val minute = timeStr.substring(2, 4).toInt()

        val dateTime = LocalDateTime.of(year, month, day, hour, minute)
        val now = LocalDateTime.now()

        when {
            dateTime.isBefore(now) -> "Дата и время не могут быть в прошлом"
            dateTime.isBefore(now.plusMinutes(5)) -> "Выберите время с запасом минимум 5 минут"
            else -> null
        }
    } catch (_: Exception) {
        null
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
