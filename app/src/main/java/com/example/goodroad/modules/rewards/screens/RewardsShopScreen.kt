package com.example.goodroad.modules.rewards.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.goodroad.modules.rewards.presentation.RewardsViewModel
import com.example.goodroad.modules.rewards.data.RewardOffer
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.SafeRoute
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.theme.SurfaceWarm
import com.example.goodroad.ui.theme.WhiteSoft
import kotlinx.coroutines.delay

@Composable
fun RewardsShopScreen(
    viewModel: RewardsViewModel,
    onRewardClick: (RewardOffer) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val account = state.account
    val rewards = state.rewards
    val loading = state.loading
    val error = state.error

    var showStatusPopup by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadAccount()
        viewModel.loadRewards()
    }

    LaunchedEffect(state.purchaseResult) {
        if (state.purchaseResult != null) {
            viewModel.loadAccount()
            viewModel.loadRewards()
            delay(500)
            viewModel.clearPurchaseResult()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
                        text = "Награды и баллы",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    Row {
                        IconButton(onClick = onNavigateToHistory) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "История",
                                tint = UrbanBrown.copy(alpha = 0.7f)
                            )
                        }
                        IconButton(onClick = onNavigateToLeaderboard) {
                            Icon(
                                imageVector = Icons.Default.Leaderboard,
                                contentDescription = "Лидеры",
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
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (account != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = TextPrimary.copy(alpha = 0.08f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Мои баллы",
                                    fontSize = 18.sp,
                                    color = UrbanBrown.copy(alpha = 1.5f)
                                )
                                Text(
                                    text = "${account.balance}",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UrbanBrown.copy(alpha = 1.7f)
                                )
                                Text(
                                    text = account.title,
                                    fontSize = 16.sp,
                                    color = UrbanBrown.copy(alpha = 1.5f),
                                    modifier = Modifier.clickable {
                                        showStatusPopup = true
                                    }
                                )
                            }
                            Text(
                                text = "⭐",
                                fontSize = 48.sp
                            )
                        }
                    }
                } else if (loading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                var selectedSort by remember { mutableStateOf("price_asc") }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedSort == "price_asc",
                        onClick = {
                            selectedSort = "price_asc"
                            viewModel.loadRewards(sort = "price_asc")
                        },
                        label = {
                            Text(
                                text = "По возрастанию",
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
                        selected = selectedSort == "price_desc",
                        onClick = {
                            selectedSort = "price_desc"
                            viewModel.loadRewards(sort = "price_desc")
                        },
                        label = {
                            Text(
                                text = "По убыванию",
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
                    loading && rewards.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    error != null && rewards.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("❌", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { viewModel.loadRewards() }) {
                                    Text("Повторить")
                                }
                            }
                        }
                    }

                    rewards.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎁", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Пока нет доступных наград",
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Загляните позже!",
                                    fontSize = 12.sp,
                                    color = UrbanBrown.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(rewards) { reward ->
                                RewardCard(
                                    reward = reward,
                                    onClick = { onRewardClick(reward) }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showStatusPopup && account != null) {
            StatusPopup(
                status = account.title,
                onDismiss = { showStatusPopup = false }
            )
        }
    }
}

@Composable
private fun StatusPopup(
    status: String,
    onDismiss: () -> Unit
) {
    val allStatuses = listOf(
        "Новичок GoodRoad" to "0 - 99 ⭐",
        "Разведчик тротуаров" to "100 - 499 ⭐",
        "Проводник добра" to "500 - 999 ⭐",
        "Герой района" to "1000 - 1499 ⭐",
        "Хранитель маршрутов" to "1500 - 1999 ⭐",
        "Навигатор перемен" to "2000 - 2499 ⭐",
        "Мастер доступного города" to "2500 - 2999 ⭐",
        "Легенда добрых маршрутов" to "3000+ ⭐"
    )

    Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = SafeRoute.copy(alpha = 0.92f)            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                allStatuses.forEach { (statusName, range) ->
                    val isCurrent = statusName == status

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .then(
                                if (isCurrent) {
                                    Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(WhiteSoft.copy(alpha = 0.15f))
                                        .padding(8.dp)
                                } else Modifier
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = statusName,
                            fontSize = if (isCurrent) 17.sp else 15.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isCurrent) WhiteSoft else WhiteSoft.copy(alpha = 0.8f)
                        )
                        Text(
                            text = range,
                            fontSize = if (isCurrent) 17.sp else 15.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isCurrent) WhiteSoft else WhiteSoft.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun RewardCard(
    reward: RewardOffer,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWarm
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Text(
                    text = reward.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = reward.partnerName,
                    fontSize = 16.sp,
                    color = UrbanBrown.copy(alpha = 1.5f)
                )
                if (!reward.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = reward.description,
                        fontSize = 15.sp,
                        color = UrbanBrown.copy(alpha = 1.3f),
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "⭐",
                    fontSize = 28.sp
                )
                Text(
                    text = "${reward.price}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = UrbanBrown.copy(alpha = 1.7f)
                )
            }
        }
    }
}