package com.example.goodroad.data.user

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

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