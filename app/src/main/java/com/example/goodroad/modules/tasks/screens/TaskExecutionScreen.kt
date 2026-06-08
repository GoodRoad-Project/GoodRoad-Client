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
    var targetsState by remember { mutableStateOf(task.targets.toList()) }
    val completedTargets = targetsState.count { it.done }
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
                    fontSize = 28.sp,
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                    lineHeight = 31.sp
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
                        text = "Задание на ${task.points}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = UrbanBrown
                    )
                    Text(
                        text = "⭐",
                        fontSize = 16.sp
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
                fontSize = 16.sp,
                color = UrbanBrown
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Цели задания",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(targetsState) { idx, target ->
                    TargetItem(
                        target = target,
                        index = idx,
                        isCompleted = target.done,
                        onComplete = {
                            if (!target.done) {
                                val updatedTarget = target.copy(done = true)
                                targetsState = targetsState.mapIndexed { index, t ->
                                    if (index == idx) updatedTarget else t
                                }
                                onTargetComplete(updatedTarget)
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
            .clickable(enabled = !isCompleted) { if (!isCompleted) onComplete() },
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWarm
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
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Text(
                        text = "${index + 1}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = UrbanBrown
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = target.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isCompleted) UrbanBrown.copy(alpha = 0.6f) else TextPrimary,
                        lineHeight = 26.sp
                    )

                    if (target.latitude != null && target.longitude != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = AlertRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Отметить на карте",
                                fontSize = 14.sp,
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
                    onClick = {
                        if (!isCompleted) onComplete()
                    }
                )
            }
        }
    }
}