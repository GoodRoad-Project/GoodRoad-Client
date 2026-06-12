package com.example.goodroad.modules.rewards.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.goodroad.modules.rewards.data.RewardOffer
import com.example.goodroad.modules.rewards.presentation.RewardsViewModel
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.AlertRed
import androidx.compose.ui.graphics.Color
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.TextPrimary

@Composable
fun RewardDetailScreen(
    viewModel: RewardsViewModel,
    reward: RewardOffer,
    onPurchaseComplete: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showConfirmation by remember { mutableStateOf(false) }
    var isPurchasing by remember { mutableStateOf(false) }

    val account = state.account

    LaunchedEffect(state.purchaseResult) {
        if (state.purchaseResult != null && isPurchasing) {
            isPurchasing = false
            onPurchaseComplete()
            viewModel.clearPurchaseResult()
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
                    text = reward.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
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

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = UrbanBrown.copy(alpha = 0.06f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "ПАРТНЁР",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = UrbanBrown.copy(alpha = 1.5f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reward.partnerName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = UrbanBrown
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(
                        color = UrbanBrown.copy(alpha = 0.1f),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "ОПИСАНИЕ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = UrbanBrown.copy(alpha = 1.5f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = reward.description ?: "Нет описания",
                        fontSize = 16.sp,
                        color = UrbanBrown.copy(alpha = 0.85f),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(
                        color = UrbanBrown.copy(alpha = 0.1f),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "СТОИМОСТЬ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = UrbanBrown.copy(alpha = 1.6f),
                            letterSpacing = 1.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "⭐",
                                fontSize = 28.sp
                            )
                            Text(
                                text = "${reward.price}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = UrbanBrown
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (account != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = SafeGreen.copy(alpha = 0.08f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ваш баланс",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = UrbanBrown
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "⭐",
                                fontSize = 20.sp
                            )
                            Text(
                                text = "${account.balance}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = UrbanBrown
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                val canBuy = account.balance >= reward.price

                if (isPurchasing) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = UrbanBrown
                        )
                    }
                } else {
                    PrimaryButton(
                        text = if (canBuy) "Купить за ${reward.price} баллов" else "Недостаточно баллов",
                        backgroundColor = if (canBuy) SafeGreen else UrbanBrown,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = canBuy,
                        onClick = { showConfirmation = true }
                    )
                }
            }

            if (state.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.error ?: "Ошибка",
                    color = AlertRed,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = {
                Text(
                    text = "Подтверждение покупки",
                    fontWeight = FontWeight.Bold,
                    color = UrbanBrown
                )
            },
            text = {
                Text(
                    text = "Вы уверены, что хотите купить \"${reward.title}\" за ${reward.price} баллов?",
                    color = UrbanBrown.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmation = false
                        isPurchasing = true
                        viewModel.purchaseReward(reward.id)
                    }
                ) {
                    Text("Купить", color = SafeGreen, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) {
                    Text("Отмена", color = UrbanBrown)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}