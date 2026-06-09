package com.example.goodroad.data.network

import android.content.Context
import com.example.goodroad.BuildConfig
import com.example.goodroad.modules.moderator.data.ModeratorApi
import com.example.goodroad.modules.moderationReview.data.ModerationReviewApi
import com.example.goodroad.data.obstacle.*
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.Response
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
import com.example.goodroad.modules.rewards.data.RewardsApi
import com.example.goodroad.modules.tasks.data.TasksApi

object ApiClient {

    private lateinit var tokenManager: TokenManager

    fun init(context: Context) {
        tokenManager = TokenManager(context.applicationContext)
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val authInterceptor = Interceptor { chain ->
        val token = tokenManager.getToken()
        val request = chain.request().newBuilder().apply {
            if (token != null && token.isNotBlank()) {
                header("Authorization", "Bearer $token")
                addHeader("Content-Type", "application/json")
            }
        }.build()

        chain.proceed(request)
    }

    private val authenticator = object : Authenticator {
        override fun authenticate(route: Route?, response: Response): Request? {
            if (response.code == 401) {
                tokenManager.clearToken()
                return null
            }
            return null
        }
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .authenticator(authenticator)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

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

    val rewardsApi: RewardsApi by lazy {
        retrofit().create(RewardsApi::class.java)
    }

    val tasksApi: TasksApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        retrofit().create(TasksApi::class.java)
    }

    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    fun logout() {
        tokenManager.clearToken()
    }

    fun getCurrentToken(): String? = tokenManager.getToken()
}

