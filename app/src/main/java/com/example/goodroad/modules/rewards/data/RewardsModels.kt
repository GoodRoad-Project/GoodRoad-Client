package com.example.goodroad.modules.rewards.data

data class RewardOffer(
    val id: String,
    val partnerName: String,
    val title: String,
    val description: String?,
    val price: Int
)

data class PurchaseRequest(
    val confirmed: Boolean = true
)

data class PurchaseResponse(
    val id: String?,
    val reward: RewardOffer,
    val balanceAfter: Int
)

data class RewardsAccount(
    val balance: Int,
    val lifetimePoints: Int,
    val completedTasksCount: Int,
    val title: String
)

data class LeaderboardItem(
    val userId: String,
    val firstName: String?,
    val lastName: String?,
    val lifetimePoints: Int,
    val title: String
)

data class PointTransaction(
    val id: String?,
    val amount: Int,
    val type: String,
    val description: String?,
    val details: String?,
    val balanceAfter: Int,
    val createdAt: String
)