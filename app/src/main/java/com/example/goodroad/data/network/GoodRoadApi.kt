package com.example.goodroad.data.network

import com.example.goodroad.data.network.route.RouteRequest
import com.example.goodroad.data.network.route.RouteResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GoodRoadApi {
    @POST("/api/v1/routes")
    suspend fun getRoute(@Body request: RouteRequest): RouteResponse
}