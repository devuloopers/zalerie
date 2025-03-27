package com.zalerie.ui.screens.home

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.zalerie.appPermissions.PermissionDeniedContent
import com.zalerie.appPermissions.PermissionPermanentlyDeniedContent
import com.zalerie.appPermissions.PermissionsHandler
import com.zalerie.viewmodel.PermissionState
import com.zalerie.appPermissions.PermissionUtils
import com.zalerie.models.UploadViewModel
import com.zalerie.viewmodel.PermissionsViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    permissionViewModel: PermissionsViewModel = koinViewModel()
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
        UploadScreen()
    }
}

@Composable
fun UploadScreen(viewModel: UploadViewModel = koinViewModel()) {
    val firebaseAuth: FirebaseAuth = koinInject()
    val userId = firebaseAuth.currentUser?.uid!! // Replace with FirebaseAuth UID
    var selectedFiles by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var uploadStatus by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        selectedFiles = uris
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { filePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) }) {
            Text("Select Images/Videos")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            if (selectedFiles.isNotEmpty()) {
                uploadStatus = "Uploading..."
                viewModel.uploadFiles(context, userId, selectedFiles) { success, uploadedFiles ->
                    uploadStatus =
                        if (success) "Upload Successful: ${uploadedFiles.size} files" else "Upload Failed"
                }
            }
        }) {
            Text("Upload")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = uploadStatus)
    }
}