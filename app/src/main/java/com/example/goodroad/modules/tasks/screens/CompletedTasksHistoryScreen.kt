package com.example.goodroad.modules.tasks.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.goodroad.modules.tasks.data.CompletedTaskDto
import com.example.goodroad.modules.tasks.presentation.TasksViewModel
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.theme.AlertRed
import com.example.goodroad.ui.theme.SurfaceWarm

@Composable
fun CompletedTasksHistoryScreen(
    viewModel: TasksViewModel,
    onBack: () -> Unit
) {
    val completedTasks by viewModel.completedTasks.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadCompletedTasks()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "История заданий",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = UrbanBrown
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ваши выполненные задания",
                fontSize = 18.sp,
                color = UrbanBrown,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                loading && completedTasks.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null && completedTasks.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("❌", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error ?: "Ошибка загрузки",
                                color = AlertRed
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadCompletedTasks() }) {
                                Text("Повторить")
                            }
                        }
                    }
                }

                completedTasks.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📭", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Нет выполненных заданий",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Text(
                                text = "Выполните первое задание, чтобы оно появилось здесь!",
                                fontSize = 14.sp,
                                color = UrbanBrown.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(completedTasks) { completedTask ->
                            CompletedTaskCard(
                                completedTask = completedTask
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompletedTaskCard(
    completedTask: CompletedTaskDto
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWarm
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Выполнено",
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(28.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = completedTask.taskTitle,
                        fontSize = 16.sp,
                        fontWeight =  FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "⭐ ${completedTask.pointsAwarded}",
                            fontSize = 17.sp,
                            color = UrbanBrown
                        )
                        Text(
                            text = "•",
                            fontSize = 17.sp,
                            color = UrbanBrown
                        )
                        Text(
                            text = formatDate(completedTask.createdAt),
                            fontSize = 17.sp,
                            color = UrbanBrown
                        )
                    }
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val instant = java.time.Instant.parse(dateString)
        val formatter = java.time.format.DateTimeFormatter
            .ofPattern("dd.MM.yyyy")
            .withZone(java.time.ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        dateString.take(10)
    }
}