package com.example.goodroad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.goodroad.ui.auth.*
import com.example.goodroad.ui.theme.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoodRoadTheme {
                AuthApp()
            }
        }
    }
}
