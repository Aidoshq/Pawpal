package com.example.pawpal

interface AuthRepository {

    val currentUserId: String?

    suspend fun signIn(email: String, password: String)

    suspend fun signUp(email: String, password: String)

    fun signOut()
}