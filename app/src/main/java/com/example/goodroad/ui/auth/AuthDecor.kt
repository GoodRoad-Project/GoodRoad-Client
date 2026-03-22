package com.example.goodroad.ui.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.SurfaceWarm
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.TextSecondary
import com.example.goodroad.ui.theme.UrbanBrown
import androidx.compose.foundation.layout.size

@Composable
fun AuthScreenFrame(
    title: String,
    subtitle: String? = null,
    action: @Composable () -> Unit,
    footer: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        AuthDecor()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            Spacer(Modifier.height(28.dp))
            content()
            Spacer(Modifier.height(28.dp))
            action()
            Spacer(Modifier.height(16.dp))
            footer()
        }
    }
}

@Composable
fun AuthDecor() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp)
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
                moveTo(size.width * 0.88f, -8f)
                cubicTo(
                    size.width * 0.8f, size.height * 0.06f,
                    size.width * 0.7f, size.height * 0.14f,
                    size.width * 0.62f, size.height * 0.28f
                )
                cubicTo(
                    size.width * 0.55f, size.height * 0.4f,
                    size.width * 0.42f, size.height * 0.52f,
                    size.width * 0.26f, size.height * 0.58f
                )
                cubicTo(
                    size.width * 0.15f, size.height * 0.62f,
                    size.width * 0.07f, size.height * 0.68f,
                    -8f, size.height * 0.8f
                )
            }

            drawPath(
                path = road,
                brush = SolidColor(UrbanBrown),
                style = Stroke(
                    width = size.width * 0.09f,
                    cap = StrokeCap.Square,
                    join = StrokeJoin.Round
                )
            )

            drawPath(
                path = road,
                color = BackgroundLight.copy(alpha = 0.95f),
                style = Stroke(
                    width = size.width * 0.014f,
                    cap = StrokeCap.Butt,
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
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
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