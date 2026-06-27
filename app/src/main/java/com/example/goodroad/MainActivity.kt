package com.example.goodroad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.modules.auth.navigation.AuthApp
import com.example.goodroad.ui.theme.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiClient.init(applicationContext)
        setContent {
            GoodRoadTheme {
                AuthApp()
            }
        }
    }
}
