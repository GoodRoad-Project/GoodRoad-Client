package com.example.goodroad.data.moderator

import retrofit2.Response
import retrofit2.http.*

interface ModeratorApi {

    @GET("/users/moderators/all")
    suspend fun getModerators(): Response<List<ModeratorView>>

    @POST("/users/moderators")
    suspend fun createModerator(
        @Body req: CreateModeratorReq
    ): Response<String>

    @PUT("/users/moderators/{id}")
    suspend fun disableModerator(
        @Path("id") id: String
    ): Response<Unit>
}