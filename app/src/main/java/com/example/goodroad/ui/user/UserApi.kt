package com.example.goodroad.ui.user

data class UserDto(
    val id: String,
    val role: String,
    val firstName: String?,
    val lastName: String?,
    val photoUrl: String?,
    val active: Boolean
)

data class UpdateSettingsReq(
    val firstName: String? = null,
    val lastName: String? = null,
    val photoUrl: String? = null,
    val phone: String? = null
)

data class SettingsView(
    val id: String,
    val role: String,
    val firstName: String?,
    val lastName: String?,
    val photoUrl: String?,
    val active: Boolean
)

data class ChangePasswordReq(
    val oldPassword: String,
    val newPassword: String
)

data class DeleteAccountReq(
    val password: String
)