package com.example.goodroad.modules.gamification.data

class RewardsRepository(
    private val api: RewardsApi
) {

    suspend fun getRewards(
        minPrice: Int? = null,
        maxPrice: Int? = null,
        sort: String = "price_asc"
    ): List<RewardOffer> {
        return api.getRewards(minPrice, maxPrice, sort)
    }

    suspend fun purchaseReward(rewardId: String): PurchaseResponse {
        return api.purchaseReward(
            rewardId = rewardId,
            request = PurchaseRequest(confirmed = true)
        )
    }

    suspend fun getAccount(): RewardsAccount {
        return api.getAccount()
    }

    suspend fun getHistory(): List<PointTransaction> {
        return api.getHistory()
    }

    suspend fun getLeaderboard(): List<LeaderboardItem> {
        return api.getLeaderboard()
    }
}