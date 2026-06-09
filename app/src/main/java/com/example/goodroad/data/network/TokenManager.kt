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

    fun saveToken(token: String) {
        Log.d("TokenManager", "📝 saveToken called")
        Log.d("TokenManager", "   Token: ${token.take(50)}...")
        prefs.edit().putString("access_token", token).apply()
        Log.d("TokenManager", "   Saved successfully")

        // Проверяем что сохранилось
        val saved = prefs.getString("access_token", null)
        Log.d("TokenManager", "   Verification: ${if (saved != null) "YES" else "NO"}")
    }

    fun getToken(): String? {
        val token = prefs.getString("access_token", null)
        Log.d("TokenManager", "getToken called")
        Log.d("TokenManager", "Token exists: ${if (token != null) "YES (${token.take(50)}...)" else "NO"}")
        return token
    }

    fun clearToken() {
        Log.d("TokenManager", "🗑️ clearToken called")
        prefs.edit().remove("access_token").apply()
    }

    fun isLoggedIn(): Boolean {
        val result = !getToken().isNullOrBlank()
        Log.d("TokenManager", "isLoggedIn: $result")
        return result
    }
}