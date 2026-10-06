package com.example.pawpal

data class CareTask(
    val id: Int,
    val petId: Int,
    val title: String,
    val time: String,
    val notes: String = "",
    val isCompleted: Boolean = false
)

val careTasks = listOf(
    CareTask(
        id = 1,
        petId = 1,
        title = "Morning feeding",
        time = "8:00 AM",
        isCompleted = true
    ),
    CareTask(
        id = 2,
        petId = 1,
        title = "Evening walk",
        time = "7:00 PM"
    ),
    CareTask(
        id = 3,
        petId = 1,
        title = "Give vitamins",
        time = "9:00 PM",
        notes = "Give after dinner"
    ),
    CareTask(
        id = 4,
        petId = 2,
        title = "Morning feeding",
        time = "9:00 AM"
    ),
    CareTask(
        id = 5,
        petId = 3,
        title = "Clean cage",
        time = "6:00 PM"
    )
)