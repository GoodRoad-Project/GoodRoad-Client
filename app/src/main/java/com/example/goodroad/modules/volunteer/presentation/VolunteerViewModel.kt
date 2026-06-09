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
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

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
        val socialNickname: String,
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
    val feed = mutableStateListOf<HelpRequest>()

    val wards = mutableStateListOf<HelpRequest>()

    init {
        loadOwnRequests()
        loadVolunteerMenu()
        loadFeed()
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

    val isVolunteer: Boolean
        get() = volunteerMenu.value?.isVolunteer == true

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

    fun loadFeed() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val loaded = repository.loadFeed()

                println("FEED RAW SIZE = ${loaded.size}")
                println("FEED RAW = $loaded")

                feed.clear()
                feed.addAll(loaded.map { it.toUiModel() })

                println("FEED UI SIZE = ${feed.size}")
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Ошибка загрузки ленты"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun refreshFeed() = loadFeed()

    fun acceptRequest(id: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            successMessage.value = null

            val removedItem = feed.find { it.id == id }

            try {
                feed.removeAll { it.id == id }

                repository.acceptRequest(id)

                successMessage.value = "Вы стали сопровождающим"
            } catch (e: Exception) {
                if (removedItem != null) {
                    feed.add(removedItem)
                }
                errorMessage.value = e.message ?: "Ошибка принятия заявки"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun withdrawRequest(id: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            successMessage.value = null

            val removedItem = wards.find { it.id == id }

            try {
                wards.removeAll { it.id == id }

                repository.withdrawResponse(id)

                successMessage.value = "Вы отказались от заявки"

            } catch (e: Exception) {
                if (removedItem != null) {
                    wards.add(removedItem)
                }

                errorMessage.value = e.message ?: "Ошибка отказа от заявки"

            } finally {
                isLoading.value = false
            }
        }
    }

    fun createRequest(
        routeStart: String,
        routeEnd: String,
        meetingDate: String,
        meetingTime: String,
        contact: String,
        socialNickname: String,
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
                    socialNickname = socialNickname.takeIf { it.isNotBlank() },
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
                if (dobroUrl.isBlank()) throw IllegalArgumentException("Укажите ссылку")
                if (!dobroUrl.startsWith("http")) throw IllegalArgumentException("Неверная ссылка")
                if (phone.isBlank()) throw IllegalArgumentException("Укажите телефон")

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
                errorMessage.value = e.message ?: "Ошибка"
            } finally {
                tempFiles.forEach { it.delete() }
                isLoading.value = false
            }
        }
    }

    fun cancelOrDeleteRequest(id: String, status: RequestStatus) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            successMessage.value = null

            try {
                if (status == RequestStatus.ACCEPTED) {
                    repository.cancelOwnRequest(id)
                    successMessage.value = "Заявка отменена"
                } else {
                    repository.deleteOwnRequest(id)
                    successMessage.value = "Заявка удалена"
                }

                requests.removeAll { it.id == id }

            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Ошибка операции"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun finishWalk(id: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            successMessage.value = null

            try {
                val updated = repository.finishWalk(id)

                requests.replaceAll {
                    if (it.id == id) updated.toUiModel() else it
                }

                successMessage.value = "Прогулка отмечена как выполненная"

            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Ошибка завершения прогулки"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        errorMessage.value = null
        successMessage.value = null
    }

    private fun replaceFeedItem(updated: HelpRequest) {
        val index = feed.indexOfFirst { it.id == updated.id }
        if (index >= 0) {
            feed[index] = updated
        } else {
            feed.add(updated)
        }
    }

    fun loadMyWards() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val loaded = repository.loadMyWards()

                wards.clear()
                wards.addAll(
                    loaded.map { it.toUiModel() }
                )

            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Ошибка загрузки подопечных"
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun HelpRequestItem.toUiModel(): HelpRequest {
        return HelpRequest(
            id = id,
            routeStart = routeStart,
            routeEnd = routeEnd,
            dateTime = dateTime,
            contact = contact,
            socialNickname = socialNickname.ifBlank { "—" },
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