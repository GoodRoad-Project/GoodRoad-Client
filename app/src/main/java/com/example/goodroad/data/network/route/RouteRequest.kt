package com.example.goodroad.data.network.route

import com.google.gson.annotations.SerializedName

data class RouteRequest(

    val start: String,              // (lat,lon)
    val end: String,                // (lat,lon)

    @SerializedName("user_id")
    val userId: String? = null,

    @SerializedName("max_stairs")
    val maxStairsCount: Int? = null,        // сколько ступенек максимум

    @SerializedName("max_slope")
    val maxSlopeAngle: Double? = null,      // макс угол уклона

    @SerializedName("max_curb_height")
    val maxCurbHeight: Int? = null,         // макс высота бордюра

    @SerializedName("min_path_width")
    val minPathWidth: Int? = null,          // мин ширина прохода

    @SerializedName("avoid_stairs")
    val avoidStairs: Boolean = false,       // избегать лестниц

    @SerializedName("need_ramp")
    val needRamp: Boolean = false,          // нужен пандус

    @SerializedName("avoid_bad_road")
    val avoidBadRoad: Boolean = false,      // избегать плохих дорог

    // какие поверхности избегать
    @SerializedName("avoid_surfaces")
    val avoidSurfaceTypes: List<String> = emptyList(),  // "SAND", "GRAVEL"

    val locale: String = "ru",              // язык инструкций

    @SerializedName("alternatives")
    val needAlternatives: Boolean = true,   // нужны ли альтернативы

    @SerializedName("points_encoded")
    val pointsEncoded: Boolean = true
)