package com.zalerie.ui.screens.home

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.zalerie.appPermissions.PermissionDeniedContent
import com.zalerie.appPermissions.PermissionPermanentlyDeniedContent
import com.zalerie.appPermissions.PermissionsHandler
import com.zalerie.appPermissions.PermissionState
import com.zalerie.appPermissions.PermissionUtils
import com.zalerie.appPermissions.PermissionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    permissionViewModel: PermissionViewModel = koinViewModel()
) {
    val permissionState by permissionViewModel.permissionState.collectAsState()
    val context = LocalContext.current

    when (permissionState) {
        PermissionState.Granted -> ActualHomeScreen(modifier, navHostController)
        PermissionState.Denied -> PermissionDeniedContent()
        PermissionState.PermanentlyDenied -> PermissionPermanentlyDeniedContent()
        PermissionState.Idle -> PermissionsHandler()
    }

    LaunchedEffect(Unit) {
        while (true) {
            if (PermissionUtils.hasPermissions(context)) {
                permissionViewModel.onPermissionGranted()
                break
            }
            // Checking every 0.3 Sec for permission from settings
            delay(300)
        }
    }
}

@Composable
private fun ActualHomeScreen(
    modifier: Modifier,
    navHostController: NavHostController
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Yellow)
    ) {

    }
}