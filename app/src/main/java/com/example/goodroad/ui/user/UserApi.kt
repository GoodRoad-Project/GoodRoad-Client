package com.example.goodroad.ui.user

data class UserDto(
    val id: String,
    val role: String,
    val firstName: String?,
    val lastName: String?,
    val photoUrl: String?,
    val active: Boolean
)

data class UpdateUserReq(
    val firstName: String?,
    val lastName: String?,
    val photoUrl: String?,
    val phone: String?
)

data class ChangePasswordReq(
    val oldPassword: String,
    val newPassword: String
)

data class DeleteAccountReq(
    val password: String
)