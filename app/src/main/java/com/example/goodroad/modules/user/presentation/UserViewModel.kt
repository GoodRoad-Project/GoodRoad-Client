package com.example.goodroad.modules.user.presentation

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.modules.user.data.ChangePhoneReq
import com.example.goodroad.modules.user.data.DeleteAccountReq
import com.example.goodroad.modules.user.data.SettingsView
import com.example.goodroad.modules.user.data.UpdateUserReq
import com.example.goodroad.modules.user.data.UserRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
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
        photoUrl: String? = null
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            successMessage.value = null

            try {
                val current = user.value

                val req = UpdateUserReq(
                    firstName = firstName.takeIf {
                        it != current?.firstName
                    },
                    lastName = lastName.takeIf {
                        it != current?.lastName
                    },
                    photoUrl = photoUrl.takeIf {
                        it != current?.photoUrl
                    }
                )

                val hasChanges =
                    req.firstName != null ||
                            req.lastName != null ||
                            req.photoUrl != null

                if (!hasChanges) {
                    throw IllegalArgumentException(
                        "Нет изменений для сохранения"
                    )
                }

                val updatedUser = repository.updateCurrentUser(req)

                if (updatedUser == null) {
                    throw IllegalStateException(
                        "Сервер не вернул данные пользователя"
                    )
                }

                user.value = updatedUser

                successMessage.value = "Профиль обновлён"

            } catch (e: Exception) {
                errorMessage.value = mapUserError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun changePhone(
        newPhone: String,
        currentPassword: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            successMessage.value = null

            try {
                if (newPhone.isBlank()) {
                    throw IllegalArgumentException(
                        "Введите новый номер телефона"
                    )
                }

                if (currentPassword.isBlank()) {
                    throw IllegalArgumentException(
                        "Введите текущий пароль"
                    )
                }

                val req = ChangePhoneReq(
                    phone = newPhone.trim(),
                    currentPassword = currentPassword
                )

                val updatedUser = repository.changePhone(
                    phone = req.phone,
                    currentPassword = req.currentPassword
                )

                if (updatedUser == null) {
                    throw IllegalStateException(
                        "Сервер не вернул данные пользователя"
                    )
                }

                user.value = updatedUser

                successMessage.value = "Номер телефона изменён"

                onSuccess()

            } catch (e: Exception) {
                errorMessage.value = mapPhoneChangeError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            successMessage.value = null

            try {
                if (oldPassword.isBlank()) {
                    throw IllegalArgumentException(
                        "Введите текущий пароль"
                    )
                }

                if (newPassword.isBlank()) {
                    throw IllegalArgumentException(
                        "Введите новый пароль"
                    )
                }

                repository.changePassword(
                    oldPassword = oldPassword,
                    newPassword = newPassword
                )

                successMessage.value = "Пароль успешно изменён"

                onSuccess()

            } catch (e: Exception) {
                errorMessage.value = mapPasswordChangeError(e)
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
            successMessage.value = null

            var tempFile: File? = null

            try {
                val resolver = context.contentResolver
                val mimeType = resolver.getType(uri) ?: "image/*"

                tempFile = File.createTempFile(
                    "avatar",
                    ".tmp",
                    context.cacheDir
                )

                resolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                } ?: throw IllegalStateException(
                    "Не удалось прочитать файл"
                )

                val body = tempFile.asRequestBody(
                    mimeType.toMediaTypeOrNull()
                )

                val part = MultipartBody.Part.createFormData(
                    "file",
                    tempFile.name,
                    body
                )

                val response = repository.uploadAvatar(part)
                    ?: throw IllegalStateException(
                        "Сервер не вернул фото"
                    )

                val current = user.value

                if (current != null) {
                    user.value = current.copy(
                        photoUrl = response.photoUrl
                    )
                }

                successMessage.value = "Фото профиля обновлено"

                onSuccess(response.photoUrl)

            } catch (e: Exception) {
                errorMessage.value = mapUserError(e)
            } finally {
                tempFile?.delete()
                isLoading.value = false
            }
        }
    }

    fun deleteUser(
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            successMessage.value = null

            try {
                repository.deleteCurrentUser(
                    DeleteAccountReq(password)
                )

                ApiClient.logout()

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

    fun logout(
        onSuccess: () -> Unit
    ) {
        ApiClient.logout()

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

    private fun mapUserError(e: Exception): String {
        return when (e) {
            is HttpException -> {
                val errorBody = e.response()?.errorBody()?.string()
                val errorCode = extractErrorCode(errorBody)

                when {
                    errorCode == "USER_FIRST_NAME_INVALID" -> "Имя должно содержать только кириллицу"
                    errorCode == "USER_LAST_NAME_INVALID" -> "Фамилия должна содержать только кириллицу"
                    errorCode == "AVATAR_TOO_LARGE" -> "Файл слишком большой (макс. 10MB)"
                    errorCode == "AVATAR_TYPE_INVALID" -> "Поддерживаются только JPEG, PNG, WEBP"
                    errorCode == "PHONE_INVALID" -> "Некорректный номер телефона"
                    errorCode == "PHONE_ALREADY_USED" -> "Этот номер уже используется"
                    errorCode == "USER_UPDATE_EMPTY" -> "Нет изменений для сохранения"
                    else -> when (e.code()) {
                        400 -> "Некорректные данные"
                        401 -> "Не авторизован"
                        403 -> "Нет доступа"
                        404 -> "Пользователь не найден"
                        409 -> "Телефон уже используется"
                        500 -> "Ошибка сервера"
                        else -> "Ошибка"
                    }
                }
            }
            is IOException -> "Проверьте интернет"
            else -> e.message ?: "Неизвестная ошибка"
        }
    }

    private fun mapPhoneChangeError(e: Exception): String {
        return when (e) {

            is IllegalArgumentException ->
                e.message ?: "Некорректные данные"

            is HttpException -> when (e.code()) {
                400 -> "Некорректный номер телефона"
                401 -> "Неверный текущий пароль"
                403 -> "Нет доступа"
                404 -> "Пользователь не найден"
                409 -> "Этот номер телефона уже используется"
                500 -> "Ошибка сервера"
                else -> "Не удалось изменить номер телефона"
            }

            is IOException ->
                "Проверьте интернет"

            else ->
                e.message ?: "Неизвестная ошибка"
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

            is IOException ->
                "Проверьте интернет"

            else ->
                e.message ?: "Неизвестная ошибка"
        }
    }
}

private fun mapPasswordChangeError(e: Exception): String {
    return when (e) {

        is IllegalArgumentException ->
            e.message ?: "Некорректные данные"

        is HttpException -> when (e.code()) {
            400 -> "Некорректные данные"
            401 -> "Неверный текущий пароль"
            403 -> "Нет доступа"
            404 -> "Пользователь не найден"
            500 -> "Ошибка сервера"
            else -> "Не удалось изменить пароль"
        }

        is IOException ->
            "Проверьте интернет"

        else ->
            e.message ?: "Неизвестная ошибка"
    }
}

private fun extractErrorCode(errorBody: String?): String? {
    if (errorBody.isNullOrBlank()) return null

    return try {
        val json = org.json.JSONObject(errorBody)
        json.optString("code", null) ?: json.optString("error", null)
    } catch (_: Exception) {
        null
    }
}