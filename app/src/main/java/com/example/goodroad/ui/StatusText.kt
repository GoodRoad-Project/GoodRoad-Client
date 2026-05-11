package com.example.goodroad.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AuthStatusText(
    text: String?,
    onTimeout: (() -> Unit)? = null
) {
    if (text == null) return

    LaunchedEffect(text) {
        delay(5_000)
        onTimeout?.invoke()
    }

    Spacer(Modifier.height(12.dp))

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = AlertRed
    )
}

@Composable
fun AuthSuccessText(
    text: String?,
    onTimeout: (() -> Unit)? = null
) {
    if (text == null) return

    LaunchedEffect(text) {
        delay(5_000)
        onTimeout?.invoke()
    }

    Spacer(Modifier.height(12.dp))

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = SafeGreen
    )
}