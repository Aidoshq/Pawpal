
package com.example.pawpal

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pawpal.ui.theme.PawPalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onPetClick: (Int) -> Unit,
    onAddPetClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onTypeSelected: (String) -> Unit,
    onSortClick: () -> Unit,
    onRetry: () -> Unit,
    onSimulateError: () -> Unit = {}
) {
    val careTips = listOf(
        "🍽️ Feeding",
        "🦮 Walking",
        "🧼 Grooming",
        "🩺 Vet Check"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🐾  PawPal") }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 20.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // HEADER
            item {
                Column {
                    Text(
                        text = "YOUR PET FAMILY",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "My Pets",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Take care of your little friends",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // PET CARE
            item {
                Text(
                    text = "Pet Care",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(careTips) { tip ->
                        CareTipCard(tip = tip)
                    }
                }
            }

            // PET LIST TITLE
            item {
                Text(
                    text = "Your Pets",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // SEARCH
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChange,
                    label = { Text("Search pets") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // FILTER
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        listOf("All", "Dog", "Cat", "Bird", "Other")
                    ) { type ->
                        FilterChip(
                            selected = uiState.selectedType == type,
                            onClick = { onTypeSelected(type) },
                            label = { Text(type) }
                        )
                    }
                }
            }

            // SORT
            item {
                OutlinedButton(
                    onClick = onSortClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (uiState.sortAscending) {
                            "Sort: A to Z"
                        } else {
                            "Sort: Z to A"
                        }
                    )
                }
            }

            // SIMULATE ERROR
            item {
                OutlinedButton(
                    onClick = onSimulateError,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Simulate Loading Error")
                }
            }

            // FOUR UI STATES
            when {
                uiState.isLoading -> {
                    item {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    item {
                        Column {
                            Text(
                                text = uiState.error,
                                color = MaterialTheme.colorScheme.error
                            )

                            Spacer(Modifier.height(8.dp))

                            Button(onClick = onRetry) {
                                Text("Retry")
                            }
                        }
                    }
                }

                uiState.pets.isEmpty() -> {
                    item {
                        EmptyPetsState()
                    }
                }

                else -> {
                    items(
                        items = uiState.pets,
                        key = { pet -> pet.id }
                    ) { pet ->
                        PetCard(
                            pet = pet,
                            onClick = {
                                onPetClick(pet.id)
                            }
                        )
                    }
                }
            }

            // ADD PET BUTTON
            item {
                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = onAddPetClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("+  Add New Pet")
                }
            }
        }
    }
}

// REUSABLE COMPOSABLE 1
@Composable
fun CareTipCard(
    tip: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(
            text = tip,
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 14.dp
            ),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

// REUSABLE COMPOSABLE 2
@Composable
fun EmptyPetsState(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "No pets found",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Add a pet or change your search filters.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// PREVIEW DATA
private val previewPets = listOf(
    Pet(
        id = 1,
        name = "Buddy",
        type = "Dog",
        age = "3",
        breed = "Golden Retriever"
    ),
    Pet(
        id = 2,
        name = "Luna",
        type = "Cat",
        age = "2",
        breed = "British Shorthair"
    )
)

// CONTENT PREVIEW
@Preview(name = "Content", showBackground = true)
@Composable
fun HomeContentPreview() {
    PawPalTheme {
        HomeScreen(
            uiState = HomeUiState(pets = previewPets),
            onPetClick = {},
            onAddPetClick = {},
            onSearchChange = {},
            onTypeSelected = {},
            onSortClick = {},
            onRetry = {},
            onSimulateError = {}
        )
    }
}

// LOADING PREVIEW
@Preview(name = "Loading", showBackground = true)
@Composable
fun HomeLoadingPreview() {
    PawPalTheme {
        HomeScreen(
            uiState = HomeUiState(isLoading = true),
            onPetClick = {},
            onAddPetClick = {},
            onSearchChange = {},
            onTypeSelected = {},
            onSortClick = {},
            onRetry = {},
            onSimulateError = {}
        )
    }
}

// EMPTY PREVIEW
@Preview(name = "Empty", showBackground = true)
@Composable
fun HomeEmptyPreview() {
    PawPalTheme {
        HomeScreen(
            uiState = HomeUiState(),
            onPetClick = {},
            onAddPetClick = {},
            onSearchChange = {},
            onTypeSelected = {},
            onSortClick = {},
            onRetry = {},
            onSimulateError = {}
        )
    }
}

// ERROR PREVIEW
@Preview(name = "Error", showBackground = true)
@Composable
fun HomeErrorPreview() {
    PawPalTheme {
        HomeScreen(
            uiState = HomeUiState(
                error = "Failed to load pets"
            ),
            onPetClick = {},
            onAddPetClick = {},
            onSearchChange = {},
            onTypeSelected = {},
            onSortClick = {},
            onRetry = {},
            onSimulateError = {}
        )
    }
}

// DARK MODE PREVIEW
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun HomeDarkPreview() {
    PawPalTheme(darkTheme = true) {
        HomeScreen(
            uiState = HomeUiState(pets = previewPets),
            onPetClick = {},
            onAddPetClick = {},
            onSearchChange = {},
            onTypeSelected = {},
            onSortClick = {},
            onRetry = {},
            onSimulateError = {}
        )
    }
}

// CARE TIP PREVIEW
@Preview(name = "Care Tip", showBackground = true)
@Composable
fun CareTipCardPreview() {
    PawPalTheme {
        CareTipCard(tip = "🩺 Vet Check")
    }
}

// EMPTY COMPONENT PREVIEW
@Preview(name = "Empty Component", showBackground = true)
@Composable
fun EmptyPetsStatePreview() {
    PawPalTheme {
        EmptyPetsState()
    }
}
