package com.example.goodroad.modules.moderator.data

import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ModeratorRepositoryTest {

    @Test
    fun getModeratorsReturnsList() = runBlocking {
        val moderators = listOf(ModeratorView("1", "MODERATOR", "Анна", "Иванова", null, true))
        val api = FakeModeratorApi(getResp = Response.success(moderators))
        val repository = ModeratorRepository(api)

        val result = repository.getModerators()

        assertEquals(moderators, result)
    }

    @Test
    fun createModeratorSendsRequest() = runBlocking {
        val req = CreateModeratorReq("Анна", "Иванова", "+79990000151", "123")
        val api = FakeModeratorApi(createResp = Response.success("51"))
        val repository = ModeratorRepository(api)

        val result = repository.createModerator(req)

        assertEquals("51", result)
        assertEquals(req, api.createReq)
    }

    @Test
    fun disableModeratorThrowsForError() {
        val api = FakeModeratorApi(disableResp = errorResponse())
        val repository = ModeratorRepository(api)

        assertThrows(HttpException::class.java) {
            runBlocking {
                repository.disableModerator("51")
            }
        }
    }

    private class FakeModeratorApi(
        private val getResp: Response<List<ModeratorView>> = Response.success(emptyList()),
        private val createResp: Response<String> = Response.success("1"),
        private val disableResp: Response<Unit> = Response.success(Unit)
    ) : ModeratorApi {
        var createReq: CreateModeratorReq? = null

        override suspend fun getModerators(): Response<List<ModeratorView>> = getResp

        override suspend fun createModerator(req: CreateModeratorReq): Response<String> {
            createReq = req
            return createResp
        }

        override suspend fun disableModerator(id: String): Response<Unit> = disableResp
    }

    private fun <T> errorResponse(): Response<T> {
        return Response.error(400, "{}".toResponseBody(null))
    }
}
