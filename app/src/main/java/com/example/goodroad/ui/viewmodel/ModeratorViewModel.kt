package com.example.goodroad.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.data.moderator.CreateModeratorReq
import com.example.goodroad.data.moderator.ModeratorRepository
import com.example.goodroad.data.moderator.ModeratorView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ModeratorViewModel(
    private val repository: ModeratorRepository
) : ViewModel() {

    private val _moderators = MutableStateFlow<List<ModeratorView>>(emptyList())
    val moderators: StateFlow<List<ModeratorView>> = _moderators

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadModerators() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                _moderators.value = repository.getModerators()
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addModerator(
        firstName: String,
        lastName: String,
        phone: String,
        password: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.createModerator(
                    CreateModeratorReq(firstName, lastName, phone, password)
                )
                loadModerators()
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка добавления"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun disableModerator(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.disableModerator(id)
                loadModerators()
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка отключения"
            } finally {
                _isLoading.value = false
            }
        }
    }
}