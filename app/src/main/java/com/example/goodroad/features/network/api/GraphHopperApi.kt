package com.example.goodroad.features.network.api

import com.example.goodroad.model.RouteResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GraphHopperApi {

    @GET("route")
    suspend fun getRoute(
        @Query("point") points: List<String>,
        @Query("profile") profile: String,
        @Query("locale") locale: String = "ru",
        @Query("instructions") instructions: Boolean = true,
        @Query("calc_points") calcPoints: Boolean = true,
        @Query("points_encoded") pointsEncoded: Boolean = true,
        @Query("key") apiKey: String
    ): RouteResponse
}