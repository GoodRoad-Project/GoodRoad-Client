package com.example.goodroad.data.auth

import com.example.goodroad.data.network.ApiClient

class AuthRepository {
    private val api = ApiClient.authApi

    suspend fun loginUser(phone: String, password: String): AuthResp =
        api.login(LoginReq(phone, password))

    suspend fun registerUser(firstName: String, lastName: String, phone: String, password: String): AuthResp =
        api.register(RegisterReq(firstName, lastName, phone, password))

    suspend fun recoverPassword(phone: String, firstName: String, lastName: String, newPassword: String): Boolean {
        val req = RecoverPasswordReq(phone, firstName, lastName, newPassword)
        val response = api.recoverPassword(req)
        if (response.isSuccessful) return true
        else throw Exception("Ошибка восстановления пароля: ${response.code()}")
    }
}