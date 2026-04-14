package com.example.goodroad.data.obstacle

data class ObstaclePolicyItem(
    val obstacleType: String,
    val selected: Boolean,
    val maxAllowedSeverity: Short?
)

data class ReplaceObstaclePolicyReq(
    val items: List<ObstaclePolicyItem>
)
