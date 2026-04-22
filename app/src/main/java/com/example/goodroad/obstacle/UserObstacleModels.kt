package com.example.goodroad.obstacle

data class PolicyItem(
    val obstacleType: String,
    val selected: Boolean,
    val maxAllowedSeverity: Short?
)

data class ReplacePolicyReq(
    val items: List<PolicyItem>
)