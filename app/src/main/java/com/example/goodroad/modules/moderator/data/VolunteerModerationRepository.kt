package com.example.goodroad.modules.moderator.data

import com.example.goodroad.data.network.ApiClient
import retrofit2.HttpException

class VolunteerModerationRepository {

    private val api = ApiClient.volunteerModerationApi

    suspend fun getPendingApplications(): List<VolunteerApplicationResp> {
        val response = api.getPendingApplications()

        if (response.isSuccessful) {
            return response.body().orEmpty()
        }

        throw HttpException(response)
    }

    suspend fun approve(id: String) {
        val response = api.approve(id)
        if (!response.isSuccessful) throw HttpException(response)
    }

    suspend fun reject(id: String, reason: String) {
        val response = api.reject(id, RejectReq(reason))
        if (!response.isSuccessful) throw HttpException(response)
    }
}