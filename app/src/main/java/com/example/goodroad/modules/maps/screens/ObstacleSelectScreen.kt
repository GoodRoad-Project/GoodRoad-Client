package com.example.goodroad.modules.maps.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.goodroad.data.obstacle.model.PolicyItem
import com.example.goodroad.ui.*
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.*
import com.example.goodroad.modules.maps.presentation.MapsViewModel

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
                    .padding(bottom = 140.dp)
                    .padding(24.dp)
            ) {
                Text(
                    text = "Выбор препятствий",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Выберите препятствия, которые хотите избегать. Препятсвия с номером, большим выбранного, будут избегаться",
                    style = MaterialTheme.typography.titleMedium,
                    color = UrbanBrown,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                                style = MaterialTheme.typography.titleMedium,
                                color = UrbanBrown
                            )
                        }

                        if (checked) {
                            Column(
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Text(
                                    text = "Максимальная допустимая тяжесть для Вас:",
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = UrbanBrown.copy(
                                        red = UrbanBrown.red * 0.7f,
                                        green = UrbanBrown.green * 0.5f,
                                        blue = UrbanBrown.blue * 0.5f
                                    ),
                                    fontSize = 16.sp
                                )

                                val severityDescriptions = when (obstacle.obstacleType) {
                                    "STAIRS" -> listOf(
                                        "1-3 ступеньки",
                                        "4-10 ступенек",
                                        "Более 10 ступенек"
                                    )
                                    "CURB" -> listOf(
                                        "Маленький бордюр",
                                        "Обычный бордюр",
                                        "Высокий бордюр"
                                    )
                                    "ROAD_SLOPE" -> listOf(
                                        "Незначительный подъём",
                                        "Заметный подъём",
                                        "Крутой подъём"
                                    )
                                    "POTHOLES" -> listOf(
                                        "Маленькая яма",
                                        "Обычная яма",
                                        "Большая яма"
                                    )
                                    "SAND", "GRAVEL" -> listOf(
                                        "Укатанный песок",
                                        "Немного рыхлый песок",
                                        "Сильно рыхлый песок"
                                    )
                                    else -> listOf(
                                        "1 — слабая",
                                        "2 — средняя",
                                        "3 — сильная"
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Column(
                                    modifier = Modifier.padding(start = 6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    (0..2).forEach { index ->
                                        val value = index + 1
                                        val description = severityDescriptions.getOrElse(index) { "$value" }
                                        val isSelected = severity == value

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    severityMap[obstacle.obstacleType] = value
                                                    mapsViewModel.clearMessages()
                                                }
                                                .background(
                                                    color = if (isSelected) SafeGreen.copy(alpha = 0.12f) else BackgroundLight,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) SafeGreen else BorderWarm.copy(alpha = 0.5f),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .padding(vertical = 10.dp, horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .background(
                                                        color = if (isSelected) SafeGreen else UrbanBrown.copy(alpha = 0.1f),
                                                        shape = RoundedCornerShape(16.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = value.toString(),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color.White else UrbanBrown
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Text(
                                                text = description,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = if (isSelected) SafeGreen else UrbanBrown,
                                                fontSize = 15.sp,
                                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                                            )
                                        }
                                    }
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
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(BackgroundLight)
                    .padding(16.dp)
            ) {
                PrimaryButton(
                    text = if (isSaving) "Сохраняем..." else "Сохранить",
                    enabled = !isSaving && !isLoading
                ) {
                    val items = ServerObstacleOptions.map { obstacle ->
                        val selected = selectedMap[obstacle.obstacleType] == true

                        PolicyItem(
                            obstacleType = obstacle.obstacleType,
                            selected = selected,
                            maxAllowedSeverity = if (selected) {
                                (severityMap[obstacle.obstacleType] ?: 1).toShort()
                            } else null
                        )
                    }

                    mapsViewModel.savePolicies(items) {
                        onSaved()
                    }
                }
            }
        }
    }
}