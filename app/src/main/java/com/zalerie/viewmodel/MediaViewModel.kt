package com.zalerie.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.zalerie.models.MediaItems
import com.zalerie.repository.MediaRepository
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class MediaViewModel(private val repository: MediaRepository) : ViewModel() {

    val mediaPager = Pager(
        config = PagingConfig(pageSize = 30, enablePlaceholders = false),
        pagingSourceFactory = { repository.getPagedMedia() }
    ).flow.cachedIn(viewModelScope)

    fun syncMedia() {
        viewModelScope.launch {
            repository.syncMedia()
        }
    }

    fun startRealTimeUpdates() {
        repository.startFirestoreListener { newMedia ->
            viewModelScope.launch {
                repository.insertMediaToRoom(newMedia)
            }
        }
    }

    fun stopRealTimeUpdates() {
        repository.stopFirestoreListener()
    }

    fun deleteSelectedMedia(selectedMedia: List<MediaItems>) {
        viewModelScope.launch {
            repository.deleteMedia(selectedMedia)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logoutAction()
        }
    }
}

fun extractThumbnail(context: Context, videoUri: Uri): ByteArray? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, videoUri)
        val bitmap = retriever.getFrameAtTime(1000000) //1 sec
        bitmap?.let {
            val outputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.toByteArray()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        retriever.release()
    }
}