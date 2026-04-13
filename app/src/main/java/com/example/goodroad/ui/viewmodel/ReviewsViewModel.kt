package com.example.goodroad.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.data.review.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class ReviewsViewModel(private val repository: ReviewRepository) : ViewModel() {

    var reviews = mutableStateOf<List<ReviewCardResp>>(emptyList())
        private set

    var points = mutableStateOf<ReviewPointsResp?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    var isSubmitting = mutableStateOf(false)
        private set

    var isPhotoUploading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var successMessage = mutableStateOf<String?>(null)
        private set

    fun loadReviews() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                points.value = repository.getOwnReviewPoints()
                reviews.value = repository.getOwnReviews()
            } catch (e: Exception) {
                errorMessage.value = mapReviewError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun createReview(req: UpsertReviewReq, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isSubmitting.value = true
            errorMessage.value = null
            successMessage.value = null

            try {
                repository.createReview(req)
                successMessage.value = "Отзыв отправлен на модерацию"
                points.value = repository.getOwnReviewPoints()
                reviews.value = repository.getOwnReviews()
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = mapReviewError(e)
            } finally {
                isSubmitting.value = false
            }
        }
    }

    fun updateReview(reviewId: String, req: UpsertReviewReq, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isSubmitting.value = true
            errorMessage.value = null
            successMessage.value = null

            try {
                repository.updateReview(reviewId, req)
                successMessage.value = "Изменения сохранены. Отзыв снова отправлен на модерацию"
                points.value = repository.getOwnReviewPoints()
                reviews.value = repository.getOwnReviews()
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = mapReviewError(e)
            } finally {
                isSubmitting.value = false
            }
        }
    }

    fun deleteReview(reviewId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isSubmitting.value = true
            errorMessage.value = null
            successMessage.value = null

            try {
                repository.deleteReview(reviewId)
                successMessage.value = "Отзыв удален"
                points.value = repository.getOwnReviewPoints()
                reviews.value = repository.getOwnReviews()
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = mapReviewError(e)
            } finally {
                isSubmitting.value = false
            }
        }
    }

    fun uploadReviewPhotos(
        context: Context,
        uris: List<Uri>,
        onSuccess: (List<String>) -> Unit
    ) {
        if (uris.isEmpty()) return

        viewModelScope.launch {
            isPhotoUploading.value = true
            errorMessage.value = null
            successMessage.value = null

            try {
                val resolver = context.contentResolver
                val uploadedUrls = mutableListOf<String>()

                for (uri in uris) {
                    val mimeType = resolver.getType(uri) ?: "image/*"
                    val extension = MimeTypeMap.resolveExtension(mimeType)
                    val tempFile = File.createTempFile("review_photo", extension, context.cacheDir)

                    try {
                        resolver.openInputStream(uri)?.use { input ->
                            tempFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        } ?: throw IllegalArgumentException("Не удалось прочитать выбранный файл")

                        val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
                        val part = MultipartBody.Part.createFormData("file", tempFile.name, requestBody)
                        uploadedUrls += repository.uploadReviewPhoto(part)
                    } finally {
                        tempFile.delete()
                    }
                }

                successMessage.value = if (uploadedUrls.size == 1) {
                    "Фотография добавлена"
                } else {
                    "Фотографии добавлены"
                }
                onSuccess(uploadedUrls)
            } catch (e: Exception) {
                errorMessage.value = mapReviewError(e)
            } finally {
                isPhotoUploading.value = false
            }
        }
    }

    fun clearMessages() {
        errorMessage.value = null
        successMessage.value = null
    }

    fun clearSuccessMessage() {
        successMessage.value = null
    }

    private fun mapReviewError(e: Exception): String {
        return when (e) {
            is IllegalArgumentException -> e.message ?: "Некорректные данные отзыва"
            is HttpException -> when (e.code()) {
                400 -> "Проверьте поля отзыва"
                401 -> "Вы не авторизованы"
                404 -> "Отзыв или препятствие не найдены"
                409 -> "Такой отзыв уже существует"
                413 -> "Файл слишком большой"
                500 -> "Сервер временно недоступен"
                else -> "Не удалось выполнить операцию с отзывом"
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
