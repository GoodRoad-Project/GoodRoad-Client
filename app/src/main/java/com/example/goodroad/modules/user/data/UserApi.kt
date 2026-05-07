package com.example.goodroad.modules.user.data

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface UserApi {

    @GET("/users")
    suspend fun getCurrentUser(): Response<SettingsView>

    @PUT("/users")
    suspend fun updateCurrentUser(@Body req: UpdateUserReq): Response<SettingsView>

    @POST("/users")
    suspend fun changePassword(
        @Query("oldPassword") oldPassword: String,
        @Query("newPassword") newPassword: String
    ): Response<Unit>

    @Multipart
    @POST("/users/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): Response<AvatarUploadResp>

    @HTTP(method = "DELETE", path = "/users", hasBody = true)
    suspend fun deleteCurrentUser(@Body req: DeleteAccountReq): Response<Unit>
}