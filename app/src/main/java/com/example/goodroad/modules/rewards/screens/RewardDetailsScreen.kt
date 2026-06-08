package com.example.goodroad.modules.rewards.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.goodroad.modules.rewards.data.RewardOffer
import com.example.goodroad.modules.rewards.presentation.RewardsViewModel
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.AlertRed

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

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = UrbanBrown.copy(alpha = 0.08f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Партнёр",
                        fontSize = 12.sp,
                        color = UrbanBrown.copy(alpha = 0.6f)
                    )
                    Text(
                        text = reward.partnerName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Описание",
                        fontSize = 12.sp,
                        color = UrbanBrown.copy(alpha = 0.6f)
                    )
                    Text(
                        text = reward.description ?: "Нет описания",
                        fontSize = 15.sp,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Стоимость",
                        fontSize = 12.sp,
                        color = UrbanBrown.copy(alpha = 0.6f)
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
                            text = "${reward.price}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = UrbanBrown
                        )
                        Text(
                            text = "баллов",
                            fontSize = 14.sp,
                            color = UrbanBrown.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (account != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SafeGreen.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ваш баланс:",
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "⭐",
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${account.balance}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = UrbanBrown
                            )
                            Text(
                                text = "баллов",
                                fontSize = 12.sp,
                                color = UrbanBrown.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val canBuy = account.balance >= reward.price

                if (isPurchasing) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(52.dp),
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
                        backgroundColor = if (canBuy) UrbanBrown else UrbanBrown.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = canBuy && !isPurchasing,
                        onClick = { showConfirmation = true }
                    )
                }
            }

            if (state.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.error ?: "Ошибка",
                    color = AlertRed,
                    fontSize = 13.sp,
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
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Вы уверены, что хотите купить \"${reward.title}\" за ${reward.price} баллов?"
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
                    Text("Купить", color = SafeGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) {
                    Text("Отмена", color = UrbanBrown)
                }
            }
        )
    }
}