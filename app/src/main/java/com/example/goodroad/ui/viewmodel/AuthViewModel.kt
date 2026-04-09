package com.example.goodroad.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.goodroad.data.auth.AuthResp
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.auth.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _loginResult = MutableLiveData<AuthResp?>()
    val loginResult: LiveData<AuthResp?> = _loginResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _recoverResult = MutableLiveData<Boolean?>()
    val recoverResult: LiveData<Boolean?> = _recoverResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(phone: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = authRepository.loginUser(phone, password)
                ApiClient.setCredentials(phone, password)
                _loginResult.value = response
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка логина"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(firstName: String, lastName: String, phone: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = authRepository.registerUser(firstName, lastName, phone, password)
                ApiClient.setCredentials(phone, password)
                _loginResult.value = response
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка регистрации"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun recoverPassword(phone: String, firstName: String, lastName: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val success = authRepository.recoverPassword(phone, firstName, lastName, newPassword)
                _recoverResult.value = success
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка восстановления пароля"
            } finally {
                _isLoading.value = false
            }
        }
    }
}