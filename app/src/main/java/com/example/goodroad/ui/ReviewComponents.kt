package com.example.goodroad.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import coil.compose.*
import com.example.goodroad.modules.review.data.ReviewAddress
import com.example.goodroad.modules.review.data.ReviewCardResp
import com.example.goodroad.ui.theme.*
import java.time.*
import java.time.format.*

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
    val parts = mutableListOf<String>()

    address.country
        ?.takeIf { it.isNotBlank() }
        ?.let { parts += it }

    address.region
        ?.takeIf { it.isNotBlank() }
        ?.let { parts += it }

    val locality = listOfNotNull(
        address.localityType?.takeIf { it.isNotBlank() },
        address.city?.takeIf { it.isNotBlank() }
    ).joinToString(" ")

    if (locality.isNotBlank()) {
        parts += locality
    }

    address.street
        ?.takeIf { it.isNotBlank() }
        ?.let { parts += it }

    address.house
        ?.takeIf { it.isNotBlank() }
        ?.let { parts += it }

    address.placeName
        ?.takeIf { it.isNotBlank() }
        ?.let { parts += it }

    return parts.joinToString(", ")
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
            style = MaterialTheme.typography.titleMedium,
            color = UrbanBrown
        )
        Spacer(Modifier.height(4.dp))
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
            val isSelected = item == value

            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onValueChange(item)
                    }
                    .background(
                        color = if (isSelected) SafeGreen.copy(alpha = 0.12f) else BackgroundLight,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) SafeGreen else BorderWarm.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (isSelected) SafeGreen else UrbanBrown.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else UrbanBrown
                    )
                }
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
        Text(
            text = buildAddressLine(review.address),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            maxLines = Int.MAX_VALUE
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Оценка",
                    style = MaterialTheme.typography.titleSmall,
                    color = UrbanBrown
                )
                Text(
                    text = "${review.rating}/5",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Баллы",
                    style = MaterialTheme.typography.titleSmall,
                    color = UrbanBrown
                )
                Text(
                    text = "${review.awardedPoints} ⭐",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Дата",
                    style = MaterialTheme.typography.titleSmall,
                    color = UrbanBrown
                )
                Text(
                    text = formatReviewDate(review.createdAt),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            ReviewStatusBadge(review.status)
        }
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
        modifier = modifier.height(44.dp),
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
