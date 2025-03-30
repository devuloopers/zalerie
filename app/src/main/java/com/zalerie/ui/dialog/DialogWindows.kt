package com.zalerie.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.zalerie.ui.screens.home.LogoutDialog
import org.koin.compose.koinInject

@Composable
fun DialogWindow(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    dialogWindowState: DialogWindowState = koinInject()
) {
    val show by remember { derivedStateOf { dialogWindowState.showDialog } }

    println("Test dialog")

    if (show) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f))
                    .fillMaxSize()
            )
            LogoutDialog(navController = navHostController) {
                dialogWindowState.hideDialog()
            }
        }
    }
}