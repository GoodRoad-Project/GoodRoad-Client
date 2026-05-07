package com.example.goodroad.ui.fields

import androidx.compose.foundation.LocalIndication
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.ui.text.input.*
import com.example.goodroad.ui.theme.*
import com.example.goodroad.validation.PASSWORD_MAX_LENGTH

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    maxLength: Int = PASSWORD_MAX_LENGTH
) {
    var visible by remember { mutableStateOf(false) }

    PlainField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        icon = {
            Icon(Icons.Default.Lock, contentDescription = null, tint = UrbanBrown)
        },
        maxLength = maxLength,
        trailing = {
            Icon(
                imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = LocalIndication.current
                ) {
                    visible = !visible
                }
            )
        }
    )
}