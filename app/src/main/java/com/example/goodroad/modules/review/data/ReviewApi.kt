package com.example.goodroad.modules.review.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

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