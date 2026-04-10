package com.example.goodroad.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.user.DeleteAccountReq
import com.example.goodroad.data.user.SettingsView
import com.example.goodroad.data.user.UpdateUserReq
import com.example.goodroad.data.user.UserRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    var user = mutableStateOf<SettingsView?>(null)
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    var isDeleted = false
        private set

    fun getCurrentUser() {
        if (isDeleted) return

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                user.value = repository.getCurrentUser()
            } catch (e: Exception) {
                errorMessage.value = mapUserError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateUser(
        firstName: String,
        lastName: String,
        photoUrl: String? = null,
        phone: String? = null,
        oldPassword: String? = null,
        newPassword: String? = null
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val current = user.value
                val phoneToUpdate = phone?.takeIf { it.isNotBlank() }
                val hasPasswordChange = !oldPassword.isNullOrBlank() || !newPassword.isNullOrBlank()

                if (hasPasswordChange && (oldPassword.isNullOrBlank() || newPassword.isNullOrBlank())) {
                    throw IllegalArgumentException("Для смены пароля заполните оба поля")
                }

                val req = UpdateUserReq(
                    firstName = firstName.takeIf { it != current?.firstName },
                    lastName = lastName.takeIf { it != current?.lastName },
                    photoUrl = photoUrl.takeIf { it != current?.photoUrl },
                    phone = phoneToUpdate
                )

                val hasProfileChanges = req.firstName != null ||
                        req.lastName != null ||
                        req.photoUrl != null ||
                        req.phone != null

                if (!hasProfileChanges && !hasPasswordChange) {
                    throw IllegalArgumentException("Нет изменений для сохранения")
                }

                if (hasProfileChanges) {
                    user.value = repository.updateCurrentUser(req)
                    if (req.phone != null) {
                        ApiClient.updateCredentials(phone = req.phone)
                    }
                }

                if (hasPasswordChange) {
                    repository.changePassword(oldPassword!!, newPassword!!)
                    ApiClient.updateCredentials(password = newPassword)
                }
            } catch (e: Exception) {
                errorMessage.value = mapUserError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteUser(password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                repository.deleteCurrentUser(DeleteAccountReq(password))
                ApiClient.clearCredentials()
                user.value = null
                isDeleted = true
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = mapUserError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        ApiClient.clearCredentials()
        user.value = null
        isDeleted = false
        errorMessage.value = null
        onSuccess()
    }

    private fun mapUserError(e: Exception): String {
        return when (e) {
            is IllegalArgumentException -> e.message ?: "Некорректные данные"
            is HttpException -> when (e.code()) {
                400 -> "Некорректные данные профиля"
                401 -> "Вы не авторизованы"
                403 -> "Нет прав для выполнения действия"
                404 -> "Пользователь не найден"
                409 -> "Телефон уже используется другим пользователем"
                500 -> "Сервер временно недоступен"
                else -> "Ошибка операции"
            }
            is IOException -> "Проверьте подключение к интернету"
            else -> e.message ?: "Неизвестная ошибка"
        }
    }
}