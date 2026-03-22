package com.example.goodroad.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import com.example.goodroad.ui.theme.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun AuthButton(
    text: String,
    backgroundColor: Color = SafeRoute,
    contentColor: Color = BackgroundLight,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SafeRoute,
            contentColor = BackgroundLight,
            disabledContainerColor = SafeRoute.copy(alpha = 0.6f),
            disabledContentColor = BackgroundLight
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AuthFooter(
    prefix: String,
    action: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = prefix,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(Modifier.width(4.dp))
        TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = action,
                style = MaterialTheme.typography.bodyMedium,
                color = UrbanBrown,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AuthStatusText(
    text: String?
) {
    if (text == null) return

    Spacer(Modifier.height(12.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = AlertRed
    )
}

@Composable
fun AuthSuccessText(
    text: String?
) {
    if (text == null) return

    Spacer(Modifier.height(12.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = SafeGreen
    )
}