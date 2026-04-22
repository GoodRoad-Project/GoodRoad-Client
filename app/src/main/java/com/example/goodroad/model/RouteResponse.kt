package com.example.goodroad.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Duration

data class RouteResponse(
    val id: String,                          // уникальный ID маршрута
    val paths: List<PathResponse>,           // варианты маршрутов
    val info: ResponseInfo? = null
)

data class PathResponse(
    val distance: Double,
    val time: Long,

    @JsonProperty("points_encoded")
    val pointsEncoded: Boolean = true,
    val points: String,

    val obstacles: List<ObstacleResponse> = emptyList(),

    @JsonProperty("route_type")
    val routeType: String = "fast",            // fast, safe, balanced
)


data class ObstacleResponse(
    val id: String,

    @JsonProperty("lat")
    val latitude: Double,

    @JsonProperty("lon")
    val longitude: Double,

    val type: String,                           // STAIRS, CURB и т.д.

    val details: ObstacleDetailsResponse? = null
)

data class ObstacleDetailsResponse(
    @JsonProperty("step_count")
    val stepCount: Int? = null,                  // для лестниц

    @JsonProperty("height_cm")
    val heightCm: Int? = null,                    // для бордюров

    @JsonProperty("angle_degrees")
    val angleDegrees: Double? = null,              // для уклонов

    @JsonProperty("has_ramp")
    val hasRamp: Boolean? = null,                  // есть пандус

    @JsonProperty("surface_type")
    val surfaceType: String? = null                 // покрытие
)

data class ResponseInfo(
    val took: Double
)