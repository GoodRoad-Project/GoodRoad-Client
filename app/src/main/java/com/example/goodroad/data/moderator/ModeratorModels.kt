package com.example.goodroad.data.moderator

data class ModeratorView(
    val id: String,
    val role: String,
    val firstName: String?,
    val lastName: String?,
    val photoUrl: String?,
    val active: Boolean
)

data class CreateModeratorReq(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val password: String
)