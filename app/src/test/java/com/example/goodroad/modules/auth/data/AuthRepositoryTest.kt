package com.example.goodroad.modules.auth.data

import android.content.Context
import com.example.goodroad.data.network.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import retrofit2.HttpException
import retrofit2.Response

class AuthRepositoryTest {

    @Test
    fun loginUserReturnsResponse() = runBlocking {
        val api = FakeAuthApi(
            loginResp = AuthResp(
                user = UserDto(id = "1", role = "USER"),
                accessToken = "access",
                refreshToken = "refresh"
            )
        )

        val tokenManager = mock(TokenManager::class.java)

        val repository = AuthRepository(
            context = mock(Context::class.java),
            api = api,
            tokenManager = tokenManager
        )

        val result = repository.loginUser("+79990000001", "123")

        assertEquals("1", result.user?.id)
        assertEquals(LoginReq("+79990000001", "123"), api.loginReq)

        verify(tokenManager).saveTokens("access", "refresh")
    }

    @Test
    fun registerUserReturnsResponse() = runBlocking {
        val api = FakeAuthApi(
            registerResp = AuthResp(
                user = UserDto(id = "2", role = "USER"),
                accessToken = "access",
                refreshToken = "refresh"
            )
        )

        val tokenManager = mock(TokenManager::class.java)

        val repository = AuthRepository(
            context = mock(Context::class.java),
            api = api,
            tokenManager = tokenManager
        )

        val result = repository.registerUser(
            "Иван",
            "Петров",
            "+79990000002",
            "123"
        )

        assertEquals("2", result.user?.id)
        assertEquals(
            RegisterReq("Иван", "Петров", "+79990000002", "123"),
            api.registerReq
        )

        verify(tokenManager).saveTokens("access", "refresh")
    }

    @Test
    fun recoverPasswordReturnsTrueForSuccess() = runBlocking {
        val api = FakeAuthApi(recoverResp = Response.success(Unit))

        val repository = AuthRepository(
            context = mock(Context::class.java),
            api = api,
            tokenManager = mock(TokenManager::class.java)
        )

        val result = repository.recoverPassword(
            "+79990000001",
            "Иван",
            "Петров",
            "123"
        )

        assertEquals(true, result)
        assertEquals(
            RecoverPasswordReq("+79990000001", "Иван", "Петров", "123"),
            api.recoverReq
        )
    }

    @Test
    fun recoverPasswordThrowsForError() {
        val api = FakeAuthApi(recoverResp = errorResponse())

        val repository = AuthRepository(
            context = mock(Context::class.java),
            api = api,
            tokenManager = mock(TokenManager::class.java)
        )

        assertThrows(HttpException::class.java) {
            runBlocking {
                repository.recoverPassword(
                    "+79990000001",
                    "Иван",
                    "Петров",
                    "123"
                )
            }
        }
    }

    private class FakeAuthApi(
        private val loginResp: AuthResp = AuthResp(),
        private val registerResp: AuthResp = AuthResp(),
        private val recoverResp: Response<Unit> = Response.success(Unit)
    ) : AuthApi {

        var loginReq: LoginReq? = null
        var registerReq: RegisterReq? = null
        var recoverReq: RecoverPasswordReq? = null

        override suspend fun login(req: LoginReq): AuthResp {
            loginReq = req
            return loginResp
        }

        override suspend fun register(req: RegisterReq): AuthResp {
            registerReq = req
            return registerResp
        }

        override suspend fun recoverPassword(req: RecoverPasswordReq): Response<Unit> {
            recoverReq = req
            return recoverResp
        }
    }

    private fun <T> errorResponse(): Response<T> {
        return Response.error(400, "{}".toResponseBody(null))
    }
}