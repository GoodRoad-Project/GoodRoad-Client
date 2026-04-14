package com.example.goodroad.data.review

import retrofit2.Response
import retrofit2.http.*

interface ReviewApi {

    @GET("/reviews/own")
    suspend fun getOwnReviews(): Response<List<ReviewCardResp>>

    @GET("/reviews/points")
    suspend fun getOwnReviewPoints(): Response<ReviewPointsResp>

    @POST("/reviews")
    suspend fun createReview(@Body req: UpsertReviewReq): Response<ReviewCardResp>

    @PATCH("/reviews/{id}")
    suspend fun updateReview(
        @Path("id") reviewId: String,
        @Body req: UpsertReviewReq
    ): Response<ReviewCardResp>

    @DELETE("/reviews/{id}")
    suspend fun deleteReview(@Path("id") reviewId: String): Response<Unit>
}
