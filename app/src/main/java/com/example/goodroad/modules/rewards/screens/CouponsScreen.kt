package com.example.goodroad.modules.rewards.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.goodroad.modules.rewards.data.UserRewardView
import com.example.goodroad.modules.rewards.presentation.RewardsViewModel
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.SafeRoute
import com.example.goodroad.ui.theme.SurfaceWarm
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CouponsScreen(
    viewModel: RewardsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val coupons = state.activeCoupons + state.inactiveCoupons

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUserRewards()
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
                    text = "Мои купоны",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary
                )

                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = UrbanBrown.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                state.loading && coupons.isEmpty() -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null && coupons.isEmpty() -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "❌",
                                fontSize = 48.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = state.error ?: "Не удалось загрузить купоны",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.loadCurrentUserRewards()
                                }
                            ) {
                                Text("Повторить")
                            }
                        }
                    }
                }

                coupons.isEmpty() -> {
                    EmptyCoupons()
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = coupons,
                            key = { it.id ?: "${it.offerId}-${it.code}" }
                        ) { coupon ->
                            CouponCard(
                                coupon = coupon,
                                onDelete = {
                                    coupon.id?.let(viewModel::deleteCoupon)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCoupons() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎫",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "У вас пока нет купонов",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Зарабатывайте баллы и обменивайте их на награды!",
                fontSize = 16.sp,
                color = UrbanBrown.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CouponCard(
    coupon: UserRewardView,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWarm
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = coupon.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = coupon.partnerName,
                        fontSize = 16.sp,
                        color = UrbanBrown.copy(alpha = 1.5f)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = coupon.rewardType,
                        fontSize = 14.sp,
                        color = UrbanBrown.copy(alpha = 0.7f)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "⭐",
                        fontSize = 24.sp
                    )

                    Text(
                        text = "${coupon.pricePaid}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = UrbanBrown.copy(alpha = 1.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            CouponCode(
                code = coupon.code
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (coupon.expiresAt != null) {
                Text(
                    text = "Действует до: ${formatDate(coupon.expiresAt)}",
                    fontSize = 14.sp,
                    color = UrbanBrown.copy(alpha = 0.75f)
                )
            }

            if (coupon.redeemedAt != null) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Использован: ${formatDate(coupon.redeemedAt)}",
                    fontSize = 14.sp,
                    color = UrbanBrown.copy(alpha = 0.75f)
                )
            }

            if (coupon.purchasedAt != null) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Куплен: ${formatDate(coupon.purchasedAt)}",
                    fontSize = 14.sp,
                    color = UrbanBrown.copy(alpha = 0.55f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            StatusBadge(
                status = coupon.status
            )

            if (coupon.id != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Удалить купон")
                }
            }
        }
    }
}

@Composable
private fun CouponCode(
    code: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                TextPrimary.copy(alpha = 0.06f)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Код купона",
                fontSize = 12.sp,
                color = UrbanBrown.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = code,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        IconButton(
            onClick = {
            }
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Скопировать код",
                tint = UrbanBrown.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun StatusBadge(
    status: String
) {
    val text = when (status) {
        "ACTIVE" -> "Активен"
        "EXPIRED" -> "Истёк"
        "REDEEMED" -> "Использован"
        else -> status
    }

    val background = when (status) {
        "ACTIVE" -> SafeRoute.copy(alpha = 0.15f)
        else -> TextPrimary.copy(alpha = 0.08f)
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = UrbanBrown
        )
    }
}

private fun formatDate(value: String): String {
    return runCatching {
        val instant = Instant.parse(value)

        DateTimeFormatter
            .ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.systemDefault())
            .format(instant)
    }.getOrElse {
        value
    }
}