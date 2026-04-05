package com.example.goodroad.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.goodroad.data.network.AuthResp
import com.example.goodroad.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _loginResult = MutableLiveData<AuthResp?>()
    val loginResult: LiveData<AuthResp?> = _loginResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun login(phone: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.loginUser(phone, password)
                _loginResult.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun register(firstName: String, lastName: String, phone: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.registerUser(firstName, lastName, phone, password)
                _loginResult.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    private val _recoverResult = MutableLiveData<Boolean?>()
    val recoverResult: LiveData<Boolean?> = _recoverResult

    fun recoverPassword(phone: String, firstName: String, lastName: String, newPassword: String) {
        viewModelScope.launch {
            try {
                val success = authRepository.recoverPassword(phone, firstName, lastName, newPassword)
                _recoverResult.value = success
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}