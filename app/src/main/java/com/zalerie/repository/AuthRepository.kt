package com.zalerie.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(private val firebaseAuth: FirebaseAuth) {

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    suspend fun signUp(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { AuthResult.Success(it) } ?: AuthResult.Failure("Unknown error occurred")
        } catch (e: FirebaseAuthException) {
            AuthResult.Failure(e.localizedMessage ?: "Error")
        } catch (e: Exception) {
            AuthResult.Failure(e.localizedMessage ?: "Signup failed")
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { AuthResult.Success(it) }
                ?: AuthResult.Failure("Unknown error occurred")
        } catch (e: Exception) {
            AuthResult.Failure(e.localizedMessage ?: "Sign-in failed")
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}

sealed class AuthResult {
    data class Success(val user: FirebaseUser) : AuthResult()
    data class Failure(val error: String) : AuthResult()
}