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

    fun deleteUser(password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                repository.deleteCurrentUser(DeleteAccountReq(password))
                user.value = null
                isDeleted = true
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        user.value = null
        isDeleted = false
        onSuccess()
    }
}