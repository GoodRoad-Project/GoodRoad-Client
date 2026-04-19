package com.example.goodroad.data.moderationReview

import retrofit2.Response

class ModerationReviewRepository(
    private val api: ModerationReviewApi
) {

    @Throws(Exception::class)
    suspend fun listPending(page: Int, size: Int): ModerationPageResponse {
        val response: Response<ModerationPageResponse> = api.listPending(page, size)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Error: ${response.code()} - ${response.message()}")
        }
    }

    @Throws(Exception::class)
    suspend fun takeInWork(reviewId: String): ReviewForModeration {
        val response: Response<ReviewForModeration> = api.takeInWork(reviewId)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Error: ${response.code()} - ${response.message()}")
        }
    }

    @Throws(Exception::class)
    suspend fun approve(reviewId: String) {
        val response: Response<Unit> = api.approve(reviewId)
        if (!response.isSuccessful) {
            throw Exception("Error: ${response.code()} - ${response.message()}")
        }
    }

    @Throws(Exception::class)
    suspend fun reject(reviewId: String, reason: String) {
        val response: Response<Unit> = api.reject(reviewId, RejectRequest(reason))
        if (!response.isSuccessful) {
            throw Exception("Error: ${response.code()} - ${response.message()}")
        }
    }

    @Throws(Exception::class)
    suspend fun release(reviewId: String) {
        val response: Response<Unit> = api.release(reviewId)
        if (!response.isSuccessful) {
            throw Exception("Error: ${response.code()} - ${response.message()}")
        }
    }
}