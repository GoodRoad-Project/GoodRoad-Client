package com.example.goodroad.data.obstacle

import com.example.goodroad.data.obstacle.model.PolicyItem
import com.example.goodroad.data.obstacle.model.ReplacePolicyReq
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface ObstacleApi {

    @GET("/users/obstacles")
    suspend fun getUserObstaclePolicies(): Response<List<PolicyItem>>

    @PUT("/users/obstacles")
    suspend fun replaceUserObstaclePolicies(
        @Body req: ReplacePolicyReq
    ): Response<List<PolicyItem>>
}
