package com.zalerie.appPermissions

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.zalerie.viewmodel.PermissionsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@Composable
fun PermissionsHandler(
    permissionViewModel: PermissionsViewModel = koinViewModel()
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

    SideEffect {
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
    viewModel { PermissionsViewModel(get()) }
}

