package com.example.goodroad.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import com.example.goodroad.ui.theme.*

@Composable
fun UserDecor() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(SurfaceWarm)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val lightPatch = Path().apply {
                moveTo(0f, size.height * 0.76f)
                cubicTo(
                    size.width * 0.1f, size.height * 0.7f,
                    size.width * 0.2f, size.height * 0.45f,
                    size.width * 0.34f, size.height * 0.42f
                )
                cubicTo(
                    size.width * 0.49f, size.height * 0.38f,
                    size.width * 0.54f, size.height * 0.18f,
                    size.width * 0.72f, size.height * 0.12f
                )
                lineTo(size.width, size.height * 0.12f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = lightPatch,
                color = BackgroundLight
            )

            val road = Path().apply {
                // Начинаем в правом верхнем углу
                moveTo(size.width, 0f)
                cubicTo(
                    size.width * 0.9f, size.height * 0.1f,
                    size.width * 0.75f, size.height * 0.2f,
                    size.width * 0.62f, size.height * 0.35f
                )
                cubicTo(
                    size.width * 0.5f, size.height * 0.5f,
                    size.width * 0.35f, size.height * 0.6f,
                    size.width * 0.15f, size.height * 0.7f
                )
                cubicTo(
                    size.width * 0.05f, size.height * 0.75f,
                    0f, size.height * 0.82f,
                    0f, size.height * 0.9f
                )
            }

            drawPath(
                path = road,
                brush = SolidColor(UrbanBrown),
                style = Stroke(
                    width = size.width * 0.09f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            drawPath(
                path = road,
                color = BackgroundLight.copy(alpha = 0.95f),
                style = Stroke(
                    width = size.width * 0.014f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(size.width * 0.06f, size.width * 0.04f)
                    )
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BackgroundLight.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GR",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}