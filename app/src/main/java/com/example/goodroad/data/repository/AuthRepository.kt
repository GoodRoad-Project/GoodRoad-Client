package com.example.goodroad.data.repository

import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.network.AuthResp
import com.example.goodroad.data.network.LoginReq
import com.example.goodroad.data.network.RecoverPasswordReq
import com.example.goodroad.data.network.RegisterReq
import retrofit2.Response

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