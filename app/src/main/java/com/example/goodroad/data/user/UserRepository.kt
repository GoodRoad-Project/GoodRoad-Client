package com.example.goodroad.data.user

import com.example.goodroad.data.user.UserApi
import com.example.goodroad.data.user.DeleteAccountReq
import com.example.goodroad.data.user.SettingsView
import com.example.goodroad.data.user.UpdateUserReq

class UserRepository(private val api: UserApi) {

    suspend fun getCurrentUser(): SettingsView? {
        val response = api.getCurrentUser()
        if (response.isSuccessful) return response.body()
        throw Exception("Ошибка загрузки пользователя: ${response.code()} ${response.message()}")
    }

    suspend fun updateCurrentUser(req: UpdateUserReq): SettingsView? {
        val response = api.updateCurrentUser(req)
        if (response.isSuccessful) return response.body()
        throw Exception("Ошибка обновления пользователя: ${response.code()} ${response.message()}")
    }

    suspend fun deleteCurrentUser(req: DeleteAccountReq) {
        val response = api.deleteCurrentUser(req)
        if (!response.isSuccessful) throw Exception("Ошибка удаления аккаунта: ${response.code()} ${response.message()}")
    }
}