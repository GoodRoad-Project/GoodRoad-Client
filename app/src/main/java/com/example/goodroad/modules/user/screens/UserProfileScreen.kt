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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.goodroad.modules.user.presentation.UserViewModel
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.*

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
            LaunchedEffect(Unit) { onLogout() }
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
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = UrbanBrown.copy(alpha = 0.08f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
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

                            if (!u.photoUrl.isNullOrBlank()) {

                                AsyncImage(
                                    model = u.photoUrl,
                                    contentDescription = "Фото профиля",
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                            } else {

                                Surface(
                                    modifier = Modifier.size(90.dp),
                                    shape = CircleShape,
                                    color = WhiteSoft
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "👤",
                                            fontSize = 32.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        PrimaryButton(
                            text = "Выбрать препятствия",
                            backgroundColor = UrbanBrown,
                            contentColor = WhiteSoft
                        ) {
                            onSelectObstacles()
                        }

                        PrimaryButton(
                            text = "Редактировать профиль",
                            onClick = onEdit
                        )

                        PrimaryButton(
                            text = "Удалить аккаунт",
                            onClick = onDelete
                        )

                        PrimaryButton(
                            text = "Выйти из аккаунта"
                        ) {
                            userViewModel.logout { onLogout() }
                        }
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