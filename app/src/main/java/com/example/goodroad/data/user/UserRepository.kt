package com.example.goodroad.data.user

import retrofit2.HttpException

class UserRepository(private val api: UserApi) {

    suspend fun getCurrentUser(): SettingsView? {
        val response = api.getCurrentUser()
        if (response.isSuccessful) return response.body()
        throw HttpException(response)
    }

    suspend fun updateCurrentUser(req: UpdateUserReq): SettingsView? {
        val response = api.updateCurrentUser(req)
        if (response.isSuccessful) return response.body()
        throw HttpException(response)
    }

    suspend fun changePassword(oldPassword: String, newPassword: String) {
        val response = api.changePassword(oldPassword, newPassword)
        if (!response.isSuccessful) throw HttpException(response)
    }

    suspend fun deleteCurrentUser(req: DeleteAccountReq) {
        val response = api.deleteCurrentUser(req)
        if (!response.isSuccessful) throw HttpException(response)
    }
}