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
                errorMessage.value = mapError(e)
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

                val req = UpdateUserReq(
                    firstName = firstName.takeIf { it != current?.firstName },
                    lastName = lastName.takeIf { it != current?.lastName },
                    photoUrl = photoUrl.takeIf { it != current?.photoUrl },
                    phone = phoneToUpdate
                )

                if (hasPasswordChange) {
                    if (oldPassword.isNullOrBlank() || newPassword.isNullOrBlank()) {
                        throw IllegalArgumentException("Заполните оба пароля")
                    }
                }

                val hasChanges =
                    req.firstName != null ||
                            req.lastName != null ||
                            req.photoUrl != null ||
                            req.phone != null ||
                            hasPasswordChange

                if (!hasChanges) {
                    throw IllegalArgumentException("Нет изменений")
                }

                if (req.firstName != null ||
                    req.lastName != null ||
                    req.photoUrl != null ||
                    req.phone != null
                ) {
                    user.value = repository.updateCurrentUser(req)
                    req.phone?.let {
                        ApiClient.updateCredentials(phone = it)
                    }
                }

                if (hasPasswordChange) {
                    repository.changePassword(oldPassword!!, newPassword!!)
                    ApiClient.updateCredentials(password = newPassword)
                }

                successMessage.value = "Профиль обновлён"

            } catch (e: Exception) {
                errorMessage.value = mapError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun uploadAvatar(context: Context, uri: Uri, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true

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
                    ?: throw IllegalStateException("Нет ответа")

                onSuccess(response.photoUrl)

            } catch (e: Exception) {
                errorMessage.value = mapError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteUser(password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true

            try {
                repository.deleteCurrentUser(DeleteAccountReq(password))
                ApiClient.clearCredentials()
                user.value = null
                isDeleted = true
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = mapError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        ApiClient.clearCredentials()
        user.value = null
        isDeleted = false
        onSuccess()
    }

    private fun mapError(e: Exception): String {
        return when (e) {
            is IllegalArgumentException -> e.message ?: "Ошибка"
            is HttpException -> when (e.code()) {
                400 -> "Ошибка данных"
                401 -> "Не авторизован"
                403 -> "Нет доступа"
                404 -> "Не найдено"
                409 -> "Конфликт данных"
                500 -> "Ошибка сервера"
                else -> "Ошибка"
            }
            is IOException -> "Нет интернета"
            else -> e.message ?: "Ошибка"
        }
    }
}