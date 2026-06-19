package com.example.goodroad.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.text.font.FontWeight
import com.example.goodroad.data.place.PlaceInfoResponse
import com.example.goodroad.data.place.ReviewResp
import com.example.goodroad.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceInfoBottomSheet(
    placeInfo: PlaceInfoResponse,
    onDismiss: () -> Unit
) {
    val reviews = placeInfo.reviews
    var currentIndex by remember { mutableStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📍 ${placeInfo.placeName}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = UrbanBrown
                    )
                    Text(
                        text = placeInfo.address,
                        fontSize = 14.sp,
                        color = UrbanBrown.copy(alpha = 0.7f)
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Закрыть")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "⭐ Средняя оценка: ${String.format("%.1f", placeInfo.averageSeverity)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📝 ${reviews.size} отзывов",
                fontSize = 14.sp,
                color = UrbanBrown
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            if (reviews.isNotEmpty()) {
                val review = reviews[currentIndex]

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = UrbanBrown.copy(alpha = 0.08f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = review.comment,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        review.obstacles.forEach { obstacle ->
                            Row {
                                Text(
                                    text = when (obstacle.obstacleType) {
                                        "STAIRS" -> "🪜 "
                                        "CURB" -> "📏 "
                                        "ROAD_SLOPE" -> "⛰️ "
                                        "POTHOLES" -> "🕳️ "
                                        else -> "⚠️ "
                                    },
                                    fontSize = 13.sp,
                                    color = UrbanBrown
                                )
                                Text(
                                    text = "${obstacle.obstacleType}: тяжесть ${obstacle.severity}",
                                    fontSize = 13.sp,
                                    color = UrbanBrown
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (review.photoUrls.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(review.photoUrls) { photoUrl ->
                                    AsyncImage(
                                        model = photoUrl,
                                        contentDescription = "Фото",
                                        modifier = Modifier
                                            .width(80.dp)
                                            .height(80.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.LightGray),
                                        contentScale = ContentScale.Crop,
                                        placeholder = painterResource(R.drawable.placeholder_image)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "⭐ Оценка: ${review.rating}",
                            fontSize = 12.sp,
                            color = UrbanBrown.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { if (currentIndex > 0) currentIndex-- },
                        enabled = currentIndex > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = UrbanBrown)
                    ) {
                        Text("← Назад", color = WhiteSoft)
                    }

                    Text(
                        text = "${currentIndex + 1} / ${reviews.size}",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 14.sp,
                        color = TextPrimary
                    )

                    Button(
                        onClick = { if (currentIndex < reviews.size - 1) currentIndex++ },
                        enabled = currentIndex < reviews.size - 1,
                        colors = ButtonDefaults.buttonColors(containerColor = UrbanBrown)
                    ) {
                        Text("Вперёд →", color = WhiteSoft)
                    }
                }
            } else {
                Text("Нет отзывов", color = UrbanBrown)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}