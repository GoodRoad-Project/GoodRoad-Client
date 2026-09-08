package com.example.goodroad.modules.moderator.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.modules.moderator.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import org.json.JSONObject

class VolunteerModerationViewModel(
    private val repo: VolunteerModerationRepository
) : ViewModel() {

    private val _applications = MutableStateFlow<List<VolunteerApplicationResp>>(emptyList())
    val applications: StateFlow<List<VolunteerApplicationResp>> = _applications

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private fun extractErrorCode(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            JSONObject(errorBody).optString("code", null)
        } catch (_: Exception) {
            null
        }
    }

    private fun mapVolunteerError(e: Exception): String {
        return when (e) {
            is HttpException -> {
                val errorBody = e.response()?.errorBody()?.string()
                val errorCode = extractErrorCode(errorBody)

                when {
                    errorCode == "APPLICATION_ALREADY_PROCESSED" -> "Заявка уже обработана"
                    errorCode == "APPLICATION_NOT_FOUND" -> "Заявка не найдена"
                    errorCode == "REJECT_REASON_EMPTY" -> "Укажите причину отказа"
                    errorCode == "MODERATOR_REQUIRED" -> "Недостаточно прав для модерации"
                    errorCode == "APPLICATION_ID_INVALID" -> "Неверный ID заявки"
                    errorCode == "USER_PHONE_NOT_FOUND" -> "Пользователь не найден"
                    else -> when (e.code()) {
                        400 -> "Некорректный запрос"
                        401 -> "Необходима авторизация"
                        403 -> "Доступ запрещен"
                        404 -> "Заявка не найдена"
                        409 -> "Заявка уже обработана"
                        500 -> "Ошибка сервера"
                        else -> "Ошибка при выполнении операции"
                    }
                }
            }
            is IOException -> "Проверьте подключение к интернету"
            else -> e.message ?: "Неизвестная ошибка"
        }
    }

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _applications.value = repo.getPendingApplications()
            } catch (e: Exception) {
                _error.value = mapVolunteerError(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun approve(id: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                repo.approve(id)
                load()
            } catch (e: Exception) {
                _error.value = mapVolunteerError(e)
            }
        }
    }

    fun reject(id: String, reason: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                repo.reject(id, reason)
                load()
            } catch (e: Exception) {
                _error.value = mapVolunteerError(e)
            }
        }
    }
}