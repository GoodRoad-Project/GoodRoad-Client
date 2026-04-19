package com.example.goodroad.data.moderationReview

import retrofit2.Response
import retrofit2.http.*

interface ModerationReviewApi {

    @GET("reviews/moderation/pending")
    suspend fun listPending(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ModerationPageResponse>

    @POST("reviews/moderation/{id}/take")
    suspend fun takeInWork(
        @Path("id") reviewId: String
    ): Response<ReviewForModeration>

    @POST("reviews/moderation/{id}/approve")
    suspend fun approve(
        @Path("id") reviewId: String
    ): Response<Unit>

    @POST("reviews/moderation/{id}/reject")
    suspend fun reject(
        @Path("id") reviewId: String,
        @Body request: RejectRequest
    ): Response<Unit>

    @POST("reviews/moderation/{id}/release")
    suspend fun release(
        @Path("id") reviewId: String
    ): Response<Unit>
}