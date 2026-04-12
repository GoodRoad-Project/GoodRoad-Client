    package com.example.goodroad.data.auth

    import com.example.goodroad.data.network.ApiClient
    import retrofit2.HttpException
    import java.io.IOException

    class AuthRepository {

        private val api = ApiClient.authApi

        suspend fun loginUser(phone: String, password: String): AuthResp {
            return try {
                api.login(LoginReq(phone, password))
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
                api.register(RegisterReq(firstName, lastName, phone, password))
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