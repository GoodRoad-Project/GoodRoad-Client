package com.example.goodroad.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class MapsViewModel : ViewModel() {

    private val _selectedObstacles = mutableStateOf<List<String>>(emptyList())
    val selectedObstacles: State<List<String>> = _selectedObstacles

    fun setSelectedObstacles(selected: List<String>) {
        _selectedObstacles.value = selected
    }
}