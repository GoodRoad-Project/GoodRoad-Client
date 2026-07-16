package com.example.goodroad.data.obstacle.model

import java.time.Instant

data class ObstacleCardResp(
    val id: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val address: AddressResp?,
    val severityEstimate: Short?,
    val reviewsCount: Int,
    val lastReviewedAt: Instant?,
    val reviews: List<ReviewResp>
)