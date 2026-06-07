package com.example.goodroad.modules.rewards.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.goodroad.modules.rewards.presentation.RewardsViewModel
import com.example.goodroad.modules.rewards.data.RewardOffer
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.theme.SurfaceWarm
import com.example.goodroad.ui.theme.SafeGreen

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

    LaunchedEffect(Unit) {
        viewModel.loadAccount()
        viewModel.loadRewards()
    }

    LaunchedEffect(state.purchaseResult) {
        if (state.purchaseResult != null) {
            viewModel.loadAccount()
            viewModel.loadRewards()
        }
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
                            tint = UrbanBrown
                        )
                    }
                    IconButton(onClick = onNavigateToLeaderboard) {
                        Icon(
                            imageVector = Icons.Default.Leaderboard,
                            contentDescription = "Лидеры",
                            tint = UrbanBrown
                        )
                    }
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = UrbanBrown
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (account != null) {
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Мои баллы",
                                fontSize = 14.sp,
                                color = UrbanBrown.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${account.balance}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = UrbanBrown
                            )
                            Text(
                                text = account.title,
                                fontSize = 14.sp,
                                color = UrbanBrown.copy(alpha = 0.6f)
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
                    label = { Text("По возрастанию") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedSort == "price_desc",
                    onClick = {
                        selectedSort = "price_desc"
                        viewModel.loadRewards(sort = "price_desc")
                    },
                    label = { Text("По убыванию") },
                    modifier = Modifier.weight(1f)
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
                                color = UrbanBrown.copy(alpha = 0.6f)
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
                                onClick = { onRewardClick(reward) },
                                onBuyClick = { viewModel.purchaseReward(reward.id) },
                                canBuy = account?.balance ?: 0 >= reward.price,
                                isPurchasing = loading
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardCard(
    reward: RewardOffer,
    onClick: () -> Unit,
    onBuyClick: () -> Unit,
    canBuy: Boolean,
    isPurchasing: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reward.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reward.partnerName,
                    fontSize = 12.sp,
                    color = UrbanBrown
                )
                if (!reward.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reward.description,
                        fontSize = 14.sp,
                        color = Color(0xFF555555),
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        text = "${reward.price}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2F2B28)
                    )
                }

                Button(
                    onClick = onBuyClick,
                    enabled = canBuy && !isPurchasing,
                    modifier = Modifier.width(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canBuy) SafeGreen else UrbanBrown.copy(alpha = 0.5f)
                    )
                ) {
                    if (isPurchasing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    } else {
                        Text(
                            text = if (canBuy) "Купить" else "Не хватает",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}