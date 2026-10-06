package com.example.pawpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
    val navController = rememberNavController()

    val petList = remember {
        mutableStateListOf<Pet>().apply {
            addAll(
                LocalStorage.loadPets(context) ?: pets
            )
        }
    }

    val taskList = remember {
        mutableStateListOf<CareTask>().apply {
            addAll(
                LocalStorage.loadTasks(context) ?: careTasks
            )
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // =========================
        // HOME
        // =========================

        composable("home") {

            HomeScreen(
                pets = petList,

                onPetClick = { petId ->
                    navController.navigate("detail/$petId")
                },

                onAddPetClick = {
                    navController.navigate("addPet")
                }
            )
        }

        // =========================
        // ADD PET
        // =========================

        composable("addPet") {

            AddPetScreen(

                onBackClick = {
                    navController.popBackStack()
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

                    navController.popBackStack()
                }
            )
        }

        // =========================
        // PET DETAILS
        // =========================

        composable(
            route = "detail/{petId}",
            arguments = listOf(
                navArgument("petId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val petId =
                backStackEntry.arguments?.getInt("petId")

            val pet = petList.find {
                it.id == petId
            }

            if (pet != null) {

                DetailScreen(
                    pet = pet,
                    tasks = taskList,

                    onBackClick = {
                        navController.popBackStack()
                    },

                    onAddTaskClick = {
                        navController.navigate(
                            "addTask/${pet.id}"
                        )
                    },

                    onTaskClick = { taskId ->
                        navController.navigate(
                            "editTask/${pet.id}/$taskId"
                        )
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
                    },

                    onDeletePet = {

                        taskList.removeAll {
                            it.petId == pet.id
                        }

                        petList.removeAll {
                            it.id == pet.id
                        }

                        LocalStorage.savePets(
                            context,
                            petList
                        )

                        LocalStorage.saveTasks(
                            context,
                            taskList
                        )

                        navController.popBackStack()
                    }
                )
            }
        }

        // =========================
        // ADD TASK
        // =========================

        composable(
            route = "addTask/{petId}",
            arguments = listOf(
                navArgument("petId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val petId =
                backStackEntry.arguments?.getInt("petId")

            val pet = petList.find {
                it.id == petId
            }

            if (pet != null) {

                CareTaskScreen(
                    pet = pet,
                    existingTask = null,

                    onBackClick = {
                        navController.popBackStack()
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

                        navController.popBackStack()
                    }
                )
            }
        }

        // =========================
        // EDIT TASK
        // =========================

        composable(
            route = "editTask/{petId}/{taskId}",
            arguments = listOf(
                navArgument("petId") {
                    type = NavType.IntType
                },
                navArgument("taskId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val petId =
                backStackEntry.arguments?.getInt("petId")

            val taskId =
                backStackEntry.arguments?.getInt("taskId")

            val pet = petList.find {
                it.id == petId
            }

            val task = taskList.find {
                it.id == taskId
            }

            if (pet != null && task != null) {

                CareTaskScreen(
                    pet = pet,
                    existingTask = task,

                    onBackClick = {
                        navController.popBackStack()
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

                        navController.popBackStack()
                    },

                    onDeleteTask = {

                        taskList.removeAll {
                            it.id == task.id
                        }

                        LocalStorage.saveTasks(
                            context,
                            taskList
                        )

                        navController.popBackStack()
                    }
                )
            }
        }
    }
}