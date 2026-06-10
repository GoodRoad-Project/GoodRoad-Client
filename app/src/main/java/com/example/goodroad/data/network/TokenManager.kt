package com.example.goodroad.data.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                "secure_tokens",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e("TokenManager", "Failed to create encrypted prefs, using regular", e)
            context.getSharedPreferences("tokens", Context.MODE_PRIVATE)
        }
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit().apply {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            apply()
        }
        Log.d("TokenManager", "✅ Tokens saved")
    }

    fun getAccessToken(): String? = prefs.getString("access_token", null)

    fun getRefreshToken(): String? = prefs.getString("refresh_token", null)

    fun clearTokens() {
        prefs.edit().apply {
            remove("access_token")
            remove("refresh_token")
            apply()
        }
        Log.d("TokenManager", "🗑️ Tokens cleared")
    }

    fun updateAccessToken(newAccessToken: String) {
        prefs.edit().putString("access_token", newAccessToken).apply()
        Log.d("TokenManager", "🔄 Access token updated")
    }

    fun isLoggedIn(): Boolean = !getAccessToken().isNullOrBlank()
}