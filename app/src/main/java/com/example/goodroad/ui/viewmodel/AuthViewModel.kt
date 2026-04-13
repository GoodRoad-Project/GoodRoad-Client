package com.example.goodroad.ui.viewmodel

import com.example.goodroad.data.network.ApiClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import androidx.lifecycle.*
import com.example.goodroad.data.auth.*
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
                ApiClient.updateCredentials(phone, password)
                _loginResult.value = response
            } catch (e: Exception) {
                _error.value = mapAuthError(e, AuthAction.LOGIN)
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
                ApiClient.updateCredentials(phone, password)
                _loginResult.value = response
            } catch (e: Exception) {
                _error.value = mapAuthError(e, AuthAction.REGISTER)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun recoverPassword(phone: String, firstName: String, lastName: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _recoverResult.value = null

            try {
                val ok = authRepository.recoverPassword(phone, firstName, lastName, newPassword)
                _recoverResult.value = ok
            } catch (e: Exception) {
                _error.value = mapAuthError(e, AuthAction.RECOVER)
                _recoverResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearRecoverResult() {
        _recoverResult.value = null
    }

    private fun mapAuthError(e: Exception, action: AuthAction): String {
        return when (e) {
            is HttpException -> when (e.code()) {
                400 -> when (action) {
                    AuthAction.RECOVER -> "Неверные данные для восстановления"
                    else -> "Проверьте введённые данные"
                }

                401 -> when (action) {
                    AuthAction.RECOVER -> "Имя, фамилия или телефон не совпадают"
                    else -> "Неверный номер телефона или пароль"
                }

                403 -> when (action) {
                    AuthAction.REGISTER -> "Регистрация запрещена"
                    else -> "Доступ запрещён"
                }

                404 -> "Пользователь не найден"
                409 -> "Пользователь с таким номером уже существует"
                500 -> "Сервер временно недоступен"
                else -> "Ошибка операции"
            }

            is IOException -> "Проверьте подключение к интернету"

            else -> "Неизвестная ошибка"
        }
    }

    private enum class AuthAction {
        LOGIN,
        REGISTER,
        RECOVER
    }
}