package com.example.pawpal

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUserId: String?
        get() = auth.currentUser?.uid

    override suspend fun signIn(
        email: String,
        password: String
    ) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun signUp(
        email: String,
        password: String
    ) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    override fun signOut() {
        auth.signOut()
    }
}