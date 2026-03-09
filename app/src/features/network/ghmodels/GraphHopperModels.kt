package com.example.goodroad.features.network.ghmodels

import com.google.gson.annotations.SerializedName

data class GraphHopperResponse(
    val path: List<Path>?,
    val info: Info?
)

data class Path(
    val distance: Double,
    val time: Long,
    val points: Points,
    @SerializedName("points_encoded")
    val pointsEncoded: Boolean = false,
    val instructions: List<Instruction>
)

data class Points(
    val coordinates: String
)
data class Instruction (
    val text: String,
    val distance: Double,
    val sign: Int,
    val time: Long
)

data class Info(
    val took: Double
)
