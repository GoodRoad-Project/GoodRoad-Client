package com.example.goodroad.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.data.repository.UserRepository
import com.example.goodroad.ui.user.DeleteAccountReq
import com.example.goodroad.ui.user.SettingsView
import com.example.goodroad.ui.user.UpdateSettingsReq
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    var user = mutableStateOf<SettingsView?>(null)
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun getCurrentUser() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                user.value = repository.getCurrentUser()
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Неизвестная ошибка"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateUser(firstName: String, lastName: String, photoUrl: String? = null, phone: String? = null) {
        viewModelScope.launch {
            try {
                user.value = repository.updateCurrentUser(UpdateSettingsReq(firstName, lastName, photoUrl, phone))
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            try {
                repository.changePassword(oldPassword, newPassword)
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }

    fun deleteUser(password: String) {
        viewModelScope.launch {
            try {
                repository.deleteCurrentUser(DeleteAccountReq(password))
                user.value = null
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }

    fun logout() {
        user.value = null
    }
}