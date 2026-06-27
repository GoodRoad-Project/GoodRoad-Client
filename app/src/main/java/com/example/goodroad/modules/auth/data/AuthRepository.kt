package com.example.goodroad.modules.auth.data

import android.content.Context
import android.util.Log
import com.example.goodroad.data.network.ApiClient
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(private val context: Context) {

    private val api = ApiClient.authApi

    suspend fun loginUser(phone: String, password: String): AuthResp {
        return try {
            Log.d("AuthRepo", "Login attempt for: $phone")
            val response = api.login(LoginReq(phone, password))

            saveTokensFrom(response)
            response
        } catch (e: HttpException) {
            Log.e("AuthRepo", "HTTP error: ${e.code()} - ${e.message()}")
            throw e
        } catch (e: IOException) {
            Log.e("AuthRepo", "IO error: ${e.message}")
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

            saveTokensFrom(response)
            response
        } catch (e: HttpException) {
            throw e
        } catch (e: IOException) {
            throw IOException()
        }
    }

    private fun saveTokensFrom(response: AuthResp) {
        val accessToken = response.accessToken
        if (accessToken.isNullOrBlank()) {
            Log.e("AuthRepo", "No accessToken in auth response")
            throw IllegalStateException("Сервер не вернул токен авторизации")
        }

        ApiClient.saveTokens(
            accessToken = accessToken,
            refreshToken = response.refreshToken.orEmpty()
        )
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