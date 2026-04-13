package com.example.goodroad.ui.reviews

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.data.review.*
import com.example.goodroad.ui.auth.*
import com.example.goodroad.ui.theme.*
import com.example.goodroad.ui.user.UserDecor
import com.example.goodroad.ui.viewmodel.ReviewsViewModel

@Composable
fun ReviewDetailsScreen(
    review: ReviewCardResp,
    reviewsViewModel: ReviewsViewModel,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit
) {
    val isSubmitting by reviewsViewModel.isSubmitting
    val errorMessage by reviewsViewModel.errorMessage
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Вы уверены?",
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Отзыв будет удален без возможности восстановления.",
                    color = UrbanBrown
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        reviewsViewModel.deleteReview(review.id, onDeleted)
                    }
                ) {
                    Text("Да", color = AlertRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Нет", color = UrbanBrown)
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            UserDecor()

            Text(
                text = "Подробности отзыва",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(20.dp))

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(2.dp, moderationStatusColor(review.status)),
                colors = CardDefaults.outlinedCardColors(containerColor = BackgroundLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ReviewCardSummary(review)

                    Spacer(Modifier.height(16.dp))
                    ReviewInfoRow("Координаты", "${review.latitude}, ${review.longitude}")
                    Spacer(Modifier.height(12.dp))
                    ReviewInfoRow("Комментарий", review.comment?.ifBlank { "—" } ?: "—")
                    Spacer(Modifier.height(12.dp))
                    ReviewInfoRow(
                        "Комментарий модератора",
                        review.moderatorComment?.ifBlank { "—" } ?: "—"
                    )

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Фотографии",
                        style = MaterialTheme.typography.titleMedium,
                        color = UrbanBrown
                    )
                    Spacer(Modifier.height(8.dp))
                    ReviewPhotosStrip(review.photoUrls)

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Препятствия",
                        style = MaterialTheme.typography.titleMedium,
                        color = UrbanBrown
                    )
                    Spacer(Modifier.height(8.dp))
                    review.obstacles.forEach { obstacle ->
                        Text(
                            text = "${obstacleLabel(obstacle.obstacleType)} — ${obstacleSeverityText(obstacle.severity.toInt())}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }

            AuthStatusText(errorMessage)

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ReviewSquareActionButton(
                    text = "Редактировать",
                    backgroundColor = SafeGreen,
                    modifier = Modifier.weight(1f)
                ) {
                    onEdit()
                }
                ReviewSquareActionButton(
                    text = if (isSubmitting) "Удаляем..." else "Удалить",
                    backgroundColor = AlertRed,
                    modifier = Modifier.weight(1f),
                    enabled = !isSubmitting
                ) {
                    showDeleteDialog = true
                }
            }

            Spacer(Modifier.height(12.dp))

            ReviewSquareActionButton(
                text = "Назад к отзывам",
                backgroundColor = UrbanBrown,
                modifier = Modifier.fillMaxWidth()
            ) {
                onBack()
            }
        }
    }
}
