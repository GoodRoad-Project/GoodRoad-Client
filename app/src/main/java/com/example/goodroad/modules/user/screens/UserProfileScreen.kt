package com.example.goodroad.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onSelectObstacles: () -> Unit,
    onBecomeVolunteer: () -> Unit = {}
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

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    item {
                        UserDecor()

                        Text(
                            text = "Профиль",
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextPrimary
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = UrbanBrown.copy(alpha = 0.12f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column(modifier = Modifier.weight(1f)) {
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
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "👤",
                                                fontSize = 32.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        SectionTitle("Доступность и комфорт")
                    }

                    item {
                        ServiceCard(
                            title = "Выбрать препятствия",
                            description = "Настройте условия передвижения и доступности",
                            onClick = onSelectObstacles
                        )
                    }

                    item {
                        ServiceCard(
                            title = "Стать волонтёром",
                            description = "Подключитесь к помощи другим пользователям",
                            onClick = onBecomeVolunteer
                        )
                    }

                    item {
                        SectionTitle("Аккаунт")
                    }

                    item {
                        ServiceCard(
                            title = "Редактировать профиль",
                            description = "Изменить личные данные",
                            onClick = onEdit
                        )
                    }

                    item {
                        ServiceCard(
                            title = "Удалить аккаунт",
                            description = "Безвозвратное удаление профиля",
                            onClick = onDelete
                        )
                    }

                    item {
                        ServiceCard(
                            title = "Выйти из аккаунта",
                            description = "Завершить текущую сессию",
                            onClick = { userViewModel.logout { onLogout() } }
                        )
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

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = UrbanBrown
    )
}

@Composable
private fun ServiceCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWarm
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}