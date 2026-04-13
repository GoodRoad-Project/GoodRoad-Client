package com.example.goodroad.data.review

data class ReviewAddress(
    val country: String,
    val region: String,
    val localityType: String,
    val city: String,
    val street: String,
    val house: String,
    val placeName: String? = null
)

data class ReviewObstacle(
    val obstacleType: String,
    val severity: Short
)

data class UpsertReviewReq(
    val latitude: Double,
    val longitude: Double,
    val address: ReviewAddress,
    val rating: Short,
    val obstacles: List<ReviewObstacle>,
    val comment: String? = null,
    val photoUrls: List<String> = emptyList()
)

data class ReviewCardResp(
    val id: String,
    val featureId: String,
    val address: ReviewAddress,
    val latitude: Double,
    val longitude: Double,
    val rating: Short,
    val obstacles: List<ReviewObstacle>,
    val comment: String? = null,
    val photoUrls: List<String> = emptyList(),
    val status: String,
    val createdAt: String,
    val awardedPoints: Int,
    val moderatorComment: String? = null
)

data class ReviewPointsResp(
    val totalPoints: Int,
    val approvedReviews: Long
)
