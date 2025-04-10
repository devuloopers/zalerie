package com.zalerie.repository.firebase

import android.content.Context
import android.net.Uri
import com.zalerie.models.MediaItems

interface FirebaseStorageRepository {
    suspend fun uploadFiles(
        context: Context,
        userId: String,
        files: List<Uri>,
        onResult: (Boolean, List<MediaItems>) -> Unit
    )
}