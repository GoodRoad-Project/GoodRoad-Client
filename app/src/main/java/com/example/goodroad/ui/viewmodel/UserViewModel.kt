package com.example.goodroad.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.data.network.UserApi
import com.example.goodroad.data.repository.UserRepository
import com.example.goodroad.ui.user.DeleteAccountReq
import com.example.goodroad.ui.user.SettingsView
import com.example.goodroad.ui.user.UpdateSettingsReq
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    var user = mutableStateOf<SettingsView?>(null)
        private set

    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                val realUser = repository.getCurrentUser()
                user.value = realUser
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateUser(firstName: String, lastName: String, photoUrl: String? = null, phone: String? = null) {
        viewModelScope.launch {
            try {
                val updatedUser = repository.updateCurrentUser(
                    UpdateSettingsReq(firstName, lastName, photoUrl, phone)
                )
                user.value = updatedUser
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            try {
                repository.changePassword(oldPassword, newPassword)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteUser(password: String) {
        viewModelScope.launch {
            try {
                repository.deleteCurrentUser(DeleteAccountReq(password))
                user.value = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun logout() {
        user.value = null
    }
}