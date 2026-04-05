package com.example.goodroad.data.repository

import com.example.goodroad.data.network.UserApi
import com.example.goodroad.ui.user.ChangePasswordReq
import com.example.goodroad.ui.user.DeleteAccountReq
import com.example.goodroad.ui.user.SettingsView
import com.example.goodroad.ui.user.UpdateSettingsReq

class UserRepository(private val api: UserApi) {

    suspend fun getCurrentUser(): SettingsView? {
        return try {
            val response = api.getCurrentUser()
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Пользователь не найден")
            } else {
                throw Exception("Ошибка загрузки пользователя: ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateCurrentUser(req: UpdateSettingsReq): SettingsView? {
        return try {
            val response = api.updateCurrentUser(req)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Ошибка обновления пользователя")
            } else {
                throw Exception("Ошибка обновления пользователя: ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String) {
        try {
            val response = api.changePassword(ChangePasswordReq(oldPassword, newPassword))
            if (!response.isSuccessful) {
                throw Exception("Ошибка смены пароля: ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun deleteCurrentUser(req: DeleteAccountReq) {
        try {
            val response = api.deleteCurrentUser(req)
            if (!response.isSuccessful) {
                throw Exception("Ошибка удаления аккаунта: ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}