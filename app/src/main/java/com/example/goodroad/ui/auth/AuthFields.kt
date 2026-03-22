package com.example.goodroad.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.theme.AlertRed
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.BorderWarm
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.TextSecondary
import com.example.goodroad.ui.theme.UrbanBrown

@Composable
fun PhoneField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    warning: String? = null
) {
    PlainField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        icon = {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                tint = UrbanBrown
            )
        },
        warning = warning,
        prefix = {
            Text(
                text = "+",
                color = TextSecondary
            )
        }
    )
}

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    var visible by remember { mutableStateOf(false) }

    PlainField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = UrbanBrown
            )
        },
        trailing = {
            Icon(
                imageVector = if (visible) {
                    Icons.Default.VisibilityOff
                } else {
                    Icons.Default.Visibility
                },
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.clickable { visible = !visible }
            )
        }
    )
}

@Composable
fun PlainField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    icon: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    warning: String? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = label,
                color = UrbanBrown
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        leadingIcon = icon,
        trailingIcon = trailing,
        prefix = prefix,
        isError = warning != null,
        supportingText = {
            if (warning != null) {
                Text(
                    text = warning,
                    style = MaterialTheme.typography.bodySmall,
                    color = AlertRed
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = BackgroundLight,
            unfocusedContainerColor = BackgroundLight,
            disabledContainerColor = BackgroundLight,
            focusedIndicatorColor = if (warning != null) AlertRed else SafeGreen,
            unfocusedIndicatorColor = if (warning != null) AlertRed else BorderWarm,
            cursorColor = SafeGreen,
            focusedLabelColor = UrbanBrown,
            unfocusedLabelColor = UrbanBrown,
            focusedLeadingIconColor = UrbanBrown,
            unfocusedLeadingIconColor = UrbanBrown,
            focusedTrailingIconColor = TextSecondary,
            unfocusedTrailingIconColor = TextSecondary
        ),
        shape = RoundedCornerShape(18.dp)
    )
}