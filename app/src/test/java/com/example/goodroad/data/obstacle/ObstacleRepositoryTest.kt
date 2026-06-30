package com.example.goodroad.data.obstacle

import com.example.goodroad.data.obstacle.model.PolicyItem
import com.example.goodroad.data.obstacle.model.ReplacePolicyReq
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import com.example.goodroad.data.obstacle.model.ObstacleCardResp

class ObstacleRepositoryTest {

    @Test
    fun getUserObstaclePoliciesReturnsBody() = runBlocking {
        val policies = listOf(PolicyItem("STAIRS", true, 1.toShort()))
        val api = FakeObstacleApi(getResp = Response.success(policies))
        val repository = ObstacleRepository(api)

        val result = repository.getUserObstaclePolicies()

        assertEquals(policies, result)
    }

    @Test
    fun replaceUserObstaclePoliciesSendsRequest() = runBlocking {
        val policies = listOf(PolicyItem("CURB", true, 2.toShort()))
        val req = ReplacePolicyReq(policies)
        val api = FakeObstacleApi(replaceResp = Response.success(policies))
        val repository = ObstacleRepository(api)

        val result = repository.replaceUserObstaclePolicies(req)

        assertEquals(policies, result)
        assertEquals(req, api.replaceReq)
    }

    @Test
    fun getUserObstaclePoliciesThrowsForError() {
        val api = FakeObstacleApi(getResp = errorResponse())
        val repository = ObstacleRepository(api)

        assertThrows(HttpException::class.java) {
            runBlocking {
                repository.getUserObstaclePolicies()
            }
        }
    }

    private class FakeObstacleApi(
        private val getResp: Response<List<PolicyItem>> = Response.success(emptyList()),
        private val replaceResp: Response<List<PolicyItem>> = Response.success(emptyList()),
        private val cardResp: Response<ObstacleCardResp> = Response.success(null)
    ) : ObstacleApi {
        var replaceReq: ReplacePolicyReq? = null

        override suspend fun getUserObstaclePolicies(): Response<List<PolicyItem>> = getResp

        override suspend fun replaceUserObstaclePolicies(req: ReplacePolicyReq): Response<List<PolicyItem>> {
            replaceReq = req
            return replaceResp
        }
        override suspend fun getObstacleCard(id: String): Response<ObstacleCardResp> = cardResp
    }

    private fun <T> errorResponse(): Response<T> {
        return Response.error(400, "{}".toResponseBody(null))
    }
}
