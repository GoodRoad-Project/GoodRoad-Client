package com.example.goodroad.modules.tasks.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.goodroad.modules.tasks.presentation.TasksViewModel
import com.example.goodroad.modules.tasks.data.TaskViewDto
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.theme.SurfaceWarm
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.AlertRed

@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    onTaskClick: (TaskViewDto) -> Unit,
    onBack: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadTasks()
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
                    text = "Задания",
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
                text = "Выполняйте задания и получайте баллы!",
                fontSize = 18.sp,
                color = UrbanBrown.copy(alpha = 1.1f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                loading && tasks.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null && tasks.isEmpty() -> {
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
                            Button(onClick = { viewModel.loadTasks() }) {
                                Text("Повторить")
                            }
                        }
                    }
                }

                tasks.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📋", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Нет доступных заданий",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Text(
                                text = "Загляните позже!",
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
                        items(tasks) { task ->
                            TaskCard(
                                task = task,
                                onClick = { onTaskClick(task) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: TaskViewDto,
    onClick: () -> Unit
) {
    val isCompleted = task.completedCount >= task.targetCount
    val isInProgress = task.completedCount > 0 && !isCompleted

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isCompleted) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) SurfaceWarm.copy(alpha = 0.5f) else SurfaceWarm
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = task.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) UrbanBrown.copy(alpha = 0.6f) else TextPrimary
                    )

                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Выполнено",
                            tint = SafeGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⭐ ${task.points}",
                        fontSize = 14.sp,
                        color = UrbanBrown
                    )
                    Text(
                        text = "•",
                        fontSize = 14.sp,
                        color = UrbanBrown.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Прогресс: ${task.completedCount}/${task.targetCount}",
                        fontSize = 14.sp,
                        color = UrbanBrown.copy(alpha = 0.7f)
                    )
                }
            }

            if (!isCompleted) {
                PrimaryButton(
                    text = if (isInProgress) "Продолжить" else "Начать",
                    backgroundColor = SafeGreen,
                    modifier = Modifier.width(100.dp),
                    onClick = onClick
                )
            }
        }
    }
}