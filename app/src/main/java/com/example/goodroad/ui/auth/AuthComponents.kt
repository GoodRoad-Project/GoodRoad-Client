package com.example.goodroad.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.theme.AlertRed
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.SafeRoute
import com.example.goodroad.ui.theme.TextSecondary
import com.example.goodroad.ui.theme.UrbanBrown

@Composable
fun AuthButton(
    text: String,
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