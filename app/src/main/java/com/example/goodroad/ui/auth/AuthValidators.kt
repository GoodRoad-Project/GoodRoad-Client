package com.example.goodroad.ui.auth

private val cyrillicInputRegex = Regex("^[\\p{IsCyrillic} -]*$")
private val digitsInputRegex = Regex("^\\d*$")
private val cyrillicValueRegex = Regex("^(?=.*\\p{IsCyrillic})[\\p{IsCyrillic} -]+$")
private val russianPhoneDigitsRegex = Regex("^[78]\\d{10}$")

const val CYRILLIC_WARNING = "Допустимы только кириллица, пробел и -"
const val PHONE_CHARS_WARNING = "Допустимы только цифры. Знак + добавляется автоматически"
const val PHONE_FORMAT_WARNING = "Введите российский номер: 11 цифр, первая — 7 или 8"

fun isAllowedCyrillicInput(value: String): Boolean {
    return cyrillicInputRegex.matches(value)
}

fun isAllowedDigitsInput(value: String): Boolean {
    return digitsInputRegex.matches(value)
}

fun isValidRussianPhoneDigits(value: String): Boolean {
    return russianPhoneDigitsRegex.matches(value)
}

fun normalizeRequiredCyrillic(value: String): String? {
    val normalized = value.trim()
    if (normalized.isEmpty()) {
        return null
    }
    return if (cyrillicValueRegex.matches(normalized)) normalized else null
}

fun normalizeRequiredRussianPhone(value: String): String? {
    val normalized = value.trim()
    if (normalized.isEmpty()) {
        return null
    }
    return if (isValidRussianPhoneDigits(normalized)) normalized else null
}

fun formatPhoneForRequest(phoneDigits: String): String {
    return "+$phoneDigits"
}