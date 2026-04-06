package com.example.goodroad.data.network

import com.example.goodroad.BuildConfig
import okhttp3.Credentials
import okhttp3.OkHttpClient
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

    fun setCredentials(phone: String, password: String) {
        userPhone = phone
        userPassword = password
    }

    private val client = OkHttpClient.Builder()
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

    val authApi: AuthApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Retrofit.Builder()
            .baseUrl(BuildConfig.GOODROAD_SERVER_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    val userApi: UserApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Retrofit.Builder()
            .baseUrl(BuildConfig.GOODROAD_SERVER_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }
}