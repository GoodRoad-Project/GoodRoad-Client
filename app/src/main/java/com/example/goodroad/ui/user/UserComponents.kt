package com.example.goodroad.ui.user

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import com.example.goodroad.ui.theme.*

@Composable
fun UserInfoBlock(label: String, value: String?) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = UrbanBrown
        )
        Text(
            text = value ?: "-",
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary
        )
        Spacer(Modifier.height(8.dp))
    }
}