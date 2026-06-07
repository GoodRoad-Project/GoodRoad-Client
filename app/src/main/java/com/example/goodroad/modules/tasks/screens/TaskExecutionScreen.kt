package com.example.goodroad.modules.tasks.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goodroad.modules.tasks.data.TaskViewDto
import com.example.goodroad.modules.tasks.data.TargetViewDto
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.theme.SurfaceWarm
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.AlertRed

@Composable
fun TaskExecutionScreen(
    task: TaskViewDto,
    onTargetComplete: (TargetViewDto) -> Unit,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    var completedTargets by remember { mutableStateOf(task.targets.filter { it.done }.size) }
    val allCompleted = completedTargets >= task.targetCount

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
                    text = task.title,
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

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "⭐",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${task.points}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = UrbanBrown
                    )
                    Text(
                        text = "баллов",
                        fontSize = 14.sp,
                        color = UrbanBrown.copy(alpha = 0.7f)
                    )
                }

                if (allCompleted) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = SafeGreen.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SafeGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Готово к завершению",
                                fontSize = 12.sp,
                                color = SafeGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = completedTargets.toFloat() / task.targetCount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = SafeGreen,
                trackColor = UrbanBrown.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Прогресс: $completedTargets из ${task.targetCount} целей",
                fontSize = 13.sp,
                color = UrbanBrown.copy(alpha = 1.1f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Цели задания",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(task.targets) { idx, target ->
                    TargetItem(
                        target = target,
                        index = idx,
                        isCompleted = target.done,
                        onComplete = {
                            if (!target.done) {
                                completedTargets++
                                onTargetComplete(target)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Завершить задание",
                backgroundColor = if (allCompleted) SafeGreen else UrbanBrown.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth(),
                enabled = allCompleted,
                onClick = onComplete
            )
        }
    }
}

@Composable
private fun TargetItem(
    target: TargetViewDto,
    index: Int,
    isCompleted: Boolean,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isCompleted) { onComplete() },
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) SurfaceWarm.copy(alpha = 0.5f) else SurfaceWarm
        )
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
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Выполнено",
                        tint = SafeGreen,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = MaterialTheme.shapes.small,
                        color = UrbanBrown.copy(alpha = 0.3f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 12.sp,
                                color = UrbanBrown
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = target.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isCompleted) UrbanBrown.copy(alpha = 0.6f) else TextPrimary
                    )

                    if (target.latitude != null && target.longitude != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = AlertRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Отметить на карте",
                                fontSize = 12.sp,
                                color = AlertRed
                            )
                        }
                    }
                }
            }

            if (!isCompleted) {
                PrimaryButton(
                    text = "Выполнить",
                    backgroundColor = SafeGreen,
                    modifier = Modifier.width(100.dp),
                    onClick = onComplete
                )
            }
        }
    }
}