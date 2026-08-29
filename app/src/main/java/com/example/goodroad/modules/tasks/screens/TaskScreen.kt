package com.example.goodroad.modules.tasks.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.goodroad.modules.tasks.data.TaskViewDto
import com.example.goodroad.modules.tasks.presentation.TasksViewModel
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.AlertRed
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.SurfaceWarm
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown

@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    onTaskClick: (TaskViewDto) -> Unit,
    onBack: () -> Unit,
    onHistoryClick: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var selectedType by remember { mutableStateOf("REVIEW") }

    LaunchedEffect(selectedType) {
        viewModel.loadTasks(activityType = selectedType)
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

                IconButton(onClick = onHistoryClick) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "История заданий",
                        tint = UrbanBrown.copy(alpha = 0.7f)
                    )
                }

                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = UrbanBrown.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Выполняйте задания и получайте баллы!",
                fontSize = 18.sp,
                color = UrbanBrown,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedType == "REVIEW",
                    onClick = {
                        if (selectedType != "REVIEW") {
                            selectedType = "REVIEW"
                        }
                    },
                    label = {
                        Text(
                            text = "Отзывы",
                            fontSize = 16.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = UrbanBrown.copy(alpha = 0.15f),
                        selectedLabelColor = UrbanBrown.copy(alpha = 0.8f),
                        containerColor = Color.Transparent,
                        labelColor = UrbanBrown.copy(alpha = 0.6f)
                    )
                )

                FilterChip(
                    selected = selectedType == "VOLUNTEER",
                    onClick = {
                        if (selectedType != "VOLUNTEER") {
                            selectedType = "VOLUNTEER"
                        }
                    },
                    label = {
                        Text(
                            text = "Волонтёрство",
                            fontSize = 16.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = UrbanBrown.copy(alpha = 0.15f),
                        selectedLabelColor = UrbanBrown.copy(alpha = 0.8f),
                        containerColor = Color.Transparent,
                        labelColor = UrbanBrown.copy(alpha = 0.6f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                loading -> {
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "❌",
                                fontSize = 48.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = error ?: "Ошибка загрузки",
                                color = AlertRed
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    viewModel.loadTasks(
                                        activityType = selectedType
                                    )
                                }
                            ) {
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📋",
                                fontSize = 48.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (selectedType == "REVIEW") {
                                    "Нет доступных заданий на отзывы"
                                } else {
                                    "Нет доступных волонтёрских заданий"
                                },
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
                        items(
                            items = tasks,
                            key = { task -> task.id }
                        ) { task ->

                            TaskCard(
                                task = task,
                                isReview = selectedType == "REVIEW",
                                onClick = {
                                    onTaskClick(task)
                                }
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
    isReview: Boolean,
    onClick: () -> Unit
) {
    val isCompleted = task.completedCount >= task.targetCount

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = isReview && !isCompleted
            ) {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                SurfaceWarm.copy(alpha = 0.5f)
            } else {
                SurfaceWarm
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
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
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) {
                            UrbanBrown.copy(alpha = 0.6f)
                        } else {
                            TextPrimary
                        }
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
                        fontSize = 17.sp,
                        color = UrbanBrown
                    )

                    Text(
                        text = "•",
                        fontSize = 17.sp,
                        color = UrbanBrown
                    )

                    Text(
                        text = "Прогресс: ${task.completedCount}/${task.targetCount}",
                        fontSize = 17.sp,
                        color = UrbanBrown
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (!isCompleted) {

                if (isReview) {

                    PrimaryButton(
                        text = "Выполнить",
                        backgroundColor = SafeGreen,
                        modifier = Modifier.width(100.dp),
                        onClick = onClick
                    )

                } else {

                    PrimaryButton(
                        text = "Выполнено",
                        backgroundColor = SafeGreen,
                        modifier = Modifier.width(110.dp),
                        onClick = {


                        }
                    )
                }
            }
        }
    }
}