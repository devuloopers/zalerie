package com.zalerie.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField() {

}

@Composable
fun TextFieldEndTrailingIcon(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    textValue: String = "",
    cornerRadius: Dp = 8.dp,
    maxLines: Int = Int.MAX_VALUE,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    icon: @Composable (() -> Unit) = {},
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = textValue,
        onValueChange = { newText ->
            onValueChange(newText)
        },
        placeholder = {
            Text(
                text = if (textValue.isEmpty()) placeholder else "",
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.5f)
            )
        },
        trailingIcon = {
            icon()
        },
        modifier = modifier,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        colors = colors,
        shape = RoundedCornerShape(cornerRadius)
    )
}