package com.example.pawpal

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import com.example.pawpal.ui.theme.PawPalTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    onBackClick: () -> Unit,
    onSavePet: (
        name: String,
        type: String,
        breed: String,
        age: String
    ) -> Unit
) {
    var name by remember {
        mutableStateOf("")
    }

    var type by remember {
        mutableStateOf("")
    }

    var breed by remember {
        mutableStateOf("")
    }

    var age by remember {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Add Pet")
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Pet photo placeholder
            Surface(
                modifier = Modifier.size(160.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🐾",
                            style = MaterialTheme.typography.displaySmall
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Add Photo",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            // Pet Name
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                label = {
                    Text("Pet Name")
                },
                placeholder = {
                    Text("Enter pet name")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // Pet Type
            OutlinedTextField(
                value = type,
                onValueChange = {
                    type = it
                },
                label = {
                    Text("Pet Type")
                },
                placeholder = {
                    Text("Dog, Cat, Rabbit...")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // Breed
            OutlinedTextField(
                value = breed,
                onValueChange = {
                    breed = it
                },
                label = {
                    Text("Breed")
                },
                placeholder = {
                    Text("Enter breed")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // Age
            OutlinedTextField(
                value = age,
                onValueChange = {
                    age = it
                },
                label = {
                    Text("Age")
                },
                placeholder = {
                    Text("Enter age")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Button(
                onClick = {
                    if (
                        name.isNotBlank() &&
                        type.isNotBlank() &&
                        breed.isNotBlank() &&
                        age.isNotBlank()
                    ) {
                        onSavePet(
                            name,
                            type,
                            breed,
                            age
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "Save Pet",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}
@Preview(
    name = "Add Pet Light",
    showBackground = true
)
@Composable
fun AddPetScreenLightPreview() {
    PawPalTheme(
        darkTheme = false
    ) {
        AddPetScreen(
            onBackClick = {},
            onSavePet = { _, _, _, _ -> }
        )
    }
}

@Preview(
    name = "Add Pet Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AddPetScreenDarkPreview() {
    PawPalTheme(
        darkTheme = true
    ) {
        AddPetScreen(
            onBackClick = {},
            onSavePet = { _, _, _, _ -> }
        )
    }
}