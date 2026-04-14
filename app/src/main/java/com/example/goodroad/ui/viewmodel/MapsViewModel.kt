package com.example.goodroad.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.goodroad.data.obstacle.*
import kotlinx.coroutines.launch
import retrofit2.*
import java.io.*

class MapsViewModel(private val repository: ObstacleRepository) : ViewModel() {

    var policies = mutableStateOf<List<ObstaclePolicyItem>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set

    var isSaving = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var successMessage = mutableStateOf<String?>(null)
        private set

    fun loadPolicies() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                policies.value = repository.getUserObstaclePolicies()
            } catch (e: Exception) {
                errorMessage.value = mapObstacleError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun savePolicies(items: List<ObstaclePolicyItem>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isSaving.value = true
            errorMessage.value = null
            successMessage.value = null

            try {
                val req = ReplaceObstaclePolicyReq(items)
                policies.value = repository.replaceUserObstaclePolicies(req)
                successMessage.value = "Настройки препятствий сохранены"
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = mapObstacleError(e)
            } finally {
                isSaving.value = false
            }
        }
    }

    fun clearMessages() {
        errorMessage.value = null
        successMessage.value = null
    }

    private fun mapObstacleError(e: Exception): String {
        return when (e) {
            is HttpException -> when (e.code()) {
                400 -> "Проверьте выбранные препятствия и уровень тяжести"
                401 -> "Вы не авторизованы"
                403 -> "Нет прав для выполнения действия"
                500 -> "Сервер временно недоступен"
                else -> "Не удалось сохранить настройки препятствий"
            }
            is IOException -> "Проверьте подключение к интернету"
            else -> e.message ?: "Неизвестная ошибка"
        }
    }
}
