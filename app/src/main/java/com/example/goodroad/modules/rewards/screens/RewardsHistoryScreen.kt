package com.example.goodroad.modules.rewards.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.goodroad.modules.rewards.data.PointTransaction
import com.example.goodroad.modules.rewards.presentation.RewardsViewModel
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.AlertRed

@Composable
fun RewardsHistoryScreen(
    viewModel: RewardsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val history = state.history
    val loading = state.loading
    val error = state.error

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
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
                    text = "История операций",
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

            when {
                loading && history.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null && history.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("❌", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error,
                                color = AlertRed
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadHistory() }) {
                                Text("Повторить")
                            }
                        }
                    }
                }

                history.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "История пуста",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Совершайте покупки и выполняйте задания,\nчтобы здесь появлялись операции",
                                fontSize = 16.sp,
                                color = UrbanBrown.copy(alpha = 0.7f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(history) { transaction ->
                            HistoryItem(transaction = transaction)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(transaction: PointTransaction) {
    val isEarn = transaction.type == "EARN"
    val sign = if (isEarn) "+" else "-"
    val color = if (isEarn) SafeGreen else AlertRed

    val displayText = when {
        !transaction.details.isNullOrBlank() -> transaction.details
        isEarn -> "Начисление баллов"
        else -> "Списание баллов"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = UrbanBrown.copy(alpha = 0.06f)
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
                Text(
                    text = displayText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDateTime(transaction.createdAt),
                    fontSize = 12.sp,
                    color = UrbanBrown.copy(alpha = 0.5f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$sign ${transaction.amount}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = "⭐",
                    fontSize = 16.sp
                )
            }
        }
    }
}

private fun formatDateTime(dateString: String): String {
    return try {
        val parts = dateString.split("T")
        val date = parts[0].split("-")
        val formattedDate = "${date[2]}.${date[1]}.${date[0]}"

        val time = parts[1].substring(0, 5)

        "$formattedDate $time"
    } catch (e: Exception) {
        dateString
    }
}