package com.example.goodroad.ui.users.moderators

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goodroad.ui.auth.AuthButton
import com.example.goodroad.ui.viewmodel.UserViewModel
import com.example.goodroad.ui.theme.*
import com.example.goodroad.ui.users.UserDecor

@Composable
fun AdminProfileScreen(
    userViewModel: UserViewModel,
    onModerators: () -> Unit,
    onReviews: () -> Unit,
    onLogout: () -> Unit
) {

    val safeOnModerators by rememberUpdatedState(onModerators)

    val user by userViewModel.user
    val isLoading by userViewModel.isLoading
    val errorMessage by userViewModel.errorMessage

    LaunchedEffect(userViewModel) {
        if (user == null && !userViewModel.isDeleted) {
            userViewModel.getCurrentUser()
        }
    }

    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        errorMessage != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ошибка: $errorMessage", color = Color.Red)
            }
        }

        user != null -> {
            val u = user!!

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = BackgroundLight
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {

                    UserDecor()

                    Text(
                        "Профиль главного модератора",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary
                    )

                    Spacer(Modifier.height(20.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = UrbanBrown.copy(alpha = 0.08f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                text = "${u.firstName ?: ""} ${u.lastName ?: ""}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = UrbanBrown
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = "Роль: ${u.role ?: ""}",
                                fontSize = 16.sp,
                                color = UrbanBrown.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(Modifier.height(30.dp))

                    AuthButton(
                        text = "Модераторы",
                        backgroundColor = UrbanBrown,
                        contentColor = WhiteSoft
                    ) {
                        Log.d("AdminProfile", "BUTTON CLICKED")
                        safeOnModerators()
                    }

                    Spacer(Modifier.height(12.dp))

                    AuthButton(
                        text = "Отзывы",
                        backgroundColor = UrbanBrown,
                        contentColor = WhiteSoft
                    ) {
                        onReviews()
                    }

                    Spacer(Modifier.height(20.dp))

                    AuthButton(text = "Выйти") {
                        userViewModel.logout { onLogout() }
                    }
                }
            }
        }

        else -> {
            LaunchedEffect(Unit) {
                if (userViewModel.isDeleted) onLogout()
            }
        }
    }
}