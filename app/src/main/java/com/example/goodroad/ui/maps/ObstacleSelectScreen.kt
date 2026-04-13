package com.example.goodroad.ui.maps

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.example.goodroad.data.obstacle.*
import com.example.goodroad.ui.auth.*
import com.example.goodroad.ui.theme.*
import com.example.goodroad.ui.user.*
import com.example.goodroad.ui.viewmodel.*

data class ObstacleOption(
    val obstacleType: String,
    val title: String
)

private val ServerObstacleOptions = listOf(
    ObstacleOption("CURB", "Бордюры"),
    ObstacleOption("STAIRS", "Лестницы"),
    ObstacleOption("ROAD_SLOPE", "Наклон дороги"),
    ObstacleOption("POTHOLES", "Ямы"),
    ObstacleOption("SAND", "Песок"),
    ObstacleOption("GRAVEL", "Гравий")
)

@Composable
fun ObstacleSelectScreen(
    mapsViewModel: MapsViewModel,
    onBackToProfile: () -> Unit,
    onSaved: () -> Unit
) {
    val policies by mapsViewModel.policies
    val isLoading by mapsViewModel.isLoading
    val isSaving by mapsViewModel.isSaving
    val errorMessage by mapsViewModel.errorMessage
    val successMessage by mapsViewModel.successMessage

    val selectedMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            ServerObstacleOptions.forEach { put(it.obstacleType, false) }
        }
    }

    val severityMap = remember {
        mutableStateMapOf<String, Int>().apply {
            ServerObstacleOptions.forEach { put(it.obstacleType, 1) }
        }
    }

    LaunchedEffect(Unit) {
        mapsViewModel.loadPolicies()
    }

    LaunchedEffect(policies) {
        if (policies.isNotEmpty()) {
            policies.forEach { item ->
                selectedMap[item.obstacleType] = item.selected
                severityMap[item.obstacleType] = item.maxAllowedSeverity?.toInt() ?: 1
            }
        }
    }

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                UserDecor()

                Text(
                    text = "Выбор препятствий",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Отметьте препятствия, которых нужно избегать, и укажите максимальную допустимую тяжесть.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = UrbanBrown
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "1 — слабая тяжесть, 2 — средняя тяжесть, 3 — сильная тяжесть.",
                    style = MaterialTheme.typography.bodySmall,
                    color = UrbanBrown
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading && policies.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SafeGreen)
                    }
                }

                ServerObstacleOptions.forEach { obstacle ->
                    val checked = selectedMap[obstacle.obstacleType] == true
                    val severity = severityMap[obstacle.obstacleType] ?: 1

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { isChecked ->
                                    selectedMap[obstacle.obstacleType] = isChecked
                                    mapsViewModel.clearMessages()
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = UrbanBrown,
                                    uncheckedColor = UrbanBrown,
                                    checkmarkColor = WhiteSoft
                                )
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = obstacle.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = UrbanBrown
                            )
                        }

                        if (checked) {
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Максимальная допустимая тяжесть",
                                style = MaterialTheme.typography.bodyMedium,
                                color = UrbanBrown
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                (1..3).forEach { value ->
                                    FilterChip(
                                        selected = severity == value,
                                        onClick = {
                                            severityMap[obstacle.obstacleType] = value
                                            mapsViewModel.clearMessages()
                                        },
                                        label = {
                                            Text(
                                                text = value.toString(),
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = SafeGreen.copy(alpha = 0.18f),
                                            selectedLabelColor = SafeGreen,
                                            containerColor = BackgroundLight,
                                            labelColor = UrbanBrown
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = severity == value,
                                            borderColor = if (severity == value) SafeGreen else BorderWarm,
                                            selectedBorderColor = SafeGreen
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                AuthStatusText(
                    text = errorMessage,
                    onTimeout = mapsViewModel::clearMessages
                )
                AuthSuccessText(
                    text = successMessage,
                    onTimeout = mapsViewModel::clearMessages
                )

                Spacer(modifier = Modifier.height(30.dp))

                AuthButton(
                    text = if (isSaving) "Сохраняем..." else "Сохранить",
                    enabled = !isSaving && !isLoading
                ) {
                    val items = ServerObstacleOptions.map { obstacle ->
                        val selected = selectedMap[obstacle.obstacleType] == true
                        ObstaclePolicyItem(
                            obstacleType = obstacle.obstacleType,
                            selected = selected,
                            maxAllowedSeverity = if (selected) {
                                (severityMap[obstacle.obstacleType] ?: 1).toShort()
                            } else {
                                null
                            }
                        )
                    }

                    mapsViewModel.savePolicies(items) {
                        onSaved()
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                AuthButton(
                    text = "Назад в профиль",
                    backgroundColor = UrbanBrown,
                    contentColor = WhiteSoft,
                    enabled = !isSaving
                ) {
                    onBackToProfile()
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .align(Alignment.CenterEnd)
                    .background(UrbanBrown.copy(alpha = 0.25f))
            )

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(60.dp)
                    .offset(y = (scrollState.value * 0.2f).dp)
                    .align(Alignment.TopEnd)
                    .background(UrbanBrown)
            )
        }
    }
}
