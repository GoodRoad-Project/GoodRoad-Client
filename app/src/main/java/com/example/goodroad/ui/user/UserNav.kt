package com.example.goodroad.ui.user

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UserNav(
    onLogout: () -> Unit
) {
    val vm: UserViewModel = viewModel()
    var screen by remember { mutableStateOf("profile") }

    when (screen) {
        "profile" -> UserProfileScreen(
            vm = vm,
            onEdit = { screen = "edit" },
            onDelete = { screen = "delete" },
            onLogout = onLogout
        )

        "edit" -> UserEditScreen(
            vm = vm,
            onBack = { screen = "profile" }
        )

        "delete" -> UserDeleteAccountScreen(
            onDelete = {
                vm.logout()
                onLogout()
            },
            onExit = {
                screen = "profile"
            }
        )
    }
}