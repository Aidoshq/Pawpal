package com.example.pawpal

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import com.example.pawpal.ui.theme.PawPalTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    pet: Pet,
    tasks: List<CareTask>,
    onBackClick: () -> Unit,
    onAddTaskClick: () -> Unit,
    onTaskClick: (Int) -> Unit,
    onTaskCompletedChange: (Int, Boolean) -> Unit,
    onDeletePet: () -> Unit
) {

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    val petTasks = tasks.filter {
        it.petId == pet.id
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Pet Details")
                },
                navigationIcon = {
                    TextButton(
                        onClick = onBackClick
                    ) {
                        Text("←")
                    }
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                bottom = 30.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // PET PHOTO
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = when (pet.type.lowercase()) {
                                "dog" -> "🐶"
                                "cat" -> "🐱"
                                "rabbit" -> "🐰"
                                else -> "🐾"
                            },
                            style = MaterialTheme.typography.displayLarge
                        )
                    }
                }
            }

            // PET INFORMATION
            item {

                Column {

                    Text(
                        text = pet.name,
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = pet.breed,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "${pet.type} • ${pet.age}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // CARE TASKS TITLE
            item {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Care Tasks",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            // NO TASKS
            if (petTasks.isEmpty()) {

                item {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp)
                    ) {

                        Text(
                            text = "No care tasks yet",
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }

            } else {

                // TASK LIST
                items(
                    items = petTasks,
                    key = { task -> task.id }
                ) { task ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onTaskClick(task.id)
                            },
                        shape = RoundedCornerShape(18.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { completed ->

                                    onTaskCompletedChange(
                                        task.id,
                                        completed
                                    )
                                }
                            )

                            Spacer(
                                modifier = Modifier.width(10.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = task.time,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                if (task.notes.isNotBlank()) {

                                    Spacer(
                                        modifier = Modifier.height(4.dp)
                                    )

                                    Text(
                                        text = task.notes,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                if (task.isCompleted) {

                                    Spacer(
                                        modifier = Modifier.height(4.dp)
                                    )

                                    Text(
                                        text = "Completed",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }

                            Text(
                                text = "›",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
            }

            // ADD TASK BUTTON
            item {

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Button(
                    onClick = onAddTaskClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {

                    Text(
                        text = "+  Add Care Task",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // DELETE PET BUTTON
            item {

                OutlinedButton(
                    onClick = {
                        showDeleteDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {

                    Text(
                        text = "Delete Pet",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // DELETE CONFIRMATION
    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },

            title = {
                Text("Delete ${pet.name}?")
            },

            text = {
                Text(
                    "Are you sure you want to delete this pet? " +
                            "All care tasks for this pet will also be deleted."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeletePet()
                    }
                ) {

                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
@Preview(
    name = "Detail Screen Light",
    showBackground = true
)
@Composable
fun DetailScreenLightPreview() {
    PawPalTheme(darkTheme = false) {
        DetailScreen(
            pet = Pet(
                id = 1,
                name = "Milo",
                type = "Dog",
                age = "2 years",
                breed = "Golden Retriever"
            ),
            tasks = emptyList(),
            onBackClick = {},
            onAddTaskClick = {},
            onTaskClick = {},
            onTaskCompletedChange = { _, _ -> },
            onDeletePet = {}
        )
    }
}

@Preview(
    name = "Detail Screen Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DetailScreenDarkPreview() {
    PawPalTheme(darkTheme = true) {
        DetailScreen(
            pet = Pet(
                id = 1,
                name = "Milo",
                type = "Dog",
                age = "2 years",
                breed = "Golden Retriever"
            ),
            tasks = emptyList(),
            onBackClick = {},
            onAddTaskClick = {},
            onTaskClick = {},
            onTaskCompletedChange = { _, _ -> },
            onDeletePet = {}
        )
    }
}