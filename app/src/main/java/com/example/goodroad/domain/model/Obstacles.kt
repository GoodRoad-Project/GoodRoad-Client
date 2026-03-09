package com.example.goodroad.domain.model

data class Obstacle(
    val id: String,
    val position: LocationPoint,
    val type: ObstacleType,
    val severity: ObstacleSeverity? = null, // если null то препятсвие нам не мешает
)

enum class ObstacleType{
    CURB,
    STAIRS,
    ROAD_SLOPE,
    POTHOLES,
    SAND,
    GRAVEL;
}
enum class ObstacleSeverity{
    LITE,                   // Может преодолеть препятствие этого типа с трудом
    MEDIUM,                 // Может преодолеть только небольшое препятствие этого типа
    IMPOSSIBLE              // Не может преодолеть это препятствие ни в каком виде
}

