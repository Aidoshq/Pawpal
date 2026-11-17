package com.example.pawpal

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pawpal.ui.theme.PawPalTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("PawPalLifecycle", "Activity onCreate")

        setContent {
            PawPalTheme {
                PawPalApp()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("PawPalLifecycle", "Activity onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("PawPalLifecycle", "Activity onResume")
    }

    override fun onPause() {
        Log.d("PawPalLifecycle", "Activity onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("PawPalLifecycle", "Activity onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d("PawPalLifecycle", "Activity onDestroy")
        super.onDestroy()
    }
}

@Composable
fun PawPalApp() {

    val context = LocalContext.current
    val navController = rememberNavController()

    // NOTIFICATION PERMISSION
    var notificationPermissionGranted by remember {
        mutableStateOf(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            notificationPermissionGranted = isGranted

            if (isGranted) {
                Log.d(
                    "PawPalNotifications",
                    "Permission granted"
                )
            } else {
                Log.w(
                    "PawPalNotifications",
                    "Permission denied"
                )
            }
        }

    LaunchedEffect(Unit) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !notificationPermissionGranted
        ) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    val homeViewModel: HomeViewModel = viewModel()

    val petList = remember {
        mutableStateListOf<Pet>().apply {
            addAll(LocalStorage.loadPets(context) ?: pets)
        }
    }

    val taskList = remember {
        mutableStateListOf<CareTask>().apply {
            addAll(LocalStorage.loadTasks(context) ?: careTasks)
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // HOME
        composable("home") {
            HomeRoute(
                homeViewModel = homeViewModel,

                onPetClick = { petId ->
                    navController.navigate("detail/$petId")
                },

                onAddPetClick = {
                    navController.navigate("addPet")
                },

                onTipsClick = {
                    navController.navigate("tips")
                }
            )
        }

        // ADD PET
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
                    homeViewModel.addPet(newPet)

                    LocalStorage.savePets(context, petList)

                    navController.popBackStack()
                }
            )
        }

        // PET DETAILS
        composable(
            route = "detail/{petId}",
            arguments = listOf(
                navArgument("petId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val petId = backStackEntry.arguments?.getInt("petId")
            val pet = petList.find { it.id == petId }

            if (pet != null) {
                DetailScreen(
                    pet = pet,
                    tasks = taskList,

                    onBackClick = {
                        navController.popBackStack()
                    },

                    onAddTaskClick = {
                        navController.navigate("addTask/${pet.id}")
                    },

                    onTaskClick = { taskId ->
                        navController.navigate(
                            "editTask/${pet.id}/$taskId"
                        )
                    },

                    // COMPLETE / UNCOMPLETE TASK
                    onTaskCompletedChange = { taskId, completed ->

                        val index = taskList.indexOfFirst {
                            it.id == taskId && it.petId == pet.id
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

                            if (completed) {
                                ReminderScheduler.cancelReminder(
                                    context,
                                    taskId
                                )
                            } else {
                                ReminderScheduler.scheduleReminder(
                                    context,
                                    taskList[index],
                                    pet.name
                                )
                            }
                        }
                    },

                    // DELETE PET
                    onDeletePet = {

                        taskList
                            .filter { it.petId == pet.id }
                            .forEach { task ->
                                ReminderScheduler.cancelReminder(
                                    context,
                                    task.id
                                )
                            }

                        taskList.removeAll {
                            it.petId == pet.id
                        }

                        petList.removeAll {
                            it.id == pet.id
                        }

                        homeViewModel.deletePet(pet.id)

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

        // ADD TASK
        composable(
            route = "addTask/{petId}",
            arguments = listOf(
                navArgument("petId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val petId = backStackEntry.arguments?.getInt("petId")
            val pet = petList.find { it.id == petId }

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

                        ReminderScheduler.scheduleReminder(
                            context,
                            newTask,
                            pet.name
                        )

                        navController.popBackStack()
                    }
                )
            }
        }

        // EDIT TASK
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

            val petId = backStackEntry.arguments?.getInt("petId")
            val taskId = backStackEntry.arguments?.getInt("taskId")

            val pet = petList.find { it.id == petId }
            val task = taskList.find {
                it.id == taskId && it.petId == petId
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
                            val updatedTask = task.copy(
                                title = title,
                                time = time,
                                notes = notes
                            )

                            taskList[index] = updatedTask

                            LocalStorage.saveTasks(
                                context,
                                taskList
                            )

                            if (!updatedTask.isCompleted) {
                                ReminderScheduler.scheduleReminder(
                                    context,
                                    updatedTask,
                                    pet.name
                                )
                            } else {
                                ReminderScheduler.cancelReminder(
                                    context,
                                    updatedTask.id
                                )
                            }
                        }

                        navController.popBackStack()
                    },

                    // DELETE TASK
                    onDeleteTask = {

                        ReminderScheduler.cancelReminder(
                            context,
                            task.id
                        )

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

        // TIPS SCREEN - SIS5
        composable("tips") {
            TipsScreen()
        }
    }
}
