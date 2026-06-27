package com.example.goodroad.modules.auth.data

import android.content.Context
import android.util.Log
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.network.TokenManager
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(private val context: Context) {

    private val api = ApiClient.authApi
    private val tokenManager = TokenManager(context)

    suspend fun loginUser(phone: String, password: String): AuthResp {
        return try {
            Log.d("AuthRepo", "Login attempt for: $phone")
            val response = api.login(LoginReq(phone, password))

            response.accessToken?.let { accessToken ->
                tokenManager.saveTokens(
                    accessToken = accessToken,
                    refreshToken = response.refreshToken.orEmpty()
                )
            } ?: Log.e("AuthRepo", "No accessToken in response!")

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

            response.accessToken?.let { accessToken ->
                tokenManager.saveTokens(
                    accessToken = accessToken,
                    refreshToken = response.refreshToken.orEmpty()
                )
            }

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