package com.example.goodroad.data.network

import android.content.Context
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
import com.example.goodroad.modules.auth.data.UserDto
import com.example.goodroad.modules.review.data.ReviewApi
import com.example.goodroad.modules.user.data.UserApi
import com.example.goodroad.modules.volunteer.data.VolunteerApi
import com.example.goodroad.modules.moderator.data.VolunteerModerationApi
import com.example.goodroad.modules.rewards.data.RewardsApi
import com.example.goodroad.modules.tasks.data.TasksApi
import retrofit2.http.POST
import retrofit2.http.Body

object ApiClient {

    private lateinit var tokenManager: TokenManager
    private lateinit var refreshApi: RefreshApi

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
        val original = chain.request()
        val token = tokenManager.getAccessToken()

        val request = original.newBuilder().apply {
            if (!token.isNullOrBlank() && !original.url.encodedPath.startsWith("/auth/")) {
                header("Authorization", "Bearer $token")
            }
        }.build()

        chain.proceed(request)
    }

    private val authenticator = object : okhttp3.Authenticator {
        override fun authenticate(route: Route?, response: okhttp3.Response): Request? {
            if (response.code != 401) return null
            if (responseCount(response) >= 2) return null

            val refreshResponse = kotlinx.coroutines.runBlocking {
                refreshTokens()
            } ?: return null

            return response.request.newBuilder()
                .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                .build()
        }
    }

    private fun createRefreshApi(): RefreshApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.GOODROAD_SERVER_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(RefreshApi::class.java)
    }

    private val client: OkHttpClient by lazy {
        refreshApi = createRefreshApi()

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

    fun saveTokens(accessToken: String, refreshToken: String) {
        tokenManager.saveTokens(accessToken, refreshToken)
    }

    fun logout() {
        tokenManager.clearTokens()
    }

    fun getCurrentToken(): String? = tokenManager.getAccessToken()

    suspend fun refreshTokens(): AuthRefreshResponse? {
        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            tokenManager.clearTokens()
            return null
        }

        return try {
            if (!::refreshApi.isInitialized) {
                refreshApi = createRefreshApi()
            }

            val response = refreshApi.refreshToken(RefreshRequest(refreshToken))
            tokenManager.saveTokens(response.accessToken, response.refreshToken)
            response
        } catch (e: Exception) {
            tokenManager.clearTokens()
            null
        }
    }

    private fun responseCount(response: okhttp3.Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}

interface RefreshApi {
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): AuthRefreshResponse
}

data class RefreshRequest(val refreshToken: String)

data class AuthRefreshResponse(
    val user: UserDto? = null,
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String? = null
)