package com.zalerie.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomButton1(
    modifier: Modifier = Modifier,
    buttonText: String = "LOGIN",
    fontSize: TextUnit = 15.sp,
    innerPaddingValues: PaddingValues = PaddingValues(),
    letterSpacing: TextUnit = TextUnit.Unspecified,
    animateChangeBorderWidth: Dp = 1.dp,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = animateChangeBorderWidth, color = Color.White),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent
        ),
        onClick = onClick
    ) {
        Text(
            modifier = Modifier.padding(innerPaddingValues),
            text = buttonText,
            color = Color.White,
            fontSize = fontSize,
            letterSpacing = letterSpacing
        )
    }
}