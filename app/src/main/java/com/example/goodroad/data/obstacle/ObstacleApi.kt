package com.example.goodroad.data.obstacle

import com.example.goodroad.data.obstacle.model.PolicyItem
import com.example.goodroad.data.obstacle.model.ReplacePolicyReq
import com.example.goodroad.data.obstacle.model.ObstacleCardResp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ObstacleApi {

    @GET("/users/obstacles")
    suspend fun getUserObstaclePolicies(): Response<List<PolicyItem>>

    @PUT("/users/obstacles")
    suspend fun replaceUserObstaclePolicies(
        @Body req: ReplacePolicyReq
    ): Response<List<PolicyItem>>

    @GET("/obstacles/{id}")
    suspend fun getObstacleCard(
        @Path("id") id: String
    ): Response<ObstacleCardResp>
}
