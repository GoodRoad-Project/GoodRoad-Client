package com.example.goodroad.modules.review.data

import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ReviewRepositoryTest {

    @Test
    fun getOwnReviewsReturnsList() = runBlocking {
        val review = review()
        val api = FakeReviewApi(ownResp = Response.success(listOf(review)))
        val repository = ReviewRepository(api)

        val result = repository.getOwnReviews()

        assertEquals(listOf(review), result)
    }

    @Test
    fun createReviewSendsRequest() = runBlocking {
        val req = request()
        val api = FakeReviewApi(createResp = Response.success(review("11")))
        val repository = ReviewRepository(api)

        val result = repository.createReview(req)

        assertEquals("11", result?.id)
        assertEquals(req, api.createReq)
    }

    @Test
    fun uploadReviewPhotoReturnsUrl() = runBlocking {
        val api = FakeReviewApi(
            uploadResp = Response.success(ReviewPhotoUploadResp("https://storage/review.png"))
        )
        val repository = ReviewRepository(api)

        val result = repository.uploadReviewPhoto(part())

        assertEquals("https://storage/review.png", result)
    }

    @Test
    fun updateReviewSendsIdAndRequest() = runBlocking {
        val req = request()
        val api = FakeReviewApi(updateResp = Response.success(review("12")))
        val repository = ReviewRepository(api)

        val result = repository.updateReview("12", req)

        assertEquals("12", result?.id)
        assertEquals("12", api.updateId)
        assertEquals(req, api.updateReq)
    }

    @Test
    fun deleteReviewThrowsForError() {
        val api = FakeReviewApi(deleteResp = errorResponse())
        val repository = ReviewRepository(api)

        assertThrows(HttpException::class.java) {
            runBlocking {
                repository.deleteReview("12")
            }
        }
    }

    private class FakeReviewApi(
        private val ownResp: Response<List<ReviewCardResp>> = Response.success(emptyList()),
        private val pointsResp: Response<ReviewPointsResp> = Response.success(ReviewPointsResp(0, 0)),
        private val createResp: Response<ReviewCardResp> = Response.success(review()),
        private val uploadResp: Response<ReviewPhotoUploadResp> = Response.success(ReviewPhotoUploadResp("url")),
        private val multipartResp: Response<ReviewCardResp> = Response.success(review()),
        private val updateResp: Response<ReviewCardResp> = Response.success(review()),
        private val deleteResp: Response<Unit> = Response.success(Unit)
    ) : ReviewApi {
        var createReq: UpsertReviewReq? = null
        var updateId: String? = null
        var updateReq: UpsertReviewReq? = null

        override suspend fun getOwnReviews(): Response<List<ReviewCardResp>> = ownResp

        override suspend fun getOwnReviewPoints(): Response<ReviewPointsResp> = pointsResp

        override suspend fun createReview(req: UpsertReviewReq): Response<ReviewCardResp> {
            createReq = req
            return createResp
        }

        override suspend fun updateReview(reviewId: String, req: UpsertReviewReq): Response<ReviewCardResp> {
            updateId = reviewId
            updateReq = req
            return updateResp
        }

        override suspend fun deleteReview(reviewId: String): Response<Unit> = deleteResp

        override suspend fun uploadReviewPhoto(file: MultipartBody.Part): Response<ReviewPhotoUploadResp> {
            return uploadResp
        }

        override suspend fun createReviewMultipart(
            data: RequestBody,
            photos: List<MultipartBody.Part>?
        ): Response<ReviewCardResp> {
            return multipartResp
        }
    }

    companion object {
        private fun request() = UpsertReviewReq(
            latitude = 59.93,
            longitude = 30.33,
            address = ReviewAddress(
                country = "Россия",
                region = "Санкт-Петербург",
                localityType = "город",
                city = "Санкт-Петербург",
                street = "Садовая",
                house = "10"
            ),
            rating = 4.toShort(),
            obstacles = listOf(ReviewObstacle("STAIRS", 3.toShort())),
            comment = "Есть лестница",
            photoUrls = listOf("https://storage/review.png")
        )

        private fun review(id: String = "10") = ReviewCardResp(
            id = id,
            featureId = "201",
            address = request().address,
            latitude = 59.93,
            longitude = 30.33,
            rating = 4.toShort(),
            obstacles = request().obstacles,
            comment = "Есть лестница",
            photoUrls = listOf("https://storage/review.png"),
            status = "PENDING",
            createdAt = "2026-05-01T10:00:00Z",
            awardedPoints = 0
        )

        private fun part(): MultipartBody.Part {
            val body = "image".toRequestBody("image/png".toMediaType())
            return MultipartBody.Part.createFormData("file", "review.png", body)
        }

        private fun <T> errorResponse(): Response<T> {
            return Response.error(400, "{}".toResponseBody(null))
        }
    }
}
