package com.example.goodroad.data.network.route

import com.google.gson.annotations.SerializedName

data class RouteResponse(

    val id: String,                          // уникальный ID маршрута

    val paths: List<PathResponse>,           // варианты маршрутов

    val info: ResponseInfo? = null
)

data class PathResponse(

    val distance: Double,

    val time: Long,

    @SerializedName("points_encoded")
    val pointsEncoded: Boolean = true,

    val points: String,

    val obstacles: List<ObstacleResponse> = emptyList(),

    @SerializedName("route_type")
    val routeType: String = "fast",          // fast, safe, balanced
)

data class ObstacleResponse(

    val id: String,

    @SerializedName("lat")
    val latitude: Double,

    @SerializedName("lon")
    val longitude: Double,

    val type: String,                        // STAIRS, CURB и т.д.

    val details: ObstacleDetailsResponse? = null
)

data class ObstacleDetailsResponse(

    @SerializedName("step_count")
    val stepCount: Int? = null,              // для лестниц

    @SerializedName("height_cm")
    val heightCm: Int? = null,               // для бордюров

    @SerializedName("angle_degrees")
    val angleDegrees: Double? = null,        // для уклонов

    @SerializedName("has_ramp")
    val hasRamp: Boolean? = null,            // есть пандус

    @SerializedName("surface_type")
    val surfaceType: String? = null          // покрытие
)

data class ResponseInfo(

    val took: Double
)