package com.example.goodroad.data.network

import com.example.goodroad.data.network.route.RouteRequest
import com.example.goodroad.data.network.route.RouteResponse
import com.example.goodroad.data.place.PlaceInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GoodRoadApi {
    @POST("/routes")
    suspend fun getRoute(@Body request: RouteRequest): RouteResponse

    @GET("/places/info")
    suspend fun getPlaceInfo(@Query("lat") lat: Double, @Query("lon") lon: Double): Response<PlaceInfoResponse>
}