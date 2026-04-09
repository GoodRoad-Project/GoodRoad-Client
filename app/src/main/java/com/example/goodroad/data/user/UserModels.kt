package com.example.goodroad.data.user

class UserModels {
}

data class UserDto(
    val id: String,
    val role: String,
    val firstName: String?,
    val lastName: String?,
    val photoUrl: String?,
    val phone: String?,
    val active: Boolean
)

data class UpdateUserReq(
    val firstName: String? = null,
    val lastName: String? = null,
    val photoUrl: String? = null,
    val phone: String? = null,
    val oldPassword: String? = null,
    val newPassword: String? = null
)

data class SettingsView(
    val id: String,
    val role: String,
    val firstName: String?,
    val lastName: String?,
    val photoUrl: String?,
    val phone: String?,
    val active: Boolean
)

data class DeleteAccountReq(
    val password: String
)