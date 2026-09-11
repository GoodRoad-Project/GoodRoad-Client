package com.example.goodroad.modules.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.modules.tasks.data.CompletedTaskDto
import com.example.goodroad.modules.tasks.data.TaskCreateReq
import com.example.goodroad.modules.tasks.data.TaskViewDto
import com.example.goodroad.modules.tasks.data.TasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import org.json.JSONObject

class TasksViewModel(
    private val repository: TasksRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<TaskViewDto>>(emptyList())
    val tasks: StateFlow<List<TaskViewDto>> = _tasks.asStateFlow()

    private val _completedTasks = MutableStateFlow<List<CompletedTaskDto>>(emptyList())
    val completedTasks: StateFlow<List<CompletedTaskDto>> = _completedTasks.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private fun extractErrorCode(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            JSONObject(errorBody).optString("code", null)
        } catch (_: Exception) {
            null
        }
    }

    private fun mapTaskError(e: Exception): String {
        return when (e) {
            is HttpException -> {
                val errorBody = e.response()?.errorBody()?.string()
                val errorCode = extractErrorCode(errorBody)

                when {
                    errorCode == "TASK_LOCATION_INCOMPLETE" -> "Укажите и широту, и долготу"
                    errorCode == "TASK_LOCATION_INVALID" -> "Некорректные координаты"
                    errorCode == "TASK_ACTIVITY_INVALID" -> "Некорректный тип активности"
                    errorCode == "TASK_GENERATION_BODY_EMPTY" -> "Укажите текущее местоположение"
                    errorCode == "TASK_TARGET_NOT_FOUND" -> "Цель задания не найдена"
                    errorCode == "TASK_TARGET_ID_INVALID" -> "Неверный ID цели"
                    errorCode == "USER_PHONE_NOT_FOUND" -> "Пользователь не найден"
                    else -> when (e.code()) {
                        400 -> "Некорректный запрос"
                        401 -> "Необходима авторизация"
                        403 -> "Доступ запрещен"
                        404 -> "Задание или цель не найдены"
                        409 -> "Конфликт при выполнении"
                        500 -> "Ошибка сервера"
                        else -> "Ошибка при выполнении операции"
                    }
                }
            }
            is IOException -> "Проверьте подключение к интернету"
            else -> e.message ?: "Неизвестная ошибка"
        }
    }

    fun loadTasks(
        activityType: String? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                _tasks.value = repository.loadTasks(
                    activityType = activityType,
                    latitude = latitude,
                    longitude = longitude
                )

            } catch (e: Exception) {
                _error.value = mapTaskError(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadCompletedTasks() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                _completedTasks.value =
                    repository.loadCompletedTasks()

            } catch (e: Exception) {
                _error.value = mapTaskError(e)
            } finally {
                _loading.value = false
            }
        }
    }
}