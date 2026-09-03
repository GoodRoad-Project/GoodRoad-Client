package com.example.goodroad.ui.fields

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType
import com.example.goodroad.ui.theme.*
import com.example.goodroad.validation.*

@Composable
fun PhoneField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    maxLength: Int = PHONE_MAX_LENGTH,
    showPrefix: Boolean = true,
    showIcon: Boolean = true,
    validateOnInput: Boolean = true,
    allowEmptyWarning: Boolean = false
) {
    val validation = remember(value) {
        validatePhone(value, allowEmpty = allowEmptyWarning)
    }

    val warning = when (validation) {
        is PhoneValidation.InvalidChars -> PHONE_CHARS_WARNING
        is PhoneValidation.InvalidFormat -> PHONE_FORMAT_WARNING
        is PhoneValidation.Empty -> if (allowEmptyWarning) "Обязательное поле" else null
        is PhoneValidation.Valid -> null
    }

    PlainField(
        value = value,
        onValueChange = { newValue ->
            if (validateOnInput && newValue.any { !it.isDigit() }) {
                return@PlainField
            }

            val digitsOnly = newValue.filter { it.isDigit() }
            val limited = digitsOnly.take(maxLength)

            if (limited != value) {
                onValueChange(limited)
            }
        },
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        icon = if (showIcon) {
            {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = UrbanBrown
                )
            }
        } else null,
        warning = warning,
        maxLength = maxLength,
        prefix = if (showPrefix) {
            { androidx.compose.material3.Text("+") }
        } else null
    )
}

fun validatePhone(
    phone: String,
    allowEmpty: Boolean = false
): PhoneValidation {
    val trimmed = phone.trim()

    if (trimmed.isEmpty()) {
        return PhoneValidation.Empty
    }

    if (!trimmed.all { it.isDigit() }) {
        return PhoneValidation.InvalidChars
    }

    if (trimmed.length > PHONE_MAX_LENGTH) {
        return PhoneValidation.InvalidFormat
    }

    if (trimmed.first() !in listOf('7', '8')) {
        return PhoneValidation.InvalidFormat
    }

    if (!isValidRussianPhoneDigits(trimmed)) {
        return PhoneValidation.InvalidFormat
    }

    val normalized = normalizeRequiredRussianPhone(trimmed)
    return if (normalized != null) {
        PhoneValidation.Valid(normalized)
    } else {
        PhoneValidation.InvalidFormat
    }
}

sealed class PhoneValidation {
    data object Empty : PhoneValidation()
    data object InvalidChars : PhoneValidation()
    data object InvalidFormat : PhoneValidation()
    data class Valid(val phoneDigits: String) : PhoneValidation()
}

fun PhoneValidation.toFormattedPhone(): String? {
    return when (this) {
        is PhoneValidation.Valid -> formatPhoneForRequest(phoneDigits)
        else -> null
    }
}