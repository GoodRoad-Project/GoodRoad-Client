package com.example.goodroad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.goodroad.ui.AuthApp
import com.example.goodroad.ui.theme.GoodRoadTheme

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
