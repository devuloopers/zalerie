package com.zalerie.ui.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.firebase.auth.FirebaseAuth
import com.zalerie.appPermissions.PermissionDeniedContent
import com.zalerie.appPermissions.PermissionPermanentlyDeniedContent
import com.zalerie.appPermissions.PermissionUtils
import com.zalerie.appPermissions.PermissionsHandler
import com.zalerie.models.MediaItems
import com.zalerie.models.UploadViewModel
import com.zalerie.viewmodel.MediaViewModel
import com.zalerie.viewmodel.PermissionState
import com.zalerie.viewmodel.PermissionsViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun InitialHomeScreenSetup(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    permissionViewModel: PermissionsViewModel = koinViewModel(),
    mediaViewModel: MediaViewModel = koinViewModel()
) {
    val permissionState by permissionViewModel.permissionState.collectAsState()
    val context = LocalContext.current

    when (permissionState) {
        PermissionState.Granted -> ActualHomeScreen(modifier)
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
    LaunchedEffect(Unit) {
        mediaViewModel.syncMedia()
    }
    DisposableEffect(Unit) {
        mediaViewModel.startRealTimeUpdates()

        onDispose {
            mediaViewModel.stopRealTimeUpdates()
        }
    }
}

@Composable
private fun ActualHomeScreen(
    modifier: Modifier
) {
    MediaScreen(modifier = modifier)
}


