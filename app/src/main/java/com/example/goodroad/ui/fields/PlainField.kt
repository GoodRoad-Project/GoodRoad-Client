package com.example.goodroad.ui.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import com.example.goodroad.ui.theme.*

@Composable
fun PlainField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    icon: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    warning: String? = null,
    maxLength: Int = Int.MAX_VALUE
) {
    TextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= maxLength) {
                onValueChange(newValue)
            }
        },
        modifier = modifier.fillMaxWidth(),
        label = { Text(label, color = UrbanBrown) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        leadingIcon = icon,
        trailingIcon = trailing,
        prefix = prefix,
        isError = warning != null,
        supportingText = {
            warning?.let {
                Text(it, color = AlertRed)
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = BackgroundLight,
            unfocusedContainerColor = BackgroundLight,
            cursorColor = SafeGreen
        ),
        shape = RoundedCornerShape(18.dp)
    )
}