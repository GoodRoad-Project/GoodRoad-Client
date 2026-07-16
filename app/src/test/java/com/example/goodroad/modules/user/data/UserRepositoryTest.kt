package com.example.goodroad.modules.user.data

import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class UserRepositoryTest {

    @Test
    fun getCurrentUserReturnsBody() = runBlocking {
        val view = settings()
        val api = FakeUserApi(currentResp = Response.success(view))
        val repository = UserRepository(api)

        val result = repository.getCurrentUser()

        assertEquals(view, result)
    }

    @Test
    fun updateCurrentUserSendsRequest() = runBlocking {
        val view = settings(lastName = "Иванов")
        val api = FakeUserApi(updateResp = Response.success(view))
        val repository = UserRepository(api)
        val req = UpdateUserReq(firstName = "Иван", lastName = "Иванов", phone = "+79990000001")

        val result = repository.updateCurrentUser(req)

        assertEquals(view, result)
        assertEquals(req, api.updateReq)
    }

    @Test
    fun changePasswordThrowsForError() {
        val api = FakeUserApi(changePasswordResp = errorResponse())
        val repository = UserRepository(api)

        assertThrows(HttpException::class.java) {
            runBlocking {
                repository.changePassword("old", "new")
            }
        }
    }

    @Test
    fun uploadAvatarReturnsBody() = runBlocking {
        val api = FakeUserApi(
            avatarResp = Response.success(AvatarUploadResp("https://storage/avatar.png"))
        )
        val repository = UserRepository(api)

        val result = repository.uploadAvatar(part())

        assertEquals("https://storage/avatar.png", result?.photoUrl)
    }

    @Test
    fun deleteCurrentUserSendsRequest() = runBlocking {
        val api = FakeUserApi(deleteResp = Response.success(Unit))
        val repository = UserRepository(api)
        val req = DeleteAccountReq("123")

        repository.deleteCurrentUser(req)

        assertEquals(req, api.deleteReq)
    }

    private class FakeUserApi(
        private val currentResp: Response<SettingsView> = Response.success(settings()),
        private val updateResp: Response<SettingsView> = Response.success(settings()),
        private val changePasswordResp: Response<Unit> = Response.success(Unit),
        private val avatarResp: Response<AvatarUploadResp> = Response.success(AvatarUploadResp("url")),
        private val deleteResp: Response<Unit> = Response.success(Unit)
    ) : UserApi {
        var updateReq: UpdateUserReq? = null
        var deleteReq: DeleteAccountReq? = null

        override suspend fun getCurrentUser(): Response<SettingsView> = currentResp

        override suspend fun updateCurrentUser(req: UpdateUserReq): Response<SettingsView> {
            updateReq = req
            return updateResp
        }

        override suspend fun changePassword(oldPassword: String, newPassword: String): Response<Unit> {
            return changePasswordResp
        }

        override suspend fun uploadAvatar(file: MultipartBody.Part): Response<AvatarUploadResp> {
            return avatarResp
        }

        override suspend fun deleteCurrentUser(req: DeleteAccountReq): Response<Unit> {
            deleteReq = req
            return deleteResp
        }
    }

    companion object {
        private fun settings(lastName: String = "Петров") = SettingsView(
            id = "1",
            role = "USER",
            firstName = "Иван",
            lastName = lastName,
            photoUrl = null,
            active = true
        )

        private fun part(): MultipartBody.Part {
            val body = "image".toRequestBody("image/png".toMediaType())
            return MultipartBody.Part.createFormData("file", "avatar.png", body)
        }

        private fun <T> errorResponse(): Response<T> {
            return Response.error(400, "{}".toResponseBody(null))
        }
    }
}
