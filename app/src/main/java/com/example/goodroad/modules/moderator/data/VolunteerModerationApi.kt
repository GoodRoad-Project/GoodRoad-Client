package com.example.goodroad.modules.moderator.data

import retrofit2.Response
import retrofit2.http.*

interface VolunteerModerationApi {

    @GET("volunteer/moderation/applications/pending")
    suspend fun getPendingApplications(): Response<List<VolunteerApplicationResp>>

    @POST("volunteer/moderation/applications/{id}/approve")
    suspend fun approve(
        @Path("id") id: String
    ): Response<VolunteerApplicationResp>

    @POST("volunteer/moderation/applications/{id}/reject")
    suspend fun reject(
        @Path("id") id: String,
        @Body req: RejectReq
    ): Response<VolunteerApplicationResp>
}