package com.example.goodroad.ui.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.theme.WhiteSoft
import com.example.goodroad.modules.user.presentation.UserViewModel

@Composable
fun UserProfileScreen(
    userViewModel: UserViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onLogout: () -> Unit,
    onSelectObstacles: () -> Unit
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

        userViewModel.isDeleted -> {
            LaunchedEffect(Unit) {
                onLogout()
            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        errorMessage != null && user == null -> {
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
                        text = "Профиль",
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
                                text = "${u.firstName ?: ""} ${u.lastName ?: ""}".trim(),
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

                    PrimaryButton(
                        text = "Выбрать препятствия",
                        backgroundColor = UrbanBrown,
                        contentColor = WhiteSoft
                    ) {
                        onSelectObstacles()
                    }

                    Spacer(Modifier.height(10.dp))

                    PrimaryButton(
                        text = "Редактировать профиль",
                        onClick = onEdit
                    )

                    Spacer(Modifier.height(10.dp))

                    PrimaryButton(
                        text = "Удалить аккаунт",
                        onClick = onDelete
                    )

                    Spacer(Modifier.height(10.dp))

                    PrimaryButton(
                        text = "Выйти из аккаунта"
                    ) {
                        userViewModel.logout { onLogout() }
                    }
                }
            }
        }

        else -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}