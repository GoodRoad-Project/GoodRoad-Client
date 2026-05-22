package com.example.goodroad.modules.help.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HelpViewModel : ViewModel() {

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var successMessage = mutableStateOf<String?>(null)
        private set

    fun createRequest(
        routeStart: String,
        routeEnd: String,
        meetingDate: String,
        meetingTime: String,
        contact: String,
        specialNotes: String,
        comment: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            isLoading.value = true
            errorMessage.value = null
            successMessage.value = null

            try {

                if (routeStart.isBlank()) {
                    throw IllegalArgumentException("Укажите начало маршрута")
                }

                if (routeEnd.isBlank()) {
                    throw IllegalArgumentException("Укажите конец маршрута")
                }

                if (meetingDate.isBlank()) {
                    throw IllegalArgumentException("Укажите дату встречи")
                }

                if (meetingTime.isBlank()) {
                    throw IllegalArgumentException("Укажите время встречи")
                }

                if (contact.isBlank()) {
                    throw IllegalArgumentException("Укажите контакт для связи")
                }

                delay(700)

                successMessage.value = "Заявка отправлена"
                onSuccess()

            } catch (e: Exception) {

                errorMessage.value = e.message ?: "Ошибка"

            } finally {

                isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        errorMessage.value = null
        successMessage.value = null
    }
}