package com.zalerie.ui.screens.home

import android.graphics.drawable.ColorDrawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.zalerie.R
import com.zalerie.donwloadManager.downloadMedia
import com.zalerie.models.MediaItems

@Composable
fun FullScreenMediaView(media: MediaItems, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var showMetaData by remember { mutableStateOf(true) }
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "zoomAnimation"
    )
    val animatedOffset by animateOffsetAsState(
        targetValue = offset,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "offsetAnimation"
    )

    fun calculateMaxOffset(): Offset {
        return if (scale <= 1f) Offset.Zero else {
            val maxOffset = (scale - 1) * 200f // Adjust multiplier as needed
            Offset(maxOffset, maxOffset)
        }
    }
    BackHandler {
        if (scale != 1f || offset != Offset.Zero) {
            scale = 1f
            offset = Offset.Zero
            rotation = 0f
        } else {
            onDismiss()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = if (scale == 1f) 2f else 1f
                        offset = Offset.Zero
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (media.mediaCategoryType.contains("videos")) {
                VideoPlayer(
                    videoUrl = media.downloadUrl,
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    showMetaData = !showMetaData
                                }
                            )
                        })
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(media.thumbnailUrl ?: media.downloadUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Expanded Media",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .graphicsLayer(
                            scaleX = animatedScale,
                            scaleY = animatedScale,
                            translationX = animatedOffset.x,
                            translationY = animatedOffset.y,
                            rotationZ = rotation
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    showMetaData = !showMetaData
                                }
                            )
                            detectTransformGestures(
                                onGesture = { _, pan, gestureZoom, gestureRotate ->
                                    // Handle zoom
                                    val newScale = (scale * gestureZoom).coerceIn(1f, 5f)
                                    scale = newScale

                                    // Only allow panning if zoomed
                                    if (newScale > 1f) {
                                        val maxOffset = calculateMaxOffset()
                                        offset = Offset(
                                            (offset.x + pan.x).coerceIn(-maxOffset.x, maxOffset.x),
                                            (offset.y + pan.y).coerceIn(-maxOffset.y, maxOffset.y)
                                        )
                                    } else {
                                        offset = Offset.Zero
                                    }

                                    // Optional: handle rotation if needed
                                    rotation += gestureRotate
                                }
                            )
                        }
                )
            }
            //Metadata
            AnimatedVisibility(visible = showMetaData) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    MetadataItems(title = "Name", data = media.name)
                    Spacer(modifier = Modifier.height(5.dp))
                    MetadataItems(title = "Upload Date", data = media.date)
                    Spacer(modifier = Modifier.height(5.dp))
                    MetadataItems(title = "Upload Time", data = media.time)
                    Spacer(modifier = Modifier.height(5.dp))
                    MetadataItems(title = "Size", data = media.fileSize + " MB")
                }
            }
        }
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }

        IconButton(
            onClick = {
                downloadMedia(
                    context = context,
                    url = media.downloadUrl,
                    mediaType = media.mediaCategoryType
                )
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                painter = painterResource(R.drawable.download),
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }
}

@Composable
fun MetadataItems(title: String = "", data: String = "") {
    Row {
        Text(
            text = "$title - ",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(text = data, fontSize = 15.sp, fontWeight = FontWeight.Normal, color = Color.White)
    }
}