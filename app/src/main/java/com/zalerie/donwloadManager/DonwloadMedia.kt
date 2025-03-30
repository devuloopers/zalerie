package com.zalerie.donwloadManager

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import java.net.URL
import java.net.URLConnection

fun downloadMedia(context: Context, url: String, mediaType: String) {
    CoroutineScope(Dispatchers.IO).launch {
        val fileName = getFileNameFromUrl(url)

        var mimeType: String? = URLConnection.guessContentTypeFromName(url) //MIME type
        if (mimeType == null) {
            mimeType = if (mediaType.contains("video")) "video/mp4" else "image/jpeg"
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Scoped Storage (Android 10+)
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    if (mediaType.contains("video")) Environment.DIRECTORY_MOVIES else Environment.DIRECTORY_PICTURES
                )
            }

            val contentUri = if (mediaType.contains("video")) {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            try {
                val uri = resolver.insert(contentUri, contentValues)
                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        URL(url).openStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Downloaded to ${contentValues[MediaStore.MediaColumns.RELATIVE_PATH]}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } ?: run {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to download", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error downloading media", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            // For API < 29
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(
                    if (mediaType.contains("video")) Environment.DIRECTORY_MOVIES else Environment.DIRECTORY_PICTURES,
                    fileName
                )
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
            }

            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun getFileNameFromUrl(url: String): String {
    return url.substringAfterLast("/").substringBefore("?")
}