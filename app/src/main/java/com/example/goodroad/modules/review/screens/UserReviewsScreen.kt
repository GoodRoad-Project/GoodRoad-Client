package com.example.goodroad.modules.review.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.review.data.ReviewCardResp
import com.example.goodroad.modules.review.presentation.ReviewsViewModel
import com.example.goodroad.ui.*
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.moderationStatusColor
import com.example.goodroad.ui.theme.*

@Composable
fun UserReviewsScreen(
    reviewsViewModel: ReviewsViewModel,
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                        Text("Одобренных отзывов: $approvedCount", color = UrbanBrown)
                        Text("Отклоненных отзывов: $rejectedCount", color = UrbanBrown)
                        Text("На модерации: $pendingCount", color = UrbanBrown)
                    }
                }

                Spacer(Modifier.height(16.dp))

                AuthSuccessText(
                    text = successMessage,
                    onTimeout = reviewsViewModel::clearSuccessMessage
                )

                AuthStatusText(
                    text = errorMessage,
                    onTimeout = reviewsViewModel::clearErrorMessage
                )

                Spacer(Modifier.height(16.dp))

                when {
                    isLoading -> {
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
                            Text(
                                text = "Пока нет ни одного отзыва",
                                color = UrbanBrown
                            )
                        }
                    }

                    else -> {
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

            PrimaryButton(
                text = "Добавить отзыв",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            ) {
                reviewsViewModel.clearMessages()
                onAddReview()
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