package com.example.goodroad.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.goodroad.data.place.PlaceInfoResponse
import com.example.goodroad.ui.theme.*
import androidx.compose.material.icons.filled.Add

private val obstacleTypeMap = mapOf(
    "STAIRS" to "Лестницы",
    "CURB" to "Поребрики",
    "ROAD_SLOPE" to "Уклоны дороги",
    "POTHOLES" to "Ямы",
    "SAND" to "Песок",
    "GRAVEL" to "Гравий"
)

private fun getObstacleTypeRussian(englishType: String?): String {
    return englishType?.let { obstacleTypeMap[it] } ?: (englishType ?: "Неизвестно")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceInfoBottomSheet(
    placeInfo: PlaceInfoResponse,
    onDismiss: () -> Unit,
    onAddReview: (String, Double, Double) -> Unit
) {
    val reviews = placeInfo.reviews
    var currentIndex by remember { mutableStateOf(0) }

    val isPlaceFound = !placeInfo.placeName.isNullOrBlank()

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
                    if(isPlaceFound) {
                        Text(
                            text = "📍 ${placeInfo.placeName ?: "Без названия"}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = UrbanBrown
                        )
                        Text(
                            text = placeInfo.address ?: "Адрес не указан",
                            fontSize = 14.sp,
                            color = UrbanBrown.copy(alpha = 0.7f)
                        )
                    } else {
                        Text(
                            text = "📍 Заведение не найдено",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = UrbanBrown
                        )
                        Text(
                            text = "Вы можете оставить отзыв об этом месте",
                            fontSize = 14.sp,
                            color = UrbanBrown.copy(alpha = 0.7f)
                        )
                    }

                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Закрыть")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "📍 Координаты:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = "Широта: ${String.format("%.6f", placeInfo.latitude ?: 0.0)}",
                fontSize = 13.sp,
                color = UrbanBrown.copy(alpha = 0.7f)
            )
            Text(
                text = "Долгота: ${String.format("%.6f", placeInfo.longitude ?: 0.0)}",
                fontSize = 13.sp,
                color = UrbanBrown.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            val averageRating = if (reviews != null && reviews.isNotEmpty()) {
                reviews.mapNotNull { it.rating }.average()
            } else {
                0.0
            }

            if (isPlaceFound && reviews != null && reviews.isNotEmpty()) {

                Text(
                    text = "Средняя оценка: ${String.format("%.1f", averageRating)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "📝 ${reviews?.size ?: 0} отзывов",
                    fontSize = 14.sp,
                    color = UrbanBrown
                )

                Spacer(modifier = Modifier.height(16.dp))

                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                if (reviews?.isNotEmpty() == true) {
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
                                text = review.comment ?: "Без комментария",
                                fontSize = 14.sp,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            val obstacles = review.obstacles
                            if (obstacles != null && obstacles.isNotEmpty()) {
                                obstacles.forEach { obstacle ->
                                    Text(
                                        text = "${getObstacleTypeRussian(obstacle.obstacleType)}: тяжесть ${obstacle.severity ?: 0}",
                                        fontSize = 13.sp,
                                        color = UrbanBrown
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            val photoUrls = review.photoUrls
                            if (photoUrls != null && photoUrls.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(photoUrls) { photoUrl ->
                                        if (photoUrl != null && photoUrl.isNotBlank()) {
                                            AsyncImage(
                                                model = photoUrl,
                                                contentDescription = "Фото",
                                                modifier = Modifier
                                                    .width(80.dp)
                                                    .height(80.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color.LightGray),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .width(80.dp)
                                                    .height(80.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color.LightGray),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("📷", fontSize = 24.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "⭐ Оценка: ${review.rating ?: 0}",
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
                            Text("Назад", color = WhiteSoft)
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
                            Text("Вперёд", color = WhiteSoft)
                        }
                    }
                } else {
                    Text("Нет отзывов", color = UrbanBrown)
                }
            } else {
                if (isPlaceFound) {
                    Text(
                        "Нет отзывов",
                        color = UrbanBrown,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    Text(
                        "Пока нет отзывов. Будьте первым!",
                        color = UrbanBrown,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    android.util.Log.d("PlaceInfoBottomSheet", "🔴 КНОПКА НАЖАТА!")
                    android.util.Log.d("PlaceInfoBottomSheet", "placeName: ${placeInfo.placeName}")
                    android.util.Log.d("PlaceInfoBottomSheet", "latitude: ${placeInfo.latitude}")
                    android.util.Log.d("PlaceInfoBottomSheet", "longitude: ${placeInfo.longitude}")

                    onAddReview(
                        placeInfo.placeName ?: "",
                        placeInfo.latitude ?: 0.0,
                        placeInfo.longitude ?: 0.0
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = UrbanBrown,
                    contentColor = WhiteSoft
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Добавить отзыв")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}