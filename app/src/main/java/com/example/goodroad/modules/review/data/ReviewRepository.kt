package com.example.goodroad.modules.review.data

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

data class ReviewPhotoUploadResp(
    val photoUrl: String
)

class ReviewRepository(
    private val reviewApi: ReviewApi,
) {

    private val gson = Gson()

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

    suspend fun uploadReviewPhoto(file: MultipartBody.Part): String {
        val response = reviewApi.uploadReviewPhoto(file)
        if (response.isSuccessful) {
            return response.body()?.photoUrl
                ?: throw IllegalStateException("Empty upload response")
        }
        throw HttpException(response)
    }

    suspend fun createReviewMultipart(
        req: UpsertReviewReq,
        photos: List<MultipartBody.Part>?
    ): ReviewCardResp? {
        val jsonPart = req.toRequestBody()

        val response = reviewApi.createReviewMultipart(
            data = jsonPart,
            photos = photos
        )

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

    private fun UpsertReviewReq.toRequestBody(): RequestBody {
        return gson.toJson(this)
            .toRequestBody("application/json".toMediaType())
    }
}