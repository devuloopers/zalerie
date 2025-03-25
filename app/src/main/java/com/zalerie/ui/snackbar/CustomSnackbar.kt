package com.zalerie.ui.snackbar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CustomSnackbar(modifier: Modifier = Modifier, snackbarData: SnackbarData) {
    Snackbar(
        modifier = modifier.fillMaxWidth(),
        snackbarData = snackbarData,
        containerColor = Color(0xFFEF0014),
        contentColor = Color.White
    )
}