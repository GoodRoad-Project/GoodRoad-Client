package com.example.goodroad.data.obstacle

import com.example.goodroad.data.obstacle.model.ObstacleCardResp
import com.example.goodroad.data.obstacle.model.PolicyItem
import com.example.goodroad.data.obstacle.model.ReplacePolicyReq
import retrofit2.HttpException

class ObstacleRepository(private val api: ObstacleApi) {

    suspend fun getUserObstaclePolicies(): List<PolicyItem> {
        val response = api.getUserObstaclePolicies()

        if (response.isSuccessful) {
            return response.body().orEmpty()
        }

        throw HttpException(response)
    }

    suspend fun replaceUserObstaclePolicies(req: ReplacePolicyReq): List<PolicyItem> {
        val response = api.replaceUserObstaclePolicies(req)

        if (response.isSuccessful) {
            return response.body().orEmpty()
        }

        throw HttpException(response)
    }

    suspend fun getObstacleCard(id: String): ObstacleCardResp? {
        val response = api.getObstacleCard(id)
        return if (response.isSuccessful) response.body() else null
    }
}