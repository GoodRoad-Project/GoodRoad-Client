package com.example.goodroad.obstacle

// data/obstacles/ObstacleModels.kt

import java.time.Instant

data class ObstacleMapItemResp(
    val id: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val address: AddressResp?,
    val severityEstimate: Short?,
    val reviewsCount: Int,
    val lastReviewedAt: Instant?
)

data class AddressResp(
    val country: String?,
    val region: String?,
    val localityType: String?,
    val city: String?,
    val street: String?,
    val house: String?,
    val placeName: String?
)