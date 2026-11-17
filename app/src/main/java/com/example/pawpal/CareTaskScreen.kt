
package com.example.pawpal

import android.app.TimePickerDialog
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pawpal.ui.theme.PawPalTheme
import java.util.Calendar
import java.util.Locale

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
    val context = LocalContext.current

    val timePicker = remember(context) {
        val calendar = Calendar.getInstance()

        TimePickerDialog(
            context,
            { _, hour, minute ->
                time = String.format(
                    Locale.US,
                    "%02d:%02d",
                    hour,
                    minute
                )
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

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
                    TextButton(onClick = onBackClick) {
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
                onValueChange = { title = it },
                label = { Text("Task Name") },
                placeholder = {
                    Text("Enter task name")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // PET
            OutlinedTextField(
                value = pet.name,
                onValueChange = {},
                label = { Text("Pet") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // REMINDER TIME
            OutlinedTextField(
                value = time,
                onValueChange = {},
                label = { Text("Reminder Time") },
                placeholder = {
                    Text("Select time")
                },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            timePicker.show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Select time"
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // NOTES
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                placeholder = {
                    Text("Add any additional notes...")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // SAVE TASK
            Button(
                onClick = {
                    if (
                        title.isNotBlank() &&
                        time.isNotBlank()
                    ) {
                        onSaveTask(
                            title.trim(),
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

            // DELETE TASK
            if (isEditing && onDeleteTask != null) {

                Spacer(modifier = Modifier.height(14.dp))

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

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// LIGHT PREVIEW
@Preview(
    name = "Care Task Light",
    showBackground = true
)
@Composable
fun CareTaskScreenLightPreview() {
    PawPalTheme(darkTheme = false) {
        CareTaskScreen(
            pet = Pet(
                id = 1,
                name = "Milo",
                type = "Dog",
                age = "2 years",
                breed = "Golden Retriever"
            ),
            existingTask = null,
            onBackClick = {},
            onSaveTask = { _, _, _ -> }
        )
    }
}

// DARK PREVIEW
@Preview(
    name = "Care Task Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun CareTaskScreenDarkPreview() {
    PawPalTheme(darkTheme = true) {
        CareTaskScreen(
            pet = Pet(
                id = 1,
                name = "Milo",
                type = "Dog",
                age = "2 years",
                breed = "Golden Retriever"
            ),
            existingTask = null,
            onBackClick = {},
            onSaveTask = { _, _, _ -> }
        )
    }
}
