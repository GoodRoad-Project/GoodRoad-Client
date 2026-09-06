package com.example.goodroad.modules.review.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

    val approvedCount = reviews.count {
        it.status == "APPROVED"
    }

    val rejectedCount = reviews.count {
        it.status == "REJECTED"
    }

    val pendingCount = reviews.count {
        it.status == "PENDING"
    }

    LaunchedEffect(Unit) {
        reviewsViewModel.loadReviews()
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
                text = "Мои отзывы",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(12.dp))

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),

                border = BorderStroke(
                    1.dp,
                    UrbanBrown.copy(alpha = 0.4f)
                ),

                colors = CardDefaults.outlinedCardColors(
                    containerColor = UrbanBrown.copy(alpha = 0.06f)
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Статистика по отзывам",
                        style = MaterialTheme.typography.titleMedium,
                        color = UrbanBrown.copy(
                            red = (UrbanBrown.red * 0.7f).coerceIn(0f, 1f),
                            green = (UrbanBrown.green * 0.5f).coerceIn(0f, 1f),
                            blue = (UrbanBrown.blue * 0.8f).coerceIn(0f, 1f)
                        )
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Одобренных отзывов: $approvedCount",
                        color = UrbanBrown
                    )

                    Text(
                        text = "Отклоненных отзывов: $rejectedCount",
                        color = UrbanBrown
                    )

                    Text(
                        text = "На модерации: $pendingCount",
                        color = UrbanBrown
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = UrbanBrown
                        )
                    }
                }

                reviews.isEmpty() -> {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Пока нет ни одного отзыва",
                            color = UrbanBrown
                        )
                    }
                }

                else -> {

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        reviews.forEach { review ->
                            ReviewListItem(
                                review = review,
                                onOpenDetails = {
                                    onOpenDetails(review)
                                },
                                onEdit = {
                                    onEditReview(review)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            PrimaryButton(
                text = "Добавить отзыв",
                modifier = Modifier.fillMaxWidth()
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
        border = BorderStroke(
            2.dp,
            moderationStatusColor(review.status)
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = BackgroundLight
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            ReviewCardSummary(review)

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                PrimaryButton(
                    text = "Редактировать",
                    backgroundColor = UrbanBrown,
                    modifier = Modifier.weight(1f)
                ) {
                    onEdit()
                }

                PrimaryButton(
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