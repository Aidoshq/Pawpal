package com.example.pawpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.pawpal.ui.theme.PawPalTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PawPalTheme {
                PawPalApp()
            }
        }
    }
}

@Composable
fun PawPalApp() {

    val context = LocalContext.current

    var currentScreen by remember {
        mutableStateOf("home")
    }

    var selectedPetId by remember {
        mutableStateOf<Int?>(null)
    }

    var selectedTaskId by remember {
        mutableStateOf<Int?>(null)
    }

    // Load pets from local storage.
    // If nothing was saved yet, use default pets.
    val petList = remember {
        mutableStateListOf<Pet>().apply {
            addAll(
                LocalStorage.loadPets(context) ?: pets
            )
        }
    }

    // Load tasks from local storage.
    // If nothing was saved yet, use default tasks.
    val taskList = remember {
        mutableStateListOf<CareTask>().apply {
            addAll(
                LocalStorage.loadTasks(context) ?: careTasks
            )
        }
    }

    when (currentScreen) {

        // =========================
        // HOME
        // =========================

        "home" -> {

            HomeScreen(
                pets = petList,

                onPetClick = { petId ->
                    selectedPetId = petId
                    currentScreen = "detail"
                },

                onAddPetClick = {
                    currentScreen = "addPet"
                }
            )
        }

        // =========================
        // ADD PET
        // =========================

        "addPet" -> {

            AddPetScreen(
                onBackClick = {
                    currentScreen = "home"
                },

                onSavePet = { name, type, breed, age ->

                    val newId =
                        (petList.maxOfOrNull { it.id } ?: 0) + 1

                    val newPet = Pet(
                        id = newId,
                        name = name,
                        type = type,
                        age = age,
                        breed = breed
                    )

                    petList.add(newPet)

                    LocalStorage.savePets(
                        context,
                        petList
                    )

                    currentScreen = "home"
                }
            )
        }

        // =========================
        // PET DETAILS
        // =========================

        "detail" -> {

            val pet = petList.find {
                it.id == selectedPetId
            }

            if (pet != null) {

                DetailScreen(
                    pet = pet,
                    tasks = taskList,

                    onBackClick = {
                        currentScreen = "home"
                    },

                    onAddTaskClick = {
                        selectedTaskId = null
                        currentScreen = "addTask"
                    },

                    onTaskClick = { taskId ->
                        selectedTaskId = taskId
                        currentScreen = "editTask"
                    },

                    onTaskCompletedChange = { taskId, completed ->

                        val index = taskList.indexOfFirst {
                            it.id == taskId
                        }

                        if (index != -1) {

                            taskList[index] =
                                taskList[index].copy(
                                    isCompleted = completed
                                )

                            LocalStorage.saveTasks(
                                context,
                                taskList
                            )
                        }
                    }
                )

            } else {
                currentScreen = "home"
            }
        }

        // =========================
        // ADD TASK
        // =========================

        "addTask" -> {

            val pet = petList.find {
                it.id == selectedPetId
            }

            if (pet != null) {

                CareTaskScreen(
                    pet = pet,
                    existingTask = null,

                    onBackClick = {
                        currentScreen = "detail"
                    },

                    onSaveTask = { title, time, notes ->

                        val newId =
                            (taskList.maxOfOrNull { it.id } ?: 0) + 1

                        val newTask = CareTask(
                            id = newId,
                            petId = pet.id,
                            title = title,
                            time = time,
                            notes = notes,
                            isCompleted = false
                        )

                        taskList.add(newTask)

                        LocalStorage.saveTasks(
                            context,
                            taskList
                        )

                        currentScreen = "detail"
                    }
                )

            } else {
                currentScreen = "home"
            }
        }

        // =========================
        // EDIT TASK
        // =========================

        "editTask" -> {

            val pet = petList.find {
                it.id == selectedPetId
            }

            val task = taskList.find {
                it.id == selectedTaskId
            }

            if (pet != null && task != null) {

                CareTaskScreen(
                    pet = pet,
                    existingTask = task,

                    onBackClick = {
                        selectedTaskId = null
                        currentScreen = "detail"
                    },

                    onSaveTask = { title, time, notes ->

                        val index = taskList.indexOfFirst {
                            it.id == task.id
                        }

                        if (index != -1) {

                            taskList[index] =
                                task.copy(
                                    title = title,
                                    time = time,
                                    notes = notes
                                )

                            LocalStorage.saveTasks(
                                context,
                                taskList
                            )
                        }

                        selectedTaskId = null
                        currentScreen = "detail"
                    },

                    onDeleteTask = {

                        taskList.removeAll {
                            it.id == task.id
                        }

                        LocalStorage.saveTasks(
                            context,
                            taskList
                        )

                        selectedTaskId = null
                        currentScreen = "detail"
                    }
                )

            } else {
                selectedTaskId = null
                currentScreen = "detail"
            }
        }
    }
}