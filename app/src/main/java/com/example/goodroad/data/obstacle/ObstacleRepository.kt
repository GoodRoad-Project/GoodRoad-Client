package com.example.goodroad.data.obstacle

import retrofit2.*

class ObstacleRepository(private val api: ObstacleApi) {

    suspend fun getUserObstaclePolicies(): List<ObstaclePolicyItem> {
        val response = api.getUserObstaclePolicies()
        if (response.isSuccessful) {
            return response.body().orEmpty()
        }
        throw HttpException(response)
    }

    suspend fun replaceUserObstaclePolicies(req: ReplaceObstaclePolicyReq): List<ObstaclePolicyItem> {
        val response = api.replaceUserObstaclePolicies(req)
        if (response.isSuccessful) {
            return response.body().orEmpty()
        }
        throw HttpException(response)
    }
}
