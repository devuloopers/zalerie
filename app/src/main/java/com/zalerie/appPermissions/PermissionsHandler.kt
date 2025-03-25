package com.zalerie.appPermissions

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@Composable
fun PermissionsHandler(
    permissionViewModel: PermissionViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            permissionViewModel.onPermissionGranted()
        } else {
            permissionViewModel.onPermissionDenied()
            scope.launch {
                delay(1000)
                permissionViewModel.verifyPermissionsFromSettings(context)
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!PermissionUtils.hasPermissions(context)) {
            permissionLauncher.launch(PermissionUtils.getRequiredPermissions())
        } else {
            permissionViewModel.onPermissionGranted()
        }
    }
}

val permissionModule = module {
    single {
        androidContext().getSharedPreferences(
            "PermissionPrefs",
            Context.MODE_PRIVATE
        )
    }
    viewModel { PermissionViewModel(get()) }
}

