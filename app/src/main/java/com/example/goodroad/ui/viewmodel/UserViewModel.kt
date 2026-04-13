package com.example.goodroad.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.user.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    var user = mutableStateOf<SettingsView?>(null)
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    var successMessage = mutableStateOf<String?>(null)

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

                successMessage.value = "Профиль успешно сохранен"
            } catch (e: Exception) {
                errorMessage.value = mapUserError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun uploadAvatar(context: Context, uri: Uri, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            successMessage.value = null

            try {
                val resolver = context.contentResolver
                val mimeType = resolver.getType(uri) ?: "image/*"
                val extension = MimeTypeMap.resolveExtension(mimeType)
                val tempFile = File.createTempFile("avatar_upload", extension, context.cacheDir)
                resolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                } ?: throw IllegalArgumentException("Не удалось прочитать выбранный файл")

                val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", tempFile.name, requestBody)
                val response = repository.uploadAvatar(part)
                    ?: throw IllegalStateException("Сервер не вернул ссылку на фото")

                onSuccess(response.photoUrl)
                tempFile.delete()
            } catch (e: Exception) {
                errorMessage.value = mapUserError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun clearSuccessMessage() {
        successMessage.value = null
    }

    fun clearMessages() {
        errorMessage.value = null
        successMessage.value = null
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
        successMessage.value = null
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

    private object MimeTypeMap {
        fun resolveExtension(mimeType: String): String {
            return when (mimeType) {
                "image/jpeg" -> ".jpg"
                "image/png" -> ".png"
                "image/webp" -> ".webp"
                else -> ".tmp"
            }
        }
    }
}