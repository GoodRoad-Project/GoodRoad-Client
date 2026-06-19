package com.example.goodroad.data.network

import com.example.goodroad.data.network.route.RouteRequest
import com.example.goodroad.data.network.route.RouteResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GoodRoadApi {
    @POST("/api/v1/routes")
    suspend fun getRoute(@Body request: RouteRequest): RouteResponse

    @GET("/places/info")
    suspend fun getPlaceInfo(@Query("lat") lat: Double, @Query("lon") lon: Double): Response<PlaceInfoResponse>
}