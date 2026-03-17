package com.example.goodroad.data.network

data class LoginReq(
    val phone: String,
    val password: String
)

data class RegisterReq(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val password: String
)

data class AuthResp(
    val user: UserDto? = null,
    val message: String? = null
)

data class UserDto(
    val id: Long? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val role: String? = null,
    val totalPoints: Int? = null
)

data class ApiErrorDto(
    val error: String? = null,
    val message: String? = null,
    val details: Map<String, String>? = null
)
