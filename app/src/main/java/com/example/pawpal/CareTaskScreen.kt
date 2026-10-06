package com.example.pawpal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareTaskScreen(
    pet: Pet,
    existingTask: CareTask? = null,
    onBackClick: () -> Unit,
    onSaveTask: (
        title: String,
        time: String,
        notes: String
    ) -> Unit,
    onDeleteTask: (() -> Unit)? = null
) {
    var title by remember(existingTask) {
        mutableStateOf(existingTask?.title ?: "")
    }

    var time by remember(existingTask) {
        mutableStateOf(existingTask?.time ?: "")
    }

    var notes by remember(existingTask) {
        mutableStateOf(existingTask?.notes ?: "")
    }

    val isEditing = existingTask != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) {
                            "Edit Care Task"
                        } else {
                            "Add Care Task"
                        }
                    )
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            // TASK NAME
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                },
                label = {
                    Text("Task Name")
                },
                placeholder = {
                    Text("Enter task name")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // PET
            OutlinedTextField(
                value = pet.name,
                onValueChange = {},
                label = {
                    Text("Pet")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // TIME
            OutlinedTextField(
                value = time,
                onValueChange = {
                    time = it
                },
                label = {
                    Text("Time")
                },
                placeholder = {
                    Text("Example: 9:00 PM")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // NOTES
            OutlinedTextField(
                value = notes,
                onValueChange = {
                    notes = it
                },
                label = {
                    Text("Notes")
                },
                placeholder = {
                    Text("Add any additional notes...")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            // SAVE
            Button(
                onClick = {
                    if (
                        title.isNotBlank() &&
                        time.isNotBlank()
                    ) {
                        onSaveTask(
                            title,
                            time,
                            notes
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = if (isEditing) {
                        "Save Changes"
                    } else {
                        "Save Task"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // DELETE - only visible when editing
            if (isEditing && onDeleteTask != null) {

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                OutlinedButton(
                    onClick = onDeleteTask,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = "Delete Task",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}