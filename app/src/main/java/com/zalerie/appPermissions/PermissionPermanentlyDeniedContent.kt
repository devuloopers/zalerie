package com.zalerie.appPermissions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zalerie.viewmodel.PermissionsViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun PermissionPermanentlyDeniedContent() {
    val context = LocalContext.current
    val permissionViewModel: PermissionsViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        while (true) {
            permissionViewModel.verifyPermissionsFromSettings(context)
            delay(800)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.5f))
                .fillMaxSize()
        )
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enable permission to access Images and video from settings",
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF0014)
                ),
                onClick = {
                    PermissionUtils.openAppSettings(context)
                    permissionViewModel.verifyPermissionsFromSettings(context)
                }
            ) {
                Text(text = "Go to settings", color = Color.White)
            }
        }
    }
}