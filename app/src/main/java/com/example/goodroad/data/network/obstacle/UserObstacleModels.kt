package com.example.goodroad.data.network.obstacle

data class PolicyItem(
    val obstacleType: String,
    val selected: Boolean,
    val maxAllowedSeverity: Short?
)

data class ReplacePolicyReq(
    val items: List<PolicyItem>
)