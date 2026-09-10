package com.example.goodroad.modules.moderationReview.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.modules.moderationReview.data.ModerationReviewRepository
import com.example.goodroad.modules.moderationReview.data.ReviewForModeration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import org.json.JSONObject

class ReviewModerationViewModel(
    private val repository: ModerationReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModerationUiState())
    val uiState: StateFlow<ModerationUiState> = _uiState.asStateFlow()

    private val _reviews = MutableStateFlow<List<ReviewForModeration>>(emptyList())
    val reviews: StateFlow<List<ReviewForModeration>> = _reviews

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private var currentPage = 0
    private var totalItems = 0L
    private val pageSize = 20

    private fun extractErrorCode(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            JSONObject(errorBody).optString("code", null)
        } catch (_: Exception) {
            null
        }
    }

    private fun mapModerationError(e: Exception): String {
        return when (e) {
            is HttpException -> {
                val errorBody = e.response()?.errorBody()?.string()
                val errorCode = extractErrorCode(errorBody)

                when {
                    errorCode == "REVIEW_ALREADY_TAKEN_BY_MODERATOR" -> "Этот отзыв уже взял другой модератор"
                    errorCode == "REVIEW_NOT_TAKEN_BY_YOU" -> "Вы не можете освободить этот отзыв"
                    errorCode == "REVIEW_NOT_TAKEN_BY_MODERATOR" -> "Отзыв не взят в работу"
                    errorCode == "REVIEW_ID_NOT_FOUND" -> "Отзыв не найден"
                    errorCode == "REVIEW_REASON_EMPTY" -> "Укажите причину отклонения"
                    errorCode == "USER_ROLE_FORBIDDEN" -> "Недостаточно прав для модерации"
                    errorCode == "ID_INVALID" -> "Неверный ID отзыва"
                    errorCode == "RENIEW_NOT_PENDING" -> "Отзыв уже обработан"
                    else -> when (e.code()) {
                        400 -> "Некорректный запрос"
                        401 -> "Необходима авторизация"
                        403 -> "Доступ запрещен"
                        404 -> "Отзыв не найден"
                        409 -> "Конфликт: отзыв уже обрабатывается"
                        500 -> "Ошибка сервера"
                        else -> "Ошибка при выполнении операции"
                    }
                }
            }
            is IOException -> "Проверьте подключение к интернету"
            else -> e.message ?: "Неизвестная ошибка"
        }
    }

    fun loadReviews(reset: Boolean = true) {
        if (reset) {
            currentPage = 0
            _reviews.value = emptyList()
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = repository.listPending(currentPage, pageSize)
                if (reset) {
                    _reviews.value = response.items
                } else {
                    _reviews.value = _reviews.value + response.items
                }
                totalItems = response.total
                currentPage++
                _uiState.value = _uiState.value.copy(hasMore = _reviews.value.size < totalItems)
            } catch (e: Exception) {
                _errorMessage.value = mapModerationError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun takeInWork(reviewId: String, onSuccess: (ReviewForModeration) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val updatedReview = repository.takeInWork(reviewId)
                updateReviewInList(updatedReview)
                _successMessage.value = "Отзыв взят в работу"
                onSuccess(updatedReview)
            } catch (e: Exception) {
                _errorMessage.value = mapModerationError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun approve(reviewId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                repository.approve(reviewId)
                removeReviewFromList(reviewId)
                _successMessage.value = "Отзыв одобрен"
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = mapModerationError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reject(reviewId: String, reason: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                repository.reject(reviewId, reason)
                removeReviewFromList(reviewId)
                _successMessage.value = "Отзыв отклонен"
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = mapModerationError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun release(reviewId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                repository.release(reviewId)
                val currentReviews = _reviews.value.toMutableList()
                val index = currentReviews.indexOfFirst { it.id == reviewId }
                if (index != -1) {
                    val releasedReview = currentReviews[index].copy(
                        takenInWork = false,
                        takenByMe = false,
                        takenByModeratorId = null,
                        takenAt = null
                    )
                    currentReviews[index] = releasedReview
                    _reviews.value = currentReviews
                }
                _successMessage.value = "Отзыв освобожден"
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = mapModerationError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateReviewInList(updatedReview: ReviewForModeration) {
        val currentReviews = _reviews.value.toMutableList()
        val index = currentReviews.indexOfFirst { it.id == updatedReview.id }
        if (index != -1) {
            currentReviews[index] = updatedReview
            _reviews.value = currentReviews
        }
    }

    private fun removeReviewFromList(reviewId: String) {
        _reviews.value = _reviews.value.filter { it.id != reviewId }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun loadMore() {
        if (_uiState.value.hasMore && !_isLoading.value) {
            loadReviews(reset = false)
        }
    }

    data class ModerationUiState(
        val hasMore: Boolean = true
    )
}