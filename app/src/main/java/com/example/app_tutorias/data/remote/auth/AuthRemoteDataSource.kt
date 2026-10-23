package com.example.app_tutorias.data.remote.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRemoteDataSource(
    private val auth: FirebaseAuth
) {
    val currentUserId: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth -> trySend(firebaseAuth.currentUser?.uid) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun register(
        email: String,
        password: String
    ): String {
        val result = auth
            .createUserWithEmailAndPassword(email, password)
            .await()
        return requireNotNull(result.user?.uid) { "Firebase no devolvió el identificador del usuario" }
    }

    suspend fun signIn(
        email: String,
        password: String
    ) { auth.signInWithEmailAndPassword(email, password).await() }

    fun signOut() { auth.signOut() }

    fun requireCurrentUserId(): String = requireNotNull(auth.currentUser?.uid) { "No existe una sesión de usuario activa" }
}