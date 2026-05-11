package com.example.goodroad.ui.fields

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import com.example.goodroad.ui.theme.*
import com.example.goodroad.validation.PHONE_MAX_LENGTH

@Composable
fun PhoneField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    warning: String? = null,
    maxLength: Int = PHONE_MAX_LENGTH
) {
    PlainField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        icon = {
            Icon(Icons.Default.Phone, contentDescription = null, tint = UrbanBrown)
        },
        warning = warning,
        maxLength = maxLength,
        prefix = { androidx.compose.material3.Text("+") }
    )
}