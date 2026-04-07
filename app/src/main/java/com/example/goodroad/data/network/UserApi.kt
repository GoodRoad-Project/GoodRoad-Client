package com.example.goodroad.data.network

import com.example.goodroad.ui.user.*
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserApi {

    @GET("/users")
    suspend fun getCurrentUser(): Response<SettingsView>

    @PUT("/users")
    suspend fun updateCurrentUser(@Body req: UpdateSettingsReq): Response<SettingsView>

    @POST("/users")
    suspend fun changePassword(@Body req: ChangePasswordReq): Response<Unit>

    @HTTP(method = "DELETE", path = "/users", hasBody = true)
    suspend fun deleteCurrentUser(@Body req: DeleteAccountReq): Response<Unit>
}