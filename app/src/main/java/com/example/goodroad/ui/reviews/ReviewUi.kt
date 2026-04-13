package com.example.goodroad.ui.reviews

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.goodroad.data.review.*
import com.example.goodroad.ui.theme.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val ReviewObstacleTypes = listOf(
    "CURB",
    "STAIRS",
    "ROAD_SLOPE",
    "POTHOLES",
    "SAND",
    "GRAVEL"
)

fun obstacleLabel(type: String): String {
    return when (type) {
        "CURB" -> "Бордюр"
        "STAIRS" -> "Лестницы"
        "ROAD_SLOPE" -> "Наклон дороги"
        "POTHOLES" -> "Ямы"
        "SAND" -> "Песок"
        "GRAVEL" -> "Гравий"
        else -> type
    }
}

fun obstacleSeverityText(severity: Int): String {
    return when (severity) {
        0 -> "нет"
        1 -> "слабая тяжесть"
        2 -> "средняя тяжесть"
        3 -> "сильная тяжесть"
        else -> severity.toString()
    }
}

fun moderationStatusText(status: String): String {
    return when (status) {
        "APPROVED" -> "Одобрен"
        "REJECTED" -> "Отклонен"
        else -> "На модерации"
    }
}

fun moderationStatusColor(status: String): Color {
    return when (status) {
        "APPROVED" -> SafeGreen
        "REJECTED" -> AlertRed
        else -> UrbanBrown
    }
}

fun buildAddressLine(address: ReviewAddress): String {
    val parts = listOf(
        address.country,
        address.region,
        address.localityType,
        address.city,
        address.street,
        address.house,
        address.placeName
    )
    return parts.filterNot { it.isNullOrBlank() }.joinToString(", ")
}

fun formatReviewDate(raw: String): String {
    return try {
        val instant = Instant.parse(raw)
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        formatter.format(instant.atZone(ZoneId.systemDefault()))
    } catch (_: Exception) {
        raw.take(10)
    }
}

@Composable
fun ReviewInfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = UrbanBrown
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary
        )
    }
}

@Composable
fun SeveritySelector(
    value: Int?,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        range.forEach { item ->
            val selected = item == value
            OutlinedButton(
                onClick = { onValueChange(item) },
                border = BorderStroke(1.dp, if (selected) SafeGreen else BorderWarm),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selected) SafeGreen.copy(alpha = 0.12f) else Color.Transparent,
                    contentColor = if (selected) SafeGreen else UrbanBrown
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 12.dp)
            ) {
                Text(item.toString())
            }
        }
    }
}

@Composable
fun ReviewStatusBadge(status: String) {
    val color = moderationStatusColor(status)
    Surface(
        color = color.copy(alpha = 0.08f),
        contentColor = color,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, color)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = moderationStatusText(status),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ReviewCardSummary(review: ReviewCardResp) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ReviewInfoRow("Адрес", buildAddressLine(review.address))
        Spacer(Modifier.height(8.dp))
        ReviewInfoRow("Оценка", review.rating.toString())
        Spacer(Modifier.height(8.dp))
        ReviewInfoRow("Баллы", review.awardedPoints.toString())
        Spacer(Modifier.height(8.dp))
        ReviewInfoRow("Дата", formatReviewDate(review.createdAt))
        Spacer(Modifier.height(12.dp))
        ReviewStatusBadge(review.status)
    }
}

@Composable
fun ReviewActionButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = SafeGreen,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = WhiteSoft,
            disabledContainerColor = backgroundColor.copy(alpha = 0.6f),
            disabledContentColor = WhiteSoft
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ReviewSquareActionButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = SafeGreen,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = WhiteSoft,
            disabledContainerColor = backgroundColor.copy(alpha = 0.6f),
            disabledContentColor = WhiteSoft
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ReviewPhotosStrip(
    photoUrls: List<String>,
    onRemove: ((String) -> Unit)? = null
) {
    if (photoUrls.isEmpty()) {
        Text(
            text = "Фото не добавлены",
            style = MaterialTheme.typography.bodyMedium,
            color = UrbanBrown
        )
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        photoUrls.forEach { url ->
            Column {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                if (onRemove != null) {
                    TextButton(onClick = { onRemove(url) }) {
                        Text(
                            text = "Убрать",
                            color = UrbanBrown
                        )
                    }
                }
            }
        }
    }
}
