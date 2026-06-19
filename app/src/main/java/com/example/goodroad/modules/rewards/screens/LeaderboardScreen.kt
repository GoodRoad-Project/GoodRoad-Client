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
import com.example.goodroad.modules.rewards.data.LeaderboardItem
import com.example.goodroad.modules.rewards.presentation.RewardsViewModel
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.AlertRed

@Composable
fun LeaderboardScreen(
    viewModel: RewardsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val leaderboard = state.leaderboard
    val loading = state.loading
    val error = state.error

    LaunchedEffect(Unit) {
        viewModel.loadLeaderboard()
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
                    text = "Таблица лидеров",
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

            when {
                loading && leaderboard.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null && leaderboard.isEmpty() -> {
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
                            Button(onClick = { viewModel.loadLeaderboard() }) {
                                Text("Повторить")
                            }
                        }
                    }
                }

                leaderboard.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🏆", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Пока нет участников",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Text(
                                text = "Будьте первым!",
                                fontSize = 14.sp,
                                color = UrbanBrown.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(leaderboard) { item ->
                            LeaderboardItemView(
                                item = item,
                                position = leaderboard.indexOf(item) + 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardItemView(
    item: LeaderboardItem,
    position: Int
) {
    val medalEmoji = when (position) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (position <= 3)
                UrbanBrown.copy(alpha = 0.12f)
            else
                UrbanBrown.copy(alpha = 0.06f)
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
                if (position <= 3) {
                    Text(
                        text = medalEmoji,
                        fontSize = 28.sp
                    )
                } else {
                    Text(
                        text = "$position",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = UrbanBrown
                    )
                }

                Column {
                    Text(
                        text = "${item.firstName ?: ""} ${item.lastName ?: ""}".trim(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        color = UrbanBrown
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "⭐ ${item.lifetimePoints}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = UrbanBrown
                )
            }
        }
    }
}