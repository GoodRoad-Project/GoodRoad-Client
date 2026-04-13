package com.example.goodroad.ui.reviews

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.data.review.*
import com.example.goodroad.ui.auth.*
import com.example.goodroad.ui.theme.*
import com.example.goodroad.ui.user.UserDecor
import com.example.goodroad.ui.viewmodel.ReviewsViewModel
import kotlinx.coroutines.delay

@Composable
fun UserReviewsScreen(
    reviewsViewModel: ReviewsViewModel,
    onBack: () -> Unit,
    onAddReview: () -> Unit,
    onOpenDetails: (ReviewCardResp) -> Unit,
    onEditReview: (ReviewCardResp) -> Unit
) {
    val reviews by reviewsViewModel.reviews
    val isLoading by reviewsViewModel.isLoading
    val errorMessage by reviewsViewModel.errorMessage
    val successMessage by reviewsViewModel.successMessage

    val approvedCount = reviews.count { it.status == "APPROVED" }
    val rejectedCount = reviews.count { it.status == "REJECTED" }
    val pendingCount = reviews.count { it.status == "PENDING" }

    LaunchedEffect(Unit) {
        reviewsViewModel.loadReviews()
    }

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            delay(10_000)
            reviewsViewModel.clearSuccessMessage()
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
            UserDecor()

            Text(
                text = "Мои отзывы",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(20.dp))

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, UrbanBrown.copy(alpha = 0.4f)),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = UrbanBrown.copy(alpha = 0.06f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Статистика по отзывам",
                        style = MaterialTheme.typography.titleMedium,
                        color = UrbanBrown
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Одобренных отзывов: $approvedCount",
                        style = MaterialTheme.typography.bodyLarge,
                        color = UrbanBrown
                    )
                    Text(
                        text = "Отклоненных отзывов: $rejectedCount",
                        style = MaterialTheme.typography.bodyLarge,
                        color = UrbanBrown
                    )
                    Text(
                        text = "На модерации: $pendingCount",
                        style = MaterialTheme.typography.bodyLarge,
                        color = UrbanBrown
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            AuthButton(text = "Добавить отзыв") {
                reviewsViewModel.clearMessages()
                onAddReview()
            }

            Spacer(Modifier.height(10.dp))

            AuthButton(
                text = "Назад в профиль",
                backgroundColor = UrbanBrown
            ) {
                onBack()
            }

            AuthSuccessText(successMessage)
            AuthStatusText(errorMessage)

            Spacer(Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (reviews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Пока нет ни одного отзыва",
                        style = MaterialTheme.typography.bodyLarge,
                        color = UrbanBrown
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reviews, key = { it.id }) { review ->
                        ReviewListItem(
                            review = review,
                            onOpenDetails = { onOpenDetails(review) },
                            onEdit = { onEditReview(review) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewListItem(
    review: ReviewCardResp,
    onOpenDetails: () -> Unit,
    onEdit: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, moderationStatusColor(review.status)),
        colors = CardDefaults.outlinedCardColors(containerColor = BackgroundLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ReviewCardSummary(review)

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReviewActionButton(
                    text = "Редактировать",
                    backgroundColor = UrbanBrown,
                    modifier = Modifier.weight(1f)
                ) {
                    onEdit()
                }
                ReviewActionButton(
                    text = "Подробнее",
                    backgroundColor = SafeGreen,
                    modifier = Modifier.weight(1f)
                ) {
                    onOpenDetails()
                }
            }
        }
    }
}
