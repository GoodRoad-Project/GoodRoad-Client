package com.example.goodroad.modules.volunteer.presentation

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.modules.volunteer.data.VolunteerRepository
import com.example.goodroad.modules.volunteer.data.models.HelpRequestItem
import com.example.goodroad.modules.volunteer.data.models.RequestStatus as ApiRequestStatus
import com.example.goodroad.modules.volunteer.data.models.VolunteerMenuRespDto
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class VolunteerViewModel(
    private val repository: VolunteerRepository
) : ViewModel() {

    enum class RequestStatus {
        PENDING,
        APPROVED,
        REJECTED,
        OPEN,
        ACCEPTED,
        CANCELLED,
        COMPLETED,
        UNKNOWN
    }

    data class HelpRequest(
        val id: String,
        val routeStart: String,
        val routeEnd: String,
        val dateTime: String,
        val contact: String,
        val specialNotes: String,
        val comment: String,
        val status: RequestStatus = RequestStatus.OPEN
    )

    data class VolunteerMenu(
        val isVolunteer: Boolean,
        val applicationStatus: String?,
        val rejectReason: String?
    )

    var volunteerMenu = mutableStateOf<VolunteerMenu?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var successMessage = mutableStateOf<String?>(null)
        private set

    val requests = mutableStateListOf<HelpRequest>()

    init {
        loadOwnRequests()
        loadVolunteerMenu()
    }

    fun loadVolunteerMenu() {
        viewModelScope.launch {
            try {
                val resp = repository.getMenu()
                volunteerMenu.value = VolunteerMenu(
                    isVolunteer = resp.volunteer,
                    applicationStatus = resp.applicationStatus,
                    rejectReason = resp.rejectReason
                )
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Ошибка загрузки статуса заявки"
            }
        }
    }

    fun clearVolunteerMenu() {
        volunteerMenu.value = null
    }

    fun loadOwnRequests() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            successMessage.value = null

            try {
                val loaded = repository.loadOwnRequests()
                requests.clear()
                requests.addAll(loaded.map { it.toUiModel() })
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Ошибка загрузки заявок"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun refreshOwnRequests() = loadOwnRequests()

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
                val created = repository.createHelpRequest(
                    fromAddress = routeStart,
                    toAddress = routeEnd,
                    date = meetingDate.replace(".", "-"),
                    time = meetingTime,
                    phone = contact,
                    socialNickname = specialNotes.takeIf { it.isNotBlank() },
                    comment = comment
                )

                requests.add(0, created.toUiModel())
                successMessage.value = "Заявка отправлена"
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Ошибка"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun submitVolunteerApplication(
        context: Context,
        dobroUrl: String,
        phone: String,
        socialNickname: String?,
        uris: List<Uri>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            successMessage.value = null

            val uploadedUrls = mutableListOf<String>()
            val tempFiles = mutableListOf<File>()

            try {
                if (dobroUrl.isBlank()) {
                    throw IllegalArgumentException("Укажите ссылку на dobro.ru")
                }

                if (!dobroUrl.startsWith("http")) {
                    throw IllegalArgumentException("Неверная ссылка")
                }

                if (phone.isBlank()) {
                    throw IllegalArgumentException("Укажите телефон")
                }

                uris.forEach { uri ->
                    val file = File.createTempFile("cert", ".tmp", context.cacheDir)
                    tempFiles.add(file)

                    context.contentResolver.openInputStream(uri)?.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    val mime = context.contentResolver.getType(uri) ?: "image/*"
                    val body = file.asRequestBody(mime.toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("file", file.name, body)

                    val response = repository.uploadCertificate(part)
                    uploadedUrls.add(response.photoUrl)
                }

                repository.createApplication(
                    dobroUrl = dobroUrl.trim(),
                    phone = phone.trim(),
                    socialNickname = socialNickname?.trim()?.ifBlank { null },
                    certificatePhotoUrls = uploadedUrls
                )

                successMessage.value = "Заявка на волонтёрство отправлена"
                loadVolunteerMenu()
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = when (e) {
                    is HttpException -> when (e.code()) {
                        400 -> "Ошибка в заполненных данных"
                        403 -> "Нет прав для выполнения действия"
                        404 -> "Объект не найден"
                        409 -> "Заявка уже отправлена или уже существует"
                        else -> "Ошибка сервера (${e.code()})"
                    }

                    is IOException -> "Ошибка сети. Проверьте интернет"
                    else -> e.message ?: "Ошибка отправки заявки"
                }
            } finally {
                tempFiles.forEach { it.delete() }
                isLoading.value = false
            }
        }
    }

    fun deleteRequest(id: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            successMessage.value = null

            try {
                repository.deleteOwnRequest(id)
                requests.removeAll { it.id == id }
                successMessage.value = "Заявка удалена"
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Ошибка удаления"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        errorMessage.value = null
        successMessage.value = null
    }

    private fun HelpRequestItem.toUiModel(): HelpRequest {
        return HelpRequest(
            id = id,
            routeStart = routeStart,
            routeEnd = routeEnd,
            dateTime = dateTime,
            contact = contact,
            specialNotes = specialNotes.ifBlank { "—" },
            comment = comment.ifBlank { "—" },
            status = status.toUiStatus()
        )
    }

    private fun ApiRequestStatus.toUiStatus(): RequestStatus {
        return when (this) {
            ApiRequestStatus.OPEN -> RequestStatus.OPEN
            ApiRequestStatus.ACCEPTED -> RequestStatus.ACCEPTED
            ApiRequestStatus.CANCELLED -> RequestStatus.CANCELLED
            ApiRequestStatus.COMPLETED -> RequestStatus.COMPLETED
            ApiRequestStatus.UNKNOWN -> RequestStatus.UNKNOWN
        }
    }
}