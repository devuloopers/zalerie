package com.zalerie.repository.firebase

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.zalerie.models.MediaItems
import com.zalerie.viewmodel.extractThumbnail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirebaseStorageRepositoryImpl(
    private val firebaseStorage: FirebaseStorage,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FirebaseStorageRepository {

    override suspend fun uploadFiles(
        context: Context,
        userId: String,
        files: List<Uri>,
        onResult: (Boolean, List<MediaItems>) -> Unit
    ) {
        val scope = CoroutineScope(Dispatchers.IO)

        val deferredUploads = files.map { uri ->
            scope.async {
                val fileCategory = getFileType(context, uri)
                val timestamp = System.currentTimeMillis()
                val fileID = "${timestamp}_$fileCategory"
                val originalFileName = getFileName(context = context, uri = uri)
                println("File Name - $originalFileName")

                val folder = if (fileCategory.startsWith("image")) "images" else "videos"
                val storageRef =
                    firebaseStorage.reference.child("users/$userId/$folder/$fileID")

                try {
                    val thumbnailUrl = if (fileCategory.startsWith("video")) {
                        uploadVideoThumbnail(context, firebaseStorage, userId, fileID, uri)
                    } else {
                        null
                    }

                    storageRef.putFile(uri).await()
                    val downloadUrl = storageRef.downloadUrl.await().toString()
                    val fileSize = storageRef.metadata.await().sizeBytes

                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val dateTime = sdf.format(Date()).split(" ")

                    val mediaFile = MediaItems(
                        id = fileID,
                        name = originalFileName ?: "unknown",
                        mediaCategoryType = fileCategory,
                        date = dateTime[0],
                        time = dateTime[1],
                        downloadUrl = downloadUrl,
                        thumbnailUrl = thumbnailUrl,
                        timestamp = timestamp,
                        fileSize = bytesToMB(fileSize)
                    )
                    firestore.collection("users").document(userId)
                        .collection("uploads").document(fileID)
                        .set(mediaFile)
                        .await()

                    mediaFile
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        val results = deferredUploads.mapNotNull { it.await() }
        withContext(Dispatchers.Main) {
            onResult(results.isNotEmpty(), results)
        }
    }
}

suspend fun uploadVideoThumbnail(
    context: Context,
    firebaseStorage: FirebaseStorage,
    userId: String,
    fileName: String,
    uri: Uri
): String? {
    val thumbnailData = extractThumbnail(context, uri) ?: return null
    val thumbnailRef = firebaseStorage.reference
        .child("users/$userId/videos/thumbnails/$fileName.jpg")
    return try {
        thumbnailRef.putBytes(thumbnailData).await()
        thumbnailRef.downloadUrl.await().toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getFileType(context: Context, uri: Uri): String {
    val contentResolver = context.contentResolver
    val type = contentResolver.getType(uri)
    return when {
        type?.startsWith("image") == true -> "images"
        type?.startsWith("video") == true -> "videos"
        else -> "unknown"
    }
}

fun getFileName(context: Context, uri: Uri): String? {
    var fileName: String? = null
    val cursor = context.contentResolver.query(
        uri, null, null, null, null
    )
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1) {
            it.moveToFirst()
            fileName = it.getString(nameIndex)
        }
    }
    return fileName
}

fun bytesToMB(bytes: Long): String {
    val mb = bytes / (1024.0 * 1024.0) // Convert bytes to MB
    return "%.2f".format(mb) // Format to 2 decimal places
}