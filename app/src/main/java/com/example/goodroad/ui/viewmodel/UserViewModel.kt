package com.example.goodroad.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.user.*
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    var user = mutableStateOf<SettingsView?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var successMessage = mutableStateOf<String?>(null)
        private set

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
            successMessage.value = null

            try {
                val current = user.value

                val phoneToUpdate = phone?.takeIf { it.isNotBlank() }
                val hasPasswordChange = !oldPassword.isNullOrBlank() || !newPassword.isNullOrBlank()

                if (hasPasswordChange &&
                    (oldPassword.isNullOrBlank() || newPassword.isNullOrBlank())
                ) {
                    throw IllegalArgumentException("Для смены пароля заполните оба поля")
                }

                val req = UpdateUserReq(
                    firstName = firstName.takeIf { it != current?.firstName },
                    lastName = lastName.takeIf { it != current?.lastName },
                    photoUrl = photoUrl.takeIf { it != current?.photoUrl },
                    phone = phoneToUpdate
                )

                val hasChanges =
                    req.firstName != null ||
                            req.lastName != null ||
                            req.photoUrl != null ||
                            req.phone != null ||
                            hasPasswordChange

                if (!hasChanges) {
                    throw IllegalArgumentException("Нет изменений для сохранения")
                }

                if (req.phone != null) {
                    user.value = repository.updateCurrentUser(req)
                    ApiClient.updateCredentials(phone = req.phone)
                }

                if (hasPasswordChange) {
                    repository.changePassword(oldPassword!!, newPassword!!)
                    ApiClient.updateCredentials(password = newPassword)
                }

                successMessage.value = "Профиль обновлён"

            } catch (e: Exception) {
                errorMessage.value = mapUserError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun uploadAvatar(
        context: Context,
        uri: Uri,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val resolver = context.contentResolver
                val mimeType = resolver.getType(uri) ?: "image/*"

                val file = File.createTempFile("avatar", ".tmp", context.cacheDir)

                resolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { it.write(input.readBytes()) }
                }

                val body = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", file.name, body)

                val response = repository.uploadAvatar(part)
                    ?: throw IllegalStateException("Сервер не вернул фото")

                onSuccess(response.photoUrl)

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
            successMessage.value = null

            try {
                repository.deleteCurrentUser(DeleteAccountReq(password))

                ApiClient.clearCredentials()
                user.value = null
                isDeleted = true

                onSuccess()

            } catch (e: Exception) {
                errorMessage.value = mapDeleteError(e)
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
        successMessage.value = null
        onSuccess()
    }

    fun clearMessages() {
        errorMessage.value = null
        successMessage.value = null
    }

    fun clearSuccessMessage() {
        successMessage.value = null
    }

    private fun mapUserError(e: Exception): String {
        return when (e) {
            is IllegalArgumentException -> e.message ?: "Некорректные данные"
            is HttpException -> when (e.code()) {
                400 -> "Некорректные данные"
                401 -> "Не авторизован"
                403 -> "Нет доступа"
                404 -> "Пользователь не найден"
                409 -> "Телефон уже используется"
                500 -> "Ошибка сервера"
                else -> "Ошибка"
            }
            is IOException -> "Проверьте интернет"
            else -> e.message ?: "Неизвестная ошибка"
        }
    }

    private fun mapDeleteError(e: Exception): String {
        return when (e) {
            is HttpException -> when (e.code()) {
                400 -> "Неверный пароль"
                401 -> "Неверный пароль"
                403 -> "Нет прав"
                404 -> "Аккаунт не найден"
                409 -> "Нельзя удалить аккаунт"
                500 -> "Ошибка сервера"
                else -> "Ошибка удаления"
            }
            is IOException -> "Проверьте интернет"
            else -> e.message ?: "Неизвестная ошибка"
        }
    }
}