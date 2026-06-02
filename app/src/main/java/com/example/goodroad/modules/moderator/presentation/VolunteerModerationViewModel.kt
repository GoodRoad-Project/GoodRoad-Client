package com.example.goodroad.modules.moderator.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.modules.moderator.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VolunteerModerationViewModel(
    private val repo: VolunteerModerationRepository
) : ViewModel() {

    private val _applications = MutableStateFlow<List<VolunteerApplicationResp>>(emptyList())
    val applications: StateFlow<List<VolunteerApplicationResp>> = _applications

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _applications.value = repo.getPendingApplications()
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки"
            } finally {
                _loading.value = false
            }
        }
    }

    fun approve(id: String) {
        viewModelScope.launch {
            try {
                repo.approve(id)
                load()
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка approve"
            }
        }
    }

    fun reject(id: String, reason: String) {
        viewModelScope.launch {
            try {
                repo.reject(id, reason)
                load()
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка reject"
            }
        }
    }
}