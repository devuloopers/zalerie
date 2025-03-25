package com.zalerie.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.zalerie.repository.AuthRepository
import com.zalerie.repository.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.Timestamp

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _user = MutableStateFlow(authRepository.currentUser)
    val user = _user.asStateFlow()

    private val _errorMessage =
        MutableSharedFlow<String>(extraBufferCapacity = 1)
    val errorMessage = _errorMessage.asSharedFlow()

    private val db = FirebaseFirestore.getInstance()

    suspend fun signUp(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            when (val result = authRepository.signUp(email, password)) {
                is AuthResult.Success -> {
                    _user.value = result.user
                    true
                }
                is AuthResult.Failure -> {
                    _errorMessage.emit(result.error)
                    false
                }
            }
        }
    }

    suspend fun signIn(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            when (val result = authRepository.signIn(email, password)) {
                is AuthResult.Success -> {
                    _user.value = result.user
                    true
                }
                is AuthResult.Failure -> {
                    _errorMessage.emit(result.error)
                    false
                }
            }
        }
    }
    suspend fun isUserRegistered(userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val userDocRef = db.collection("users").document(userId)
                val document = userDocRef.get().await()
                document.exists()
            } catch (e: Exception) {
                Log.e("Firestore", "Error checking user registration", e)
                false
            }
        }
    }

    suspend fun completeUserRegistration(userId: String, userData: UserData): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val documentPath = db.collection("users").document(userId)
                documentPath.set(userData, SetOptions.merge()).await()
                true
            } catch (e: Exception) {
                Log.e("Firestore", "Error completing user registration", e)
                false
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _user.value = null
    }
}

data class UserData(
    val uid: String = "",
    val email: String? = null,
    val name: String? = null,
    val createdAt: String? = null
)