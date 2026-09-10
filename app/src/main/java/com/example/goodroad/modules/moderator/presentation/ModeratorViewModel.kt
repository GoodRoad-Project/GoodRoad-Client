package com.example.goodroad.modules.moderator.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.modules.moderator.data.CreateModeratorReq
import com.example.goodroad.modules.moderator.data.ModeratorRepository
import com.example.goodroad.modules.moderator.data.ModeratorView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import org.json.JSONObject

class ModeratorViewModel(
    private val repository: ModeratorRepository
) : ViewModel() {

    private val _moderators = MutableStateFlow<List<ModeratorView>>(emptyList())
    val moderators: StateFlow<List<ModeratorView>> = _moderators

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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

    private fun mapModeratorError(e: Exception): String {
        return when (e) {
            is HttpException -> {
                val errorBody = e.response()?.errorBody()?.string()
                val errorCode = extractErrorCode(errorBody)

                when {
                    errorCode == "MODERATOR_ALREADY_EXISTS" -> "Модератор с таким номером уже существует"
                    errorCode == "PHONE_INVALID" -> "Некорректный номер телефона"
                    errorCode == "USER_PHONE_NOT_FOUND" -> "Пользователь не найден"
                    errorCode == "MODERATOR_ID_INVALID" -> "Неверный ID модератора"
                    errorCode == "MODERATOR_NOT_FOUND" -> "Модератор не найден"
                    errorCode == "CANNOT_DISABLE_ADMIN" -> "Нельзя отключить администратора"
                    else -> when (e.code()) {
                        400 -> "Некорректный запрос"
                        401 -> "Необходима авторизация"
                        403 -> "Доступ запрещен"
                        404 -> "Модератор не найден"
                        409 -> "Модератор с таким номером уже существует"
                        500 -> "Ошибка сервера"
                        else -> "Ошибка при выполнении операции"
                    }
                }
            }
            is IOException -> "Проверьте подключение к интернету"
            else -> e.message ?: "Неизвестная ошибка"
        }
    }

    fun loadModerators() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                _moderators.value = repository.getModerators()
            } catch (e: Exception) {
                _error.value = mapModeratorError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addModerator(
        firstName: String,
        lastName: String,
        phone: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.createModerator(
                    CreateModeratorReq(
                        firstName = firstName,
                        lastName = lastName,
                        phone = phone,
                        password = password
                    )
                )

                val updated = repository.getModerators()
                _moderators.value = updated

                onSuccess()

            } catch (e: Exception) {
                _error.value = mapModeratorError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun disableModerator(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.disableModerator(id)

                val updated = repository.getModerators()
                _moderators.value = updated

            } catch (e: Exception) {
                _error.value = mapModeratorError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}