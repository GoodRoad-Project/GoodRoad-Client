package com.example.goodroad.ui.users.moderators

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.goodroad.data.moderationReview.ReviewForModeration
import com.example.goodroad.ui.auth.*
import com.example.goodroad.ui.reviews.*
import com.example.goodroad.ui.theme.*
import com.example.goodroad.ui.users.UserDecor
import com.example.goodroad.ui.viewmodel.ReviewModerationViewModel

@Composable
fun ReviewModerationScreen(
    viewModel: ReviewModerationViewModel,
    onBack: () -> Unit
) {
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var selectedReviewForReject by remember { mutableStateOf<ReviewForModeration?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadReviews()
    }

    if (selectedReviewForReject != null) {
        RejectReasonDialog(
            review = selectedReviewForReject!!,
            onDismiss = { selectedReviewForReject = null },
            onConfirm = { reason ->
                viewModel.reject(selectedReviewForReject!!.id, reason) {
                    selectedReviewForReject = null
                }
            }
        )
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
            UserDecor()

            Text(
                text = "Модерация отзывов",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Отзывы, ожидающие проверки",
                style = MaterialTheme.typography.bodyLarge,
                color = UrbanBrown
            )

            Spacer(Modifier.height(16.dp))

            AuthSuccessText(
                text = successMessage,
                onTimeout = viewModel::clearMessages
            )
            AuthStatusText(
                text = errorMessage,
                onTimeout = viewModel::clearMessages
            )

            Spacer(Modifier.height(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                when {
                    isLoading && reviews.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    reviews.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = SafeGreen
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "Нет отзывов на модерации",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = UrbanBrown
                                )
                            }
                        }
                    }
                    else -> {
                        val listState = rememberLazyListState()

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(reviews, key = { it.id }) { review ->
                                ReviewModerationCard(
                                    review = review,
                                    onTakeInWork = { viewModel.takeInWork(review.id) },
                                    onApprove = { viewModel.approve(review.id) },
                                    onReject = { selectedReviewForReject = review },
                                    onRelease = { viewModel.release(review.id) }
                                )
                            }

                            if (uiState.hasMore && reviews.isNotEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                            .clickable { viewModel.loadMore() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isLoading) {
                                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                        } else {
                                            Text(
                                                text = "Загрузить еще",
                                                color = UrbanBrown,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            AuthButton(
                text = "Назад в профиль",
                backgroundColor = UrbanBrown
            ) {
                onBack()
            }
        }
    }
}

@Composable
private fun ReviewModerationCard(
    review: ReviewForModeration,
    onTakeInWork: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onRelease: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (review.takenByMe) SafeGreen.copy(alpha = 0.08f) else BackgroundLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = buildAddressLine(review.address),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = UrbanBrown,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
                .padding(start = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Оценка: ${review.rating}/5",
                    style = MaterialTheme.typography.bodyMedium,
                    color = UrbanBrown
                )

                ModerationStatusChip(
                    takenInWork = review.takenInWork,
                    takenByMe = review.takenByMe
                )
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.padding(horizontal = 0.dp)
            ) {
                Text(
                    text = if (isExpanded) "Скрыть детали" else "Показать детали",
                    color = UrbanBrown,
                    fontSize = 14.sp
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = UrbanBrown
                )
            }

            if (isExpanded) {
                Spacer(Modifier.height(8.dp))
                Divider(color = BorderWarm)
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Координаты",
                    style = MaterialTheme.typography.titleMedium,
                    color = UrbanBrown
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${review.latitude}, ${review.longitude}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Комментарий",
                    style = MaterialTheme.typography.titleMedium,
                    color = UrbanBrown
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = review.comment?.ifBlank { "—" } ?: "—",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Препятствия",
                    style = MaterialTheme.typography.titleMedium,
                    color = UrbanBrown
                )
                Spacer(Modifier.height(8.dp))
                review.obstacles.forEach { obstacle ->
                    Text(
                        text = "${obstacleLabel(obstacle.obstacleType)}: ${obstacleSeverityText(obstacle.severity.toInt())}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Spacer(Modifier.height(8.dp))

                if (review.photoUrls.isNotEmpty()) {
                    Text(
                        text = "Фотографии (${review.photoUrls.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = UrbanBrown
                    )
                    Spacer(Modifier.height(8.dp))
                    ReviewPhotosStrip(review.photoUrls)
                    Spacer(Modifier.height(8.dp))
                }

                if (review.takenInWork && review.takenByModeratorId != null) {
                    Text(
                        text = if (review.takenByMe) "✓ Взято вами в работу" else "Взято другим модератором",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (review.takenByMe) SafeGreen else AlertRed
                    )
                    review.takenAt?.let {
                        Text(
                            text = "Взято: ${formatReviewDate(it.toString())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = UrbanBrown
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }

                if (review.status == "REJECTED" && review.moderatorComment != null) {
                    ReviewInfoRow("Причина отклонения", review.moderatorComment)
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            when {
                review.status != "PENDING" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (review.status == "APPROVED") "✓ Одобрен" else "✗ Отклонен",
                            color = if (review.status == "APPROVED") SafeGreen else AlertRed,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                review.takenInWork && !review.takenByMe -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "В работе у другого модератора",
                            color = UrbanBrown,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                review.takenByMe -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ModerationActionButton(
                                text = "Отклонить",
                                backgroundColor = AlertRed,
                                modifier = Modifier.weight(1f)
                            ) { onReject() }

                            ModerationActionButton(
                                text = "Одобрить",
                                backgroundColor = SafeGreen,
                                modifier = Modifier.weight(1f)
                            ) { onApprove() }
                        }

                        ModerationActionButton(
                            text = "Отказаться от модерации",
                            backgroundColor = UrbanBrown,
                            modifier = Modifier.fillMaxWidth()
                        ) { onRelease() }
                    }
                }
                else -> {
                    ModerationActionButton(
                        text = "Взять в работу",
                        backgroundColor = UrbanBrown,
                        modifier = Modifier.fillMaxWidth()
                    ) { onTakeInWork() }
                }
            }
        }
    }
}

@Composable
private fun ModerationStatusChip(takenInWork: Boolean, takenByMe: Boolean) {
    val (text, color) = when {
        takenByMe -> "В работе (вы)" to SafeGreen
        takenInWork -> "В работе" to AlertRed
        else -> "Ожидает модерации" to UrbanBrown
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ModerationActionButton(
    text: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = WhiteSoft
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun RejectReasonDialog(
    review: ReviewForModeration,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var reason by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Причина отклонения отзыва",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Укажите причину, по которой этот отзыв отклоняется",
                    style = MaterialTheme.typography.bodySmall,
                    color = UrbanBrown
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Причина", color = UrbanBrown) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BackgroundLight,
                        unfocusedContainerColor = BackgroundLight,
                        focusedIndicatorColor = SafeGreen,
                        unfocusedIndicatorColor = BorderWarm,
                        cursorColor = SafeGreen
                    )
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = UrbanBrown
                        ),
                        border = BorderStroke(1.dp, BorderWarm)
                    ) {
                        Text("Отмена")
                    }

                    Button(
                        onClick = { if (reason.isNotBlank()) onConfirm(reason) },
                        modifier = Modifier.weight(1f),
                        enabled = reason.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AlertRed,
                            contentColor = WhiteSoft
                        )
                    ) {
                        Text("Отклонить")
                    }
                }
            }
        }
    }
}