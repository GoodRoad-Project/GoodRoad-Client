package com.example.goodroad.ui.maps

import androidx.compose.runtime.Composable

@Composable
fun MapsNav(
    onBackToProfile: () -> Unit,
    onSaveObstacles: (List<String>) -> Unit
) {
    ObstacleSelectScreen(
        onBackToProfile = onBackToProfile,
        onSave = onSaveObstacles
    )
}