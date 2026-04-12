package com.example.goodroad.features.network.api

import com.example.goodroad.model.RouteRequest
import com.example.goodroad.model.RouteResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GoodRoadApi {
    @POST("/api/v1/routes")
    suspend fun getRoute(@Body request: RouteRequest): RouteResponse
}