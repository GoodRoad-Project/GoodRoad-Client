package com.example.goodroad.data.network

import com.example.goodroad.BuildConfig
import com.example.goodroad.data.auth.AuthApi
import com.example.goodroad.data.review.ReviewApi
import com.example.goodroad.data.user.UserApi
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private var userPhone: String? = null
    private var userPassword: String? = null

    fun updateCredentials(phone: String? = null, password: String? = null) {
        if (!phone.isNullOrBlank()) {
            userPhone = phone
        }
        if (!password.isNullOrBlank()) {
            userPassword = password
        }
    }

    fun clearCredentials() {
        userPhone = null
        userPassword = null
    }

    private val client: OkHttpClient
        get() = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                val phone = userPhone
                val password = userPassword
                if (!phone.isNullOrBlank() && !password.isNullOrBlank()) {
                    val credential = Credentials.basic(phone, password)
                    requestBuilder.addHeader("Authorization", credential)
                }
                chain.proceed(requestBuilder.build())
            }
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()

    private fun retrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.GOODROAD_SERVER_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        retrofit().create(AuthApi::class.java)
    }

    val userApi: UserApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        retrofit().create(UserApi::class.java)
    }

    val reviewApi: ReviewApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        retrofit().create(ReviewApi::class.java)
    }
}