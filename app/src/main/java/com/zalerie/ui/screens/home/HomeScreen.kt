package com.zalerie.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.zalerie.ui.screens.Screens

@Composable
fun HomeScreen(
    modifier: Modifier,
    navHostController: NavHostController
) {
    DisposableEffect(Unit) {
        onDispose {
            navHostController.popBackStack(Screens.AuthScreen, inclusive = true)
        }
    }
    Column(modifier = modifier
        .fillMaxSize()
        .background(Color.Yellow)) {

    }
}