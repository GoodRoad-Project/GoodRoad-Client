package com.example.goodroad.domain.model

data class RouteVar(
    val route: Route,
    val summary: RouteSummary
)

data class RouteSummary(
    val totalDistance: String,
    val totalTime: String,
    val obstacleSum: Int
)