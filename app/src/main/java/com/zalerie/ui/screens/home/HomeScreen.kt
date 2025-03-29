package com.zalerie.ui.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
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
fun HomeScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    permissionViewModel: PermissionsViewModel = koinViewModel(),
    mediaViewModel: MediaViewModel = koinViewModel()
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
    val userId = firebaseAuth.currentUser?.uid!!
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
    Spacer(modifier = Modifier.fillMaxHeight(0.1f))
    MediaGrid()
}

@Composable
fun MediaGrid(viewModel: MediaViewModel = koinViewModel()) {
    val lazyPagingItems = viewModel.mediaPager.collectAsLazyPagingItems()
    val selectedMedia = remember { mutableStateListOf<MediaItems>() }

    Column {
        LazyVerticalGrid(columns = GridCells.Fixed(3)) {
            items(lazyPagingItems.itemCount) { index ->
                val media = lazyPagingItems[index]
                media?.let {
                    MediaItem(
                        media = it,
                        isSelected = selectedMedia.contains(it),
                        onSelectionChange = { selected ->
                            if (selected) selectedMedia.add(it) else selectedMedia.remove(it)
                        }
                    )
                }
            }
        }

        Button(onClick = { viewModel.deleteSelectedMedia(selectedMedia) }) {
            Text("Delete Selected")
        }
    }
}

@Composable
fun MediaItem(
    media: MediaItems,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, if (isSelected) Color.Red else Color.Gray)
            .clickable { onSelectionChange(!isSelected) }
    ) {
        val url = media.thumbnailUrl ?: media.downloadUrl
        println("Async url - $url")
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(url)
                .crossfade(true)
                .build(),
            contentDescription = "Media Item",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(100.dp)
        )
    }
}
