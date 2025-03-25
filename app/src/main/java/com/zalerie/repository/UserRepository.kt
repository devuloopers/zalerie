package com.zalerie.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.zalerie.viewmodel.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository(private val db: FirebaseFirestore) {

    suspend fun isUserRegistered(userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val document = db.collection("users").document(userId).get().await()
                document.exists()
            } catch (e: Exception) {
                Log.e("Firestore", "Error checking user registration", e)
                false
            }
        }
    }

    suspend fun registerUser(userId: String, userData: UserData): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val document = db.collection("users").document(userId)
                document.set(userData, SetOptions.merge()).await()
                true
            } catch (e: Exception) {
                Log.e("Firestore", "Error registering user", e)
                false
            }
        }
    }
}