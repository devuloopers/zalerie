package com.zalerie.ui.loadingBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun LoadingBar(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFEF0014),
    service: LoadingBarState = koinInject()
) {
    val isLoading by remember { derivedStateOf { service.isLoading } }
    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f))
                    .fillMaxSize()
            )
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                CircularProgressIndicator(
                    color = color,
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                Text(text = "Please wait", fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}

@Composable
@Preview
fun TestLoadingBar() {
    LoadingBar()
}