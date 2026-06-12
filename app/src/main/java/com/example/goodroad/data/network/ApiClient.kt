package com.example.goodroad.data.network

import com.example.goodroad.BuildConfig
import com.example.goodroad.modules.moderator.data.ModeratorApi
import com.example.goodroad.modules.moderationReview.data.ModerationReviewApi
import com.example.goodroad.data.obstacle.*
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.*
import retrofit2.*
import retrofit2.converter.gson.*
import java.time.Instant
import java.util.concurrent.*
import com.example.goodroad.modules.auth.data.AuthApi
import com.example.goodroad.modules.review.data.ReviewApi
import com.example.goodroad.modules.user.data.UserApi
import com.example.goodroad.modules.volunteer.data.VolunteerApi
import com.example.goodroad.modules.moderator.data.VolunteerModerationApi

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
        val gson = GsonBuilder()
            .registerTypeAdapter(Instant::class.java, InstantAdapter())
            .create()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.GOODROAD_SERVER_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit().create(AuthApi::class.java)
    }

    val userApi: UserApi by lazy {
        retrofit().create(UserApi::class.java)
    }

    val moderatorApi: ModeratorApi by lazy {
        retrofit().create(ModeratorApi::class.java)
    }

    val obstacleApi: ObstacleApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        retrofit().create(ObstacleApi::class.java)
    }

    val reviewApi: ReviewApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        retrofit().create(ReviewApi::class.java)
    }

    val moderationReviewApi: ModerationReviewApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        retrofit().create(ModerationReviewApi::class.java)
    }

    val routeApi: GoodRoadApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        retrofit().create(GoodRoadApi::class.java)
    }

    val volunteerApi: VolunteerApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        retrofit().create(VolunteerApi::class.java)
    }

    val volunteerModerationApi: VolunteerModerationApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        retrofit().create(VolunteerModerationApi::class.java)
    }
}

