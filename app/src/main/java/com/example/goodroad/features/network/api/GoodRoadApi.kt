package com.example.goodroad.features.network.api

import com.example.goodroad.model.RouteRequest
import com.example.goodroad.model.RouteResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Header
import com.example.goodroad.obstacle.ObstacleMapItemResp
import com.example.goodroad.obstacle.PolicyItem
import com.example.goodroad.obstacle.ReplacePolicyReq
interface GoodRoadApi {
    @POST("/api/v1/routes")
    suspend fun getRoute(@Body request: RouteRequest): RouteResponse
}