package com.example.goodroad.modules.auth.data

import android.content.Context
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.network.TokenManager
import retrofit2.HttpException
import java.io.IOException
import android.util.Log

class AuthRepository(
    context: Context,
    private val api: AuthApi = ApiClient.authApi,
    private val tokenManager: TokenManager = TokenManager(context)
) {
    suspend fun loginUser(phone: String, password: String): AuthResp {
        return try {
            //Log.d("AuthRepo", "Login attempt for: $phone")
            val response = api.login(LoginReq(phone, password))
            //Log.d("AuthRepo", "Response received, accessToken: ${response.accessToken?.take(50)}")

            response.accessToken?.let { accessToken ->
                response.refreshToken?.let { refreshToken ->
                    //Log.d("AuthRepo", "Saving tokens...")
                    tokenManager.saveTokens(accessToken, refreshToken)
                    //Log.d("AuthRepo", "Tokens saved, checking...")
                    //Log.d("AuthRepo", "AccessToken exists: ${tokenManager.getAccessToken() != null}")
                    //Log.d("AuthRepo", "RefreshToken exists: ${tokenManager.getRefreshToken() != null}")
                } ?: Log.e("AuthRepo", "No refreshToken in response!")
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
                response.refreshToken?.let { refreshToken ->
                    tokenManager.saveTokens(accessToken, refreshToken)
                }
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