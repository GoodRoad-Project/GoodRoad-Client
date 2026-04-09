package com.example.goodroad.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.data.user.UserRepository
import com.example.goodroad.data.user.DeleteAccountReq
import com.example.goodroad.data.user.SettingsView
import com.example.goodroad.data.user.UpdateUserReq
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

    fun updateUser(
        firstName: String,
        lastName: String,
        photoUrl: String? = null,
        phone: String? = null,
        oldPassword: String? = null,
        newPassword: String? = null
    ) {
        viewModelScope.launch {
            errorMessage.value = null
            try {
                val req = UpdateUserReq(
                    firstName = firstName,
                    lastName = lastName,
                    photoUrl = photoUrl,
                    phone = phone,
                    oldPassword = oldPassword,
                    newPassword = newPassword
                )
                user.value = repository.updateCurrentUser(req)
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