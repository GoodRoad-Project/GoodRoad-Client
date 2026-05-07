package com.example.goodroad.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.theme.*

@Composable
fun AuthFooter(
    prefix: String,
    action: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
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