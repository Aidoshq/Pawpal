
package com.example.pawpal

import android.content.Context
import kotlinx.coroutines.delay

class PetRepository(
    private val context: Context
) {
    var shouldFail = false

    suspend fun getPets(): List<Pet> {
        delay(800)

        if (shouldFail) {
            throw Exception("Failed to load pets")
        }

        return LocalStorage.loadPets(context) ?: pets
    }

    suspend fun savePets(pets: List<Pet>) {
        LocalStorage.savePets(context, pets)
    }

    suspend fun getTasks(): List<CareTask> {
        delay(300)
        return LocalStorage.loadTasks(context) ?: careTasks
    }

    suspend fun saveTasks(tasks: List<CareTask>) {
        LocalStorage.saveTasks(context, tasks)
    }
}
