package com.zalerie.firebase

import android.content.Context
import android.net.Uri
import com.zalerie.models.MediaFile

interface FirebaseStorageRepository {
    suspend fun uploadFiles(
        context: Context,
        userId: String,
        files: List<Uri>,
        onResult: (Boolean, List<MediaFile>) -> Unit
    )
}