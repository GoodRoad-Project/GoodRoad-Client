package com.example.goodroad.model

import com.fasterxml.jackson.annotation.JsonProperty

data class RouteRequest(
    val start: String,              // (lat,lon)
    val end: String,                // (lat,lon)
    @JsonProperty("user_id")
    val userId: String? = null,

    @JsonProperty("max_stairs")
    val maxStairsCount: Int? = null,        // сколько ступенек максимум
    @JsonProperty("max_slope")
    val maxSlopeAngle: Double? = null,       // макс угол уклона
    @JsonProperty("max_curb_height")
    val maxCurbHeight: Int? = null,          // макс высота бордюра
    @JsonProperty("min_path_width")
    val minPathWidth: Int? = null,            // мин ширина прохода

    @JsonProperty("avoid_stairs")
    val avoidStairs: Boolean = false,            // избегать лестниц
    @JsonProperty("need_ramp")
    val needRamp: Boolean = false,                // нужен пандус
    @JsonProperty("avoid_bad_road")
    val avoidBadRoad: Boolean = false,            // избегать плохих дорог

    // какие поверхности избегать
    @JsonProperty("avoid_surfaces")
    val avoidSurfaceTypes: List<String> = emptyList(),  // "SAND", "GRAVEL"

    val locale: String = "ru",                    // язык инструкций
    @JsonProperty("alternatives")
    val needAlternatives: Boolean = true,          // нужны ли альтернативы
    @JsonProperty("points_encoded")
    val pointsEncoded: Boolean = true
)