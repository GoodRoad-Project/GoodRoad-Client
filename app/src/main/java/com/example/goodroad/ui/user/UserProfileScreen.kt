package com.example.goodroad.ui.user

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
import com.example.goodroad.ui.theme.*
import com.example.goodroad.ui.viewmodel.UserViewModel
import android.content.Intent
import com.example.goodroad.MapActivity
import androidx.compose.ui.platform.LocalContext

@Composable
fun UserProfileScreen(
    userViewModel: UserViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onLogout: () -> Unit,
    onSelectObstacles: () -> Unit,
    onOpenReviews: () -> Unit
) {
    val user by userViewModel.user
    val isLoading by userViewModel.isLoading
    val errorMessage by userViewModel.errorMessage

    LaunchedEffect(userViewModel) {
        if (user == null && !userViewModel.isDeleted) {
            userViewModel.getCurrentUser()
        }
    }

    when {
        isLoading && user == null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
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
                        "Профиль",
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
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
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

                    Spacer(Modifier.height(20.dp))

                    AuthButton(
                        text = "Выбрать препятствия",
                        backgroundColor = UrbanBrown,
                        contentColor = WhiteSoft
                    ) {
                        onSelectObstacles()
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val context = LocalContext.current

                    AuthButton(
                        text = "Перейти на карту",
                        backgroundColor = UrbanBrown,
                        contentColor = WhiteSoft
                    ) {
                        val intent = Intent(context, MapActivity::class.java)
                        context.startActivity(intent)
                    }

                    Spacer(Modifier.height(10.dp))

                    AuthButton(
                        text = "Мои отзывы",
                        backgroundColor = SafeGreen,
                        contentColor = WhiteSoft
                    ) {
                        onOpenReviews()
                    }

                    Spacer(Modifier.height(10.dp))

                    AuthButton(text = "Редактировать профиль", onClick = onEdit)

                    Spacer(Modifier.height(10.dp))

                    AuthButton(text = "Удалить аккаунт", onClick = onDelete)

                    Spacer(Modifier.height(10.dp))

                    AuthButton(text = "Выйти") {
                        userViewModel.logout { onLogout() }
                    }
                }
            }
        }

        errorMessage != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ошибка: $errorMessage", color = Color.Red)
            }
        }

        else -> {
            LaunchedEffect(Unit) {
                if (userViewModel.isDeleted) onLogout()
            }
        }
    }
}