package com.zalerie.repository

import androidx.paging.PagingSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.zalerie.dao.MediaDao
import com.zalerie.models.MediaItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediaRepository(
    private val mediaDao: MediaDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
) {

    private var firestoreListener: ListenerRegistration? = null
    fun getPagedMedia(): PagingSource<Int, MediaItems> = mediaDao.getPagedMedia()

    suspend fun syncMedia() {
        val lastTimestamp = mediaDao.getLastTimestamp() ?: 0L
        firebaseAuth.currentUser?.uid?.let { uid ->
            firestore.collection("users")
                .document(uid)
                .collection("uploads")
                .whereGreaterThan("timestamp", lastTimestamp)
                .get()
                .addOnSuccessListener { snapshot ->
                    val mediaList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(MediaItems::class.java)?.copy(id = doc.id)
                    }
                    if (mediaList.isNotEmpty()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            mediaDao.insertAll(mediaList)
                        }
                    }
                    mediaList.forEach {
                        println("Sync media - $it")
                    }
                }.addOnFailureListener { e ->
                    println("Error fetching from firestore: ${e.message}")
                }
        }
    }

    fun startFirestoreListener(onNewData: (List<MediaItems>) -> Unit) {
        val uid = firebaseAuth.currentUser?.uid ?: return

        firestoreListener = firestore.collection("users")
            .document(uid)
            .collection("uploads")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    val newMedia = snapshots.toObjects(MediaItems::class.java)
                    onNewData(newMedia)
                }
            }
    }

    suspend fun insertMediaToRoom(mediaList: List<MediaItems>) {
        mediaDao.insertAll(mediaList)
        mediaList.forEach {
            println("New media List $it")
        }
    }

    fun stopFirestoreListener() {
        firestoreListener?.remove()
    }

    fun deleteMedia(selectedMedia: List<MediaItems>) {
        val ids = selectedMedia.map { it.id }
        firebaseAuth.currentUser?.uid?.let { uid ->
            firestore.runBatch { batch ->
                selectedMedia.forEach { mediaItem ->
                    val mediaRef = firebaseStorage.reference
                        .child("users/$uid/${mediaItem.mediaCategoryType}/${mediaItem.id}")

                    val thumbnailRef = if (mediaItem.mediaCategoryType.startsWith("video")) {
                        firebaseStorage.reference.child("users/$uid/videos/thumbnails/${mediaItem.id}.jpg")
                    } else null

                    // Delete metadata from Firestore
                    batch.delete(
                        firestore.collection("users").document(uid)
                            .collection("uploads").document(mediaItem.id)
                    )
                    println("Firestore Delete id - ${mediaItem.id}")

                    // Delete file from Firebase Storage
                    mediaRef.delete().addOnSuccessListener {
                        println("Firebase Storage Deleted: ${mediaItem.id}")
                    }.addOnFailureListener {
                        println("Error deleting from Storage: ${mediaItem.id}")
                    }

                    // Delete video thumbnail
                    thumbnailRef?.delete()?.addOnSuccessListener {
                        println("Deleted thumbnail: ${mediaItem.id}.jpg")
                    }?.addOnFailureListener {
                        println("Error deleting thumbnail: ${mediaItem.id}.jpg")
                    }
                }
            }.addOnSuccessListener {
                // Delete from RoomDB after Firestore deletion
                CoroutineScope(Dispatchers.IO).launch {
                    mediaDao.deleteMedia(ids)
                    ids.forEach {
                        println("RoomDB Delete id - $it")
                    }
                }
            }
        }
    }
}