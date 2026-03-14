package com.example.goodroad.domain.model

data class Route(
    val id: String,
    val points: List<LocationPoint>,
    val distance: Double,
    val duration: Long,
    val type: RouteType,
    val obstacles: List<Obstacle> = emptyList()
)
enum class RouteType{
    FAST,
    SAFE,
    BALANCED
}