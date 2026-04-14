package com.example.goodroad.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.*
import com.example.goodroad.ui.theme.*
import androidx.compose.material.icons.filled.*
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