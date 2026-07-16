package com.example.goodroad.data.obstacle.model
import java.time.Instant

data class ReviewResp(
    val id: String,
    val rating: Short,
    val comment: String,
    val createdAt: Instant,
    val photoUrls: List<String>,
    val obstacles: List<ObstacleItemResp>
)