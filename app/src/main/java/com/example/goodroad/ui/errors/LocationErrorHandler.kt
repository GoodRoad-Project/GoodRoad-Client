package com.example.goodroad.ui.errors

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.example.goodroad.data.network.location.LocationError

class LocationErrorHandler (
    private val context: Context
) {
    fun getErrorMessage(error: LocationError): String {
        return when(error) {
            is LocationError.PermissionsDenied -> {
                "Нет разрешения на доступ геолокации. Разрешите доступ в настройках"
            }
            is LocationError.GPSDisabled -> {
                "GPS выключен."
            }
            is LocationError.NoLocation -> {
                "Не удалось определить мостоположение. Попробуйте позже"
            }
        }
    }

    fun showErrorDialog(
        error: LocationError,
        onPositiveClick: (() -> Unit)? = null
    ) {
        val message = getErrorMessage(error)

        val builder = AlertDialog.Builder(context)
            .setTitle("Ошибка геолокации")
            .setMessage(message)
            .setPositiveButton("ОК") {_, _ ->
                onPositiveClick?.invoke()
            }
        when (error) {
            is LocationError.PermissionsDenied -> {
                builder.setNeutralButton("Настройки") { _, _ ->
                    openAppSettings()
                }
            }
            is LocationError.GPSDisabled -> {
                builder.setNeutralButton("Настройки GPS") { _, _ ->
                    openGpsSettings()
                }
            }
            is LocationError.NoLocation -> {
            }
        }

        builder.show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:${context.packageName}")
        context.startActivity(intent)
    }

    private fun openGpsSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }

}