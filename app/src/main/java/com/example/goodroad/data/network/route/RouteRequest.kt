package com.example.goodroad.data.network.route

import com.google.gson.annotations.SerializedName

data class RouteRequest(
    val start: String,
    val end: String,

    @SerializedName("user_id")
    val userId: String? = null,

    @SerializedName("max_stairs")
    val maxStairsCount: Int? = null,

    @SerializedName("max_slope")
    val maxSlopeAngle: Double? = null,

    @SerializedName("max_curb_height")
    val maxCurbHeight: Int? = null,

    @SerializedName("min_path_width")
    val minPathWidth: Int? = null,

    @SerializedName("avoid_stairs")
    val avoidStairs: Boolean = false,

    @SerializedName("need_ramp")
    val needRamp: Boolean = false,

    @SerializedName("avoid_bad_road")
    val avoidBadRoad: Boolean = false,

    @SerializedName("avoid_surfaces")
    val avoidSurfaceTypes: List<String> = emptyList(),

    @SerializedName("obstacle_policies")
    val obstaclePolicies: List<RouteObstaclePolicy> = emptyList(),

    val locale: String = "ru",

    @SerializedName("alternatives")
    val needAlternatives: Boolean = true,

    @SerializedName("points_encoded")
    val pointsEncoded: Boolean = true
)

data class RouteObstaclePolicy(
    @SerializedName("obstacle_type")
    val obstacleType: String,

    @SerializedName("max_allowed_severity")
    val maxAllowedSeverity: Short?
)
