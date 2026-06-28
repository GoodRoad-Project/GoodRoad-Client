package com.example.goodroad.modules.rewards.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class RewardsRepositoryTest {

    @Test
    fun getRewardsReturnsList() = runBlocking {
        val reward = RewardOffer(
            id = "1",
            partnerName = "Partner",
            title = "Coffee",
            description = "Free coffee",
            price = 100
        )

        val api = FakeRewardsApi(
            rewards = listOf(reward)
        )

        val repository = RewardsRepository(api)

        val result = repository.getRewards(10, 200, "price_asc")

        assertEquals(listOf(reward), result)
    }

    @Test
    fun purchaseRewardSendsRequestAndReturnsResponse() = runBlocking {
        val reward = RewardOffer(
            id = "1",
            partnerName = "Partner",
            title = "Coffee",
            description = "Free coffee",
            price = 100
        )

        val response = PurchaseResponse(
            id = "999",
            reward = reward,
            balanceAfter = 900
        )

        val api = FakeRewardsApi(
            purchaseResponse = response
        )

        val repository = RewardsRepository(api)

        val result = repository.purchaseReward("1")

        assertEquals("999", result.id)
        assertEquals("1", api.purchaseRewardId)
        assertEquals(true, api.purchaseRequest?.confirmed)
    }

    @Test
    fun getAccountReturnsData() = runBlocking {
        val account = RewardsAccount(
            balance = 1000,
            lifetimePoints = 5000,
            completedTasksCount = 12,
            title = "Gold"
        )

        val api = FakeRewardsApi(account = account)
        val repository = RewardsRepository(api)

        val result = repository.getAccount()

        assertEquals(account.balance, result.balance)
    }

    @Test
    fun getHistoryReturnsList() = runBlocking {
        val tx = PointTransaction(
            id = "1",
            amount = 100,
            type = "EARN",
            description = "Task",
            details = null,
            balanceAfter = 1000,
            createdAt = "2026-01-01"
        )

        val api = FakeRewardsApi(history = listOf(tx))
        val repository = RewardsRepository(api)

        val result = repository.getHistory()

        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
    }

    @Test
    fun getLeaderboardReturnsList() = runBlocking {
        val item = LeaderboardItem(
            userId = "u1",
            firstName = "Ivan",
            lastName = "Ivanov",
            lifetimePoints = 1000,
            title = "Gold"
        )

        val api = FakeRewardsApi(leaderboard = listOf(item))
        val repository = RewardsRepository(api)

        val result = repository.getLeaderboard()

        assertEquals(1, result.size)
        assertEquals("u1", result[0].userId)
    }

    private class FakeRewardsApi(
        private val rewards: List<RewardOffer> = emptyList(),
        private val purchaseResponse: PurchaseResponse = PurchaseResponse(
            id = "1",
            reward = RewardOffer("1", "", "", null, 0),
            balanceAfter = 0
        ),
        private val account: RewardsAccount = RewardsAccount(0, 0, 0, ""),
        private val history: List<PointTransaction> = emptyList(),
        private val leaderboard: List<LeaderboardItem> = emptyList()
    ) : RewardsApi {

        var purchaseRewardId: String? = null
        var purchaseRequest: PurchaseRequest? = null

        override suspend fun getRewards(
            minPrice: Int?,
            maxPrice: Int?,
            sort: String
        ): List<RewardOffer> {
            return rewards
        }

        override suspend fun purchaseReward(
            rewardId: String,
            request: PurchaseRequest
        ): PurchaseResponse {
            purchaseRewardId = rewardId
            purchaseRequest = request
            return purchaseResponse
        }

        override suspend fun getAccount() = account

        override suspend fun getHistory() = history

        override suspend fun getLeaderboard() = leaderboard
    }
}