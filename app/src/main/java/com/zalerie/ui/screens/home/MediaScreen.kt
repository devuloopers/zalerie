package com.zalerie.ui.screens.home

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.google.firebase.auth.FirebaseAuth
import com.zalerie.R
import com.zalerie.donwloadManager.downloadMedia
import com.zalerie.models.MediaItems
import com.zalerie.models.UploadViewModel
import com.zalerie.ui.components.CustomButton2
import com.zalerie.ui.dialog.DialogWindowState
import com.zalerie.viewmodel.MediaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun MediaScreen(modifier: Modifier) {
    val fullScreenMedia =
        remember { mutableStateOf<MediaItems?>(null) }
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, end = 10.dp, start = 10.dp)
        ) {
            TopContent(modifier = Modifier)
            Spacer(modifier = Modifier.height(20.dp))
            MediaGridView {
                fullScreenMedia.value = it
            }
        }
        fullScreenMedia.value?.let { media ->
            FullScreenMediaView(
                media = media,
                onDismiss = { fullScreenMedia.value = null }
            )
        }
    }
}

@Composable
fun TopContent(
    modifier: Modifier,
    viewModel: UploadViewModel = koinViewModel(),
    dialogWindowState: DialogWindowState = koinInject(),
    firebaseAuth: FirebaseAuth = koinInject()
) {
    val userId = firebaseAuth.currentUser?.uid!!
    var selectedFiles by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var buttonText by remember { mutableStateOf("") }
    val context = LocalContext.current
    var showStatus by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var hideStatusTrigger: Boolean? by remember { mutableStateOf(null) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) selectedFiles = selectedFiles + uris
    }

    LaunchedEffect(hideStatusTrigger) {
        if (hideStatusTrigger == false) {
            delay(5000)
            showStatus = false
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Your uploads",
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            color = Color.White,
            letterSpacing = 2.sp
        )

        Row {
            IconButton(onClick = {
                filePicker.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageAndVideo
                    )
                )
            }) {
                Icon(
                    modifier = Modifier
                        .size(30.dp),
                    imageVector = Icons.Default.Add,
                    tint = Color.White,
                    contentDescription = "Add"
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            IconButton(onClick = {
                println("Logout dialog pressed")
                dialogWindowState.showDialogAction()
            }) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(R.drawable.logout),
                    tint = Color.Red,
                    contentDescription = "logout"
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
    AnimatedVisibility(visible = selectedFiles.isNotEmpty()) {
        buttonText = "Upload ${selectedFiles.size} content"
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomButton2(buttonText = buttonText) {
                resultMessage = "Uploading..."
                showStatus = true
                viewModel.uploadFiles(context, userId, selectedFiles) { success, uploadedFiles ->
                    if (success) {
                        selectedFiles = emptyList()
                        resultMessage = if (uploadedFiles.size == 1) {
                            "File uploaded successfully"
                        } else {
                            "${uploadedFiles.size} files uploaded successfully"
                        }
                        hideStatusTrigger = false
                    } else {
                        resultMessage = "Upload failed"
                        hideStatusTrigger = false
                    }
                }
                selectedFiles = emptyList()
            }
            IconButton(onClick = { selectedFiles = emptyList() }) {
                Icon(
                    modifier = Modifier
                        .size(30.dp),
                    imageVector = Icons.Default.Delete,
                    tint = Color.Red,
                    contentDescription = "clear selected list"
                )
            }
        }
    }

    ResultMessage(message = resultMessage, show = showStatus)
}

@Composable
fun MediaGridView(
    viewModel: MediaViewModel = koinViewModel(),
    onMediaClick: (MediaItems) -> Unit
) {
    val lazyPagingItems = viewModel.mediaPager.collectAsLazyPagingItems()
    val selectedMedia = remember { mutableStateListOf<MediaItems>() }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(3)
        ) {
            items(lazyPagingItems.itemCount, key = { index ->
                lazyPagingItems.peek(index)?.id
                    ?: index.toString()
            }) { index ->
                val media = lazyPagingItems[index]
                media?.let {
                    SingleMediaItem(
                        media = it,
                        isSelected = selectedMedia.any { selected -> selected.id == it.id },
                        onClick = {
                            when {
                                selectedMedia.isEmpty() -> onMediaClick(it)
                                selectedMedia.any { selected -> selected.id == it.id } -> {
                                    selectedMedia.removeAll { selected -> selected.id == it.id }
                                }

                                else -> selectedMedia.add(it)
                            }
                        },
                        onLongClick = { selected ->
                            if (selected && selectedMedia.none { item -> item.id == it.id }) {
                                selectedMedia.add(it)
                            }
                        }
                    )
                }
            }
        }
        if (selectedMedia.isNotEmpty()) {
            Button(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    viewModel.deleteSelectedMedia(selectedMedia)
                    selectedMedia.clear()
                    lazyPagingItems.refresh()
                }
            ) {
                Text("Delete Selected (${selectedMedia.size})")
            }
        }
    }
}

@Composable
fun SingleMediaItem(
    media: MediaItems,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: (Boolean) -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, if (isSelected) Color.Red else Color.Transparent)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongClick(!isSelected) },
                    onTap = { onClick() }
                )
            }
    ) {
        val url = media.thumbnailUrl ?: media.downloadUrl
        println("Async url - $url")
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(url)
                .placeholder(drawableResId = R.drawable.image_placeholder)
                .error(drawableResId = R.drawable.error_placeholder)
                .crossfade(true)
                .build(),
            contentDescription = "Media Item",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = media.mediaCategoryType.contains("videos")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(0.4f),
                    contentScale = ContentScale.Fit,
                    painter = painterResource(id = R.drawable.videoplay_icon),
                    contentDescription = "VideoPlayBack"
                )
            }
        }
    }
}

@Composable
fun ResultMessage(message: String, show: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = show,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF8C00))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
private fun TestComponent() {
    MediaScreen(modifier = Modifier)
}