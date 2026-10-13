package com.example.pawpal

import kotlinx.coroutines.delay

class PetRepository {

    var shouldFail = false

    suspend fun getPets(): List<Pet> {
        delay(800)

        if (shouldFail) {
            throw Exception("Failed to load pets")
        }

        return emptyList()
    }
}