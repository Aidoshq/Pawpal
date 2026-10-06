package com.example.pawpal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    onTaskCompletedChange: (Int, Boolean) -> Unit
) {

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
        }
    }
}