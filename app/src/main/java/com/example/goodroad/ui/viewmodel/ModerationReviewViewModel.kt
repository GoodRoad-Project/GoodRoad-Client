package com.example.goodroad.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.data.moderationReview.ModerationPageResponse
import com.example.goodroad.data.moderationReview.ModerationReviewRepository
import com.example.goodroad.data.moderationReview.ReviewForModeration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
                _errorMessage.value = e.message ?: "Ошибка загрузки отзывов"
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
                _errorMessage.value = e.message ?: "Ошибка при взятии отзыва"
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
                _errorMessage.value = e.message ?: "Ошибка при одобрении отзыва"
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
                _errorMessage.value = e.message ?: "Ошибка при отклонении отзыва"
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
                _errorMessage.value = e.message ?: "Ошибка при освобождении отзыва"
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