package com.example.goodroad.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.SurfaceWarm
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.TextSecondary
import com.example.goodroad.ui.theme.UrbanBrown

@Composable
fun SecurityScreen(
    onBack: () -> Unit,
    onChangePassword: () -> Unit,
    onChangePhone: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                UserDecor()
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Безопасность",
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
            }

            item {
                SecurityCard(
                    title = "Сменить пароль",
                    description = "Изменить пароль от аккаунта",
                    onClick = onChangePassword
                )
            }

            item {
                SecurityCard(
                    title = "Сменить телефон",
                    description = "Изменить номер телефона, привязанный к аккаунту",
                    onClick = onChangePhone
                )
            }
        }
    }
}

@Composable
private fun SecurityCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWarm
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}