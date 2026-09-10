package com.example.goodroad.modules.user.data

data class UpdateUserReq(
    val firstName: String? = null,
    val lastName: String? = null,
    val photoUrl: String? = null
)

data class ChangePhoneReq(
    val phone: String,
    val currentPassword: String
)

data class AvatarUploadResp(
    val photoUrl: String
)

data class SettingsView(
    val id: String,
    val role: String,
    val firstName: String?,
    val lastName: String?,
    val photoUrl: String?,
    val active: Boolean,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val tokenType: String? = null
)

data class DeleteAccountReq(
    val password: String
)