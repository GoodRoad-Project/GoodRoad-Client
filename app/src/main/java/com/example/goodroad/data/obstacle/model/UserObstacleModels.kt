package com.example.goodroad.data.obstacle.model

data class PolicyItem(
    val obstacleType: String,
    val selected: Boolean,
    val maxAllowedSeverity: Short?
)

data class ReplacePolicyReq(
    val items: List<PolicyItem>
)