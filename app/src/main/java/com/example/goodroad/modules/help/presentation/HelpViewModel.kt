package com.example.goodroad.modules.help.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class HelpViewModel : ViewModel() {

    enum class RequestStatus {
        PENDING,
        APPROVED,
        REJECTED,
        COMPLETED
    }

    data class HelpRequest(
        val id: String,
        val routeStart: String,
        val routeEnd: String,
        val dateTime: String,
        val contact: String,
        val specialNotes: String,
        val comment: String,
        val status: RequestStatus = RequestStatus.PENDING
    )

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var successMessage = mutableStateOf<String?>(null)
        private set

    val requests = mutableStateListOf<HelpRequest>()

    init {
        requests.addAll(
            listOf(
                HelpRequest(
                    id = "1",
                    routeStart = "Дом",
                    routeEnd = "Поликлиника №3",
                    dateTime = "24.05.2026 14:30",
                    contact = "+7 999 123 45 67",
                    specialNotes = "Лифт обязателен, избегать лестниц",
                    comment = "Сопровождение пожилого человека",
                    status = RequestStatus.PENDING
                ),
                HelpRequest(
                    id = "2",
                    routeStart = "Метро",
                    routeEnd = "Городская больница",
                    dateTime = "25.05.2026 10:00",
                    contact = "@telegram_user",
                    specialNotes = "Только ровные дороги",
                    comment = "",
                    status = RequestStatus.APPROVED
                ),
                HelpRequest(
                    id = "3",
                    routeStart = "Дом",
                    routeEnd = "Магазин",
                    dateTime = "20.05.2026 12:00",
                    contact = "email@example.com",
                    specialNotes = "Без шума и толпы",
                    comment = "Покупка продуктов",
                    status = RequestStatus.COMPLETED
                )
            )
        )
    }

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

                if (routeStart.isBlank()) throw IllegalArgumentException("Укажите начало маршрута")
                if (routeEnd.isBlank()) throw IllegalArgumentException("Укажите конец маршрута")
                if (meetingDate.isBlank()) throw IllegalArgumentException("Укажите дату встречи")
                if (meetingTime.isBlank()) throw IllegalArgumentException("Укажите время встречи")
                if (contact.isBlank()) throw IllegalArgumentException("Укажите контакт")

                delay(600)

                val request = HelpRequest(
                    id = UUID.randomUUID().toString(),
                    routeStart = routeStart,
                    routeEnd = routeEnd,
                    dateTime = "$meetingDate $meetingTime",
                    contact = contact,
                    specialNotes = specialNotes,
                    comment = comment,
                    status = RequestStatus.PENDING
                )

                requests.add(request)

                successMessage.value = "Заявка отправлена"
                onSuccess()

            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Ошибка"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteRequest(id: String) {
        requests.removeAll { it.id == id }
    }

    fun clearMessages() {
        errorMessage.value = null
        successMessage.value = null
    }
}