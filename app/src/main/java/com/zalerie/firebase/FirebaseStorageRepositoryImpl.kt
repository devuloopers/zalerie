package com.zalerie.firebase

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.zalerie.models.MediaFile
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
        onResult: (Boolean, List<MediaFile>) -> Unit
    ) {
        //val mediaList = mutableListOf<MediaFile>()
        val scope = CoroutineScope(Dispatchers.IO)

        val deferredUploads = files.map { uri ->
            scope.async {
                val fileType = getFileType(context = context, uri = uri)
                val fileName = "${System.currentTimeMillis()}_${fileType}"
                val storageRef = firebaseStorage.reference
                    .child("users/$userId/$fileName")

                try {
                    val uploadTask = storageRef.putFile(uri).await()
                    val downloadUrl = storageRef.downloadUrl.await().toString()

                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val dateTime = sdf.format(Date()).split(" ")

                    val mediaFile = MediaFile(
                        id = fileName,
                        name = fileName,
                        fileType = fileType,
                        date = dateTime[0],
                        time = dateTime[1],
                        downloadUrl = downloadUrl
                    )

                    firestore.collection("users").document(userId)
                        .collection("uploads")
                        .document(fileName)
                        .set(mediaFile)
                        .await()

                    mediaFile
                } catch (e: Exception) {
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

fun getFileType(context: Context, uri: Uri): String {
    val contentResolver = context.contentResolver
    val type = contentResolver.getType(uri)

    return when {
        type?.startsWith("image") == true -> "image"
        type?.startsWith("video") == true -> "video"
        else -> "unknown"
    }
}