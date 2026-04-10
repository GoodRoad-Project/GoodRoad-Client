package com.example.goodroad.domain.model

data class Obstacle(
    val id: String,
    val position: LocationPoint,
    val type: ObstacleType,
    val details: ObstacleDetails
)

enum class ObstacleType{
    CURB,
    STAIRS,
    ROAD_SLOPE,
    POTHOLES,
    SAND,
    GRAVEL;
}

sealed class ObstacleDetails {

    data class Stairs(
        val stepCount: Int,
        val hasRamp: Boolean = false
    ) : ObstacleDetails()

    data class Slope(
        val angleDegrees: Double,
    ) : ObstacleDetails()

    data class Curb(
        val heightCm: Int,
        val hasRampCut: Boolean = false
    ) : ObstacleDetails()

}

