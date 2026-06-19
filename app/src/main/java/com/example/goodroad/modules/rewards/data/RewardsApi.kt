package com.example.goodroad.modules.rewards.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RewardsApi {

    @GET("rewards")
    suspend fun getRewards(
        @Query("minPrice") minPrice: Int? = null,
        @Query("maxPrice") maxPrice: Int? = null,
        @Query("sort") sort: String = "price_asc"
    ): List<RewardOffer>

    @POST("rewards/{id}/purchase")
    suspend fun purchaseReward(
        @Path("id") rewardId: String,
        @Body request: PurchaseRequest
    ): PurchaseResponse

    @GET("rewards/account")
    suspend fun getAccount(): RewardsAccount

    @GET("rewards/history")
    suspend fun getHistory(): List<PointTransaction>

    @GET("rewards/leaderboard")
    suspend fun getLeaderboard(): List<LeaderboardItem>
}