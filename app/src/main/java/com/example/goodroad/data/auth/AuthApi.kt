package com.example.goodroad.data.auth

import retrofit2.*
import retrofit2.http.*

interface AuthApi {

    @POST("/auth/login")
    suspend fun login(@Body req: LoginReq): AuthResp

    @POST("/auth/register")
    suspend fun register(@Body req: RegisterReq): AuthResp

    @POST("/auth/recover-password")
    suspend fun recoverPassword(@Body req: RecoverPasswordReq): Response<Unit>
}