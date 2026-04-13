package com.example.goodroad.data.moderator

import com.example.goodroad.data.network.ApiClient
import retrofit2.HttpException

class ModeratorRepository {

    private val api = ApiClient.moderatorApi

    suspend fun getModerators(): List<ModeratorView> {
        val response = api.getModerators()

        if (response.isSuccessful) {
            return response.body().orEmpty()
        }

        throw HttpException(response)
    }

    suspend fun createModerator(req: CreateModeratorReq): String {
        val response = api.createModerator(req)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        }

        throw HttpException(response)
    }

    suspend fun disableModerator(id: String) {
        val response = api.disableModerator(id)

        if (!response.isSuccessful) {
            throw HttpException(response)
        }
    }
}