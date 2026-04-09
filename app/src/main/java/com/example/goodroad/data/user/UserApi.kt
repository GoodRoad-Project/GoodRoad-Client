package com.example.goodroad.data.user

import com.example.goodroad.data.user.DeleteAccountReq
import com.example.goodroad.data.user.SettingsView
import com.example.goodroad.data.user.UpdateUserReq
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PUT

interface UserApi {

    @GET("/users")
    suspend fun getCurrentUser(): Response<SettingsView>

    @PUT("/users")
    suspend fun updateCurrentUser(@Body req: UpdateUserReq): Response<SettingsView>

    @HTTP(method = "DELETE", path = "/users", hasBody = true)
    suspend fun deleteCurrentUser(@Body req: DeleteAccountReq): Response<Unit>
}