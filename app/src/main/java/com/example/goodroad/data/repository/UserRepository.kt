package com.example.goodroad.data.repository

import com.example.goodroad.data.network.UserApi
import com.example.goodroad.ui.user.*

class UserRepository(private val api: UserApi) {

    suspend fun getCurrentUser(): SettingsView? {
        val response = api.getCurrentUser()
        if (response.isSuccessful) return response.body()
        throw Exception("Ошибка загрузки пользователя: ${response.code()} ${response.message()}")
    }

    suspend fun updateCurrentUser(req: UpdateSettingsReq): SettingsView? {
        val response = api.updateCurrentUser(req)
        if (response.isSuccessful) return response.body()
        throw Exception("Ошибка обновления пользователя: ${response.code()} ${response.message()}")
    }

    suspend fun changePassword(oldPassword: String, newPassword: String) {
        val response = api.changePassword(ChangePasswordReq(oldPassword, newPassword))
        if (!response.isSuccessful) throw Exception("Ошибка смены пароля: ${response.code()} ${response.message()}")
    }

    suspend fun deleteCurrentUser(req: DeleteAccountReq) {
        val response = api.deleteCurrentUser(req)
        if (!response.isSuccessful) throw Exception("Ошибка удаления аккаунта: ${response.code()} ${response.message()}")
    }
}