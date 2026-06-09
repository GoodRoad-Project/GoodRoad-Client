package com.example.goodroad.modules.auth.data

import android.content.Context
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.network.TokenManager
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(private val context: Context) {

    private val api = ApiClient.authApi
    private val tokenManager = TokenManager(context)

    suspend fun loginUser(phone: String, password: String): AuthResp {
        return try {
            val response = api.login(LoginReq(phone, password))
            response.accessToken?.let { tokenManager.saveToken(it) }
            response
        } catch (e: HttpException) {
            throw e
        } catch (e: IOException) {
            throw IOException()
        }
    }

    suspend fun registerUser(
        firstName: String,
        lastName: String,
        phone: String,
        password: String
    ): AuthResp {
        return try {
            val response = api.register(RegisterReq(firstName, lastName, phone, password))
            response.accessToken?.let { tokenManager.saveToken(it) }
            response
        } catch (e: HttpException) {
            throw e
        } catch (e: IOException) {
            throw IOException()
        }
    }

    suspend fun recoverPassword(
        phone: String,
        firstName: String,
        lastName: String,
        newPassword: String
    ): Boolean {
        val response = api.recoverPassword(
            RecoverPasswordReq(phone, firstName, lastName, newPassword)
        )

        if (response.isSuccessful) return true
        throw HttpException(response)
    }
}