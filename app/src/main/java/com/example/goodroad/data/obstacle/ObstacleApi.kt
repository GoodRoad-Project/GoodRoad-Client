package com.example.goodroad.data.obstacle

import retrofit2.*
import retrofit2.http.*

interface ObstacleApi {

    @GET("/users/obstacles")
    suspend fun getUserObstaclePolicies(): Response<List<ObstaclePolicyItem>>

    @PUT("/users/obstacles")
    suspend fun replaceUserObstaclePolicies(
        @Body req: ReplaceObstaclePolicyReq
    ): Response<List<ObstaclePolicyItem>>
}
