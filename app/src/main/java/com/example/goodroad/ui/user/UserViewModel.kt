package com.example.goodroad.ui.user

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {

    var user = mutableStateOf<UserDto?>(null)
        private set

    init {
        user.value = UserDto(
            id = "1",
            role = "USER",
            firstName = "Мария-Антуанетта",
            lastName = "Австрийская",
            photoUrl = null,
            active = true
        )
    }

    fun update(firstName: String, lastName: String) {
        user.value = user.value?.copy(
            firstName = firstName,
            lastName = lastName
        )
    }

    fun logout() {
        user.value = null
    }
}