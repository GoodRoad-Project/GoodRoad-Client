package com.example.goodroad.data.moderationReview

import com.example.goodroad.data.review.ReviewAddress
import com.example.goodroad.data.review.ReviewObstacle
import java.time.Instant

data class ReviewForModeration(
    val id: String,
    val featureId: String,
    val authorId: String?,
    val address: ReviewAddress,
    val latitude: Double,
    val longitude: Double,
    val rating: Short,
    val obstacles: List<ReviewObstacle>,
    val comment: String?,
    val photoUrls: List<String>,
    val status: String,
    val createdAt: Instant,
    val takenInWork: Boolean,
    val takenByMe: Boolean,
    val takenByModeratorId: String?,
    val takenAt: Instant?,
    val moderatorComment: String?
)

data class ModerationPageResponse(
    val items: List<ReviewForModeration>,
    val page: Int,
    val size: Int,
    val total: Long
)

data class RejectRequest(
    val reason: String
)