package com.zalerie.models

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalerie.firebase.FirebaseStorageRepository
import kotlinx.coroutines.launch

class UploadViewModel(private val repository: FirebaseStorageRepository) : ViewModel() {

    fun uploadFiles(context: Context, userId: String, files: List<Uri>, onResult: (Boolean, List<MediaFile>) -> Unit) {
        viewModelScope.launch {
            repository.uploadFiles(context, userId, files, onResult)
        }
    }
}