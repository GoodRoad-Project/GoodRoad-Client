package com.example.goodroad.modules.moderationReview.data

import com.example.goodroad.modules.review.data.ReviewAddress
import com.example.goodroad.modules.review.data.ReviewObstacle
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.Response
import java.time.Instant

class ModerationReviewRepositoryTest {

    @Test
    fun listPendingReturnsBody() = runBlocking {
        val page = ModerationPageResponse(listOf(review()), 0, 20, 1)
        val api = FakeModerationReviewApi(listResp = Response.success(page))
        val repository = ModerationReviewRepository(api)

        val result = repository.listPending(0, 20)

        assertEquals(1, result.total)
    }

    @Test
    fun takeInWorkReturnsReview() = runBlocking {
        val api = FakeModerationReviewApi(takeResp = Response.success(review("15")))
        val repository = ModerationReviewRepository(api)

        val result = repository.takeInWork("15")

        assertEquals("15", result.id)
    }

    @Test
    fun approveSendsReviewId() = runBlocking {
        val api = FakeModerationReviewApi()
        val repository = ModerationReviewRepository(api)

        repository.approve("15")

        assertEquals("15", api.approveId)
    }

    @Test
    fun rejectSendsReason() = runBlocking {
        val api = FakeModerationReviewApi()
        val repository = ModerationReviewRepository(api)

        repository.reject("15", "Недостаточно данных")

        assertEquals("15", api.rejectId)
        assertEquals(RejectRequest("Недостаточно данных"), api.rejectReq)
    }

    @Test
    fun releaseThrowsForError() {
        val api = FakeModerationReviewApi(releaseResp = errorResponse())
        val repository = ModerationReviewRepository(api)

        assertThrows(Exception::class.java) {
            runBlocking {
                repository.release("15")
            }
        }
    }

    private class FakeModerationReviewApi(
        private val listResp: Response<ModerationPageResponse> =
            Response.success(ModerationPageResponse(emptyList(), 0, 20, 0)),
        private val takeResp: Response<ReviewForModeration> = Response.success(review()),
        private val approveResp: Response<Unit> = Response.success(Unit),
        private val rejectResp: Response<Unit> = Response.success(Unit),
        private val releaseResp: Response<Unit> = Response.success(Unit)
    ) : ModerationReviewApi {
        var approveId: String? = null
        var rejectId: String? = null
        var rejectReq: RejectRequest? = null

        override suspend fun listPending(page: Int, size: Int): Response<ModerationPageResponse> = listResp

        override suspend fun takeInWork(reviewId: String): Response<ReviewForModeration> = takeResp

        override suspend fun approve(reviewId: String): Response<Unit> {
            approveId = reviewId
            return approveResp
        }

        override suspend fun reject(reviewId: String, request: RejectRequest): Response<Unit> {
            rejectId = reviewId
            rejectReq = request
            return rejectResp
        }

        override suspend fun release(reviewId: String): Response<Unit> = releaseResp
    }

    companion object {
        private fun review(id: String = "10") = ReviewForModeration(
            id = id,
            featureId = "201",
            authorId = "1",
            address = ReviewAddress(
                country = "Россия",
                region = "Санкт-Петербург",
                localityType = "город",
                city = "Санкт-Петербург",
                street = "Садовая",
                house = "10"
            ),
            latitude = 59.93,
            longitude = 30.33,
            rating = 4.toShort(),
            obstacles = listOf(ReviewObstacle("STAIRS", 3.toShort())),
            comment = "Есть лестница",
            photoUrls = listOf("https://storage/review.png"),
            status = "PENDING",
            createdAt = Instant.parse("2026-05-01T10:00:00Z"),
            takenInWork = true,
            takenByMe = true,
            takenByModeratorId = "51",
            takenAt = Instant.parse("2026-05-01T10:10:00Z"),
            moderatorComment = null
        )

        private fun <T> errorResponse(): Response<T> {
            return Response.error(400, "{}".toResponseBody(null))
        }
    }
}
