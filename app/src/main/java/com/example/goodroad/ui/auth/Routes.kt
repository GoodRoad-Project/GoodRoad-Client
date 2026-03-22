package com.example.goodroad.ui.auth

const val LOGIN_ROUTE = "login"
const val REGISTER_ROUTE = "register"
const val RECOVER_ROUTE = "recover"
const val USER_HOME_ROUTE = "user_home"
const val MODERATOR_HOME_ROUTE = "moderator_home"

fun homeRoute(role: String): String {
    return if (role.startsWith("MODERATOR")) {
        MODERATOR_HOME_ROUTE
    } else {
        USER_HOME_ROUTE
    }
}