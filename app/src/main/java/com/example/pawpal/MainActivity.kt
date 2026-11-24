package com.example.pawpal

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pawpal.ui.theme.PawPalTheme
import org.koin.androidx.compose.koinViewModel

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

    val loginViewModel: LoginViewModel = koinViewModel()

    val state by loginViewModel.uiState
        .collectAsStateWithLifecycle()

    if (state.isLoggedIn) {

        PawPalMainContent(
            onSignOut = {
                loginViewModel.signOut()
            }
        )

    } else {

        PawPalRegistrationScreen(
            loginViewModel = loginViewModel
        )
    }
}

@Composable
fun PawPalMainContent(
    onSignOut: () -> Unit
) {

    val context = LocalContext.current
    val navController = rememberNavController()

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

        composable("home") {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 4.dp
                        ),
                    horizontalArrangement = Arrangement.End
                ) {

                    SignOutButton(
                        onSignOut = onSignOut
                    )
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    HomeRoute(
                        homeViewModel = homeViewModel,

                        onPetClick = { petId ->
                            navController.navigate(
                                "detail/$petId"
                            )
                        },

                        onAddPetClick = {
                            navController.navigate("addPet")
                        },

                        onTipsClick = {
                            navController.navigate("tips")
                        },

                        onNearMeClick = {
                            navController.navigate("nearMe")
                        }
                    )
                }
            }
        }

        composable("nearMe") {

            NearMeScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

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

                    LocalStorage.savePets(
                        context,
                        petList
                    )

                    navController.popBackStack()
                }
            )
        }

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
                            it.id == taskId &&
                                    it.petId == pet.id
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

                    onDeletePet = {

                        taskList
                            .filter {
                                it.petId == pet.id
                            }
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
                            (taskList.maxOfOrNull {
                                it.id
                            } ?: 0) + 1

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
                it.id == taskId &&
                        it.petId == petId
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
                                    task.id
                                )
                            }
                        }

                        navController.popBackStack()
                    },

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

        composable("tips") {
            TipsScreen()
        }
    }
}

@Composable
private fun PawPalRegistrationScreen(
    loginViewModel: LoginViewModel
) {

    val state by loginViewModel.uiState
        .collectAsStateWithLifecycle()

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {

        Spacer(
            modifier = Modifier.height(40.dp)
        )

        Text(
            text = "Welcome to PawPal",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Sign in or create your account",
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(

            value = email,

            onValueChange = {
                email = it
            },

            label = {
                Text("Email")
            },

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),

            singleLine = true,

            enabled = !state.isLoading,

            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(

            value = password,

            onValueChange = {
                password = it
            },

            label = {
                Text("Password")
            },

            supportingText = {
                Text("At least 6 characters")
            },

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),

            visualTransformation =
                PasswordVisualTransformation(),

            singleLine = true,

            enabled = !state.isLoading,

            modifier = Modifier.fillMaxWidth()
        )

        state.error?.let {

            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (state.isLoading) {

            CircularProgressIndicator()
        }

        Button(

            enabled = !state.isLoading,

            modifier = Modifier.fillMaxWidth(),

            onClick = {
                loginViewModel.signIn(
                    email,
                    password
                )
            }

        ) {
            Text("Sign in")
        }

        OutlinedButton(

            enabled = !state.isLoading,

            modifier = Modifier.fillMaxWidth(),

            onClick = {
                loginViewModel.signUp(
                    email,
                    password
                )
            }

        ) {
            Text("Create account")
        }
    }
}

// Sign out button with confirmation dialog

@Composable
fun SignOutButton(
    onSignOut: () -> Unit
) {

    var showDialog by remember {
        mutableStateOf(false)
    }

    FilledTonalButton(

        onClick = {
            showDialog = true
        },

        modifier = Modifier.heightIn(min = 48.dp),

        shape = RoundedCornerShape(24.dp),

        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),

        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        )

    ) {

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = "Sign out",
            style = MaterialTheme.typography.labelLarge
        )
    }

    if (showDialog) {

        AlertDialog(

            onDismissRequest = {
                showDialog = false
            },

            shape = RoundedCornerShape(28.dp),

            icon = {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },

            title = {
                Text("Sign out?")
            },

            text = {
                Text(
                    "Are you sure you want to sign out of your PawPal account?"
                )
            },

            confirmButton = {

                Button(

                    onClick = {
                        showDialog = false
                        onSignOut()
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),

                    shape = RoundedCornerShape(20.dp)

                ) {
                    Text("Sign out")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}