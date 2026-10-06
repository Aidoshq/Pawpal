package com.example.pawpal

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object LocalStorage {

    private const val PREFS_NAME = "pawpal_storage"
    private const val PETS_KEY = "pets"
    private const val TASKS_KEY = "tasks"

    // =========================
    // PETS
    // =========================

    fun savePets(
        context: Context,
        pets: List<Pet>
    ) {
        val jsonArray = JSONArray()

        pets.forEach { pet ->
            val jsonObject = JSONObject()

            jsonObject.put("id", pet.id)
            jsonObject.put("name", pet.name)
            jsonObject.put("type", pet.type)
            jsonObject.put("age", pet.age)
            jsonObject.put("breed", pet.breed)

            jsonArray.put(jsonObject)
        }

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        preferences
            .edit()
            .putString(
                PETS_KEY,
                jsonArray.toString()
            )
            .apply()
    }

    fun loadPets(
        context: Context
    ): List<Pet>? {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        if (!preferences.contains(PETS_KEY)) {
            return null
        }

        val json =
            preferences.getString(
                PETS_KEY,
                null
            ) ?: return null

        val petList =
            mutableListOf<Pet>()

        val jsonArray =
            JSONArray(json)

        for (i in 0 until jsonArray.length()) {

            val item =
                jsonArray.getJSONObject(i)

            val pet = Pet(
                id = item.getInt("id"),
                name = item.getString("name"),
                type = item.getString("type"),
                age = item.getString("age"),
                breed = item.getString("breed")
            )

            petList.add(pet)
        }

        return petList
    }

    // =========================
    // CARE TASKS
    // =========================

    fun saveTasks(
        context: Context,
        tasks: List<CareTask>
    ) {
        val jsonArray = JSONArray()

        tasks.forEach { task ->
            val jsonObject = JSONObject()

            jsonObject.put("id", task.id)
            jsonObject.put("petId", task.petId)
            jsonObject.put("title", task.title)
            jsonObject.put("time", task.time)
            jsonObject.put("notes", task.notes)
            jsonObject.put(
                "isCompleted",
                task.isCompleted
            )

            jsonArray.put(jsonObject)
        }

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        preferences
            .edit()
            .putString(
                TASKS_KEY,
                jsonArray.toString()
            )
            .apply()
    }

    fun loadTasks(
        context: Context
    ): List<CareTask>? {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        if (!preferences.contains(TASKS_KEY)) {
            return null
        }

        val json =
            preferences.getString(
                TASKS_KEY,
                null
            ) ?: return null

        val taskList =
            mutableListOf<CareTask>()

        val jsonArray =
            JSONArray(json)

        for (i in 0 until jsonArray.length()) {

            val item =
                jsonArray.getJSONObject(i)

            val task = CareTask(
                id = item.getInt("id"),
                petId = item.getInt("petId"),
                title = item.getString("title"),
                time = item.getString("time"),
                notes = item.getString("notes"),
                isCompleted =
                    item.getBoolean("isCompleted")
            )

            taskList.add(task)
        }

        return taskList
    }
}