package com.example.goodroad.data.review

import com.example.goodroad.data.user.UserApi
import okhttp3.MultipartBody
import retrofit2.HttpException

class ReviewRepository(
    private val reviewApi: ReviewApi,
    private val userApi: UserApi
) {

    suspend fun getOwnReviews(): List<ReviewCardResp> {
        val response = reviewApi.getOwnReviews()
        if (response.isSuccessful) {
            return response.body().orEmpty()
        }
        throw HttpException(response)
    }

    suspend fun getOwnReviewPoints(): ReviewPointsResp? {
        val response = reviewApi.getOwnReviewPoints()
        if (response.isSuccessful) {
            return response.body()
        }
        throw HttpException(response)
    }

    suspend fun createReview(req: UpsertReviewReq): ReviewCardResp? {
        val response = reviewApi.createReview(req)
        if (response.isSuccessful) {
            return response.body()
        }
        throw HttpException(response)
    }

    suspend fun updateReview(reviewId: String, req: UpsertReviewReq): ReviewCardResp? {
        val response = reviewApi.updateReview(reviewId, req)
        if (response.isSuccessful) {
            return response.body()
        }
        throw HttpException(response)
    }

    suspend fun deleteReview(reviewId: String) {
        val response = reviewApi.deleteReview(reviewId)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
    }

    suspend fun uploadReviewPhoto(file: MultipartBody.Part): String {
        val response = userApi.uploadAvatar(file)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        val body = response.body() ?: throw IllegalStateException("Сервер не вернул ссылку на фото")
        return body.photoUrl
    }
}
