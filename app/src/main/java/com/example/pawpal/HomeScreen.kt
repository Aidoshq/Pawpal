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
    pets: List<Pet>,
    onPetClick: (Int) -> Unit,
    onAddPetClick: () -> Unit
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
                title = {
                    Text("🐾 PawPal")
                }
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

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "My Pets",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "Take care of your little friends",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }
            }

            // PET CARE TITLE
            item {
                Text(
                    text = "Pet Care",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // HORIZONTAL LIST
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
                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Your Pets",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // EMPTY STATE
            if (pets.isEmpty()) {

                item {
                    EmptyPetsState()
                }

            } else {

                // PET LIST
                items(
                    items = pets,
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

            // ADD PET BUTTON
            item {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

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
            containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                text = "No pets yet",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Add your first pet to start using PawPal.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


// HOME SCREEN LIGHT PREVIEW
@Preview(
    name = "Home Screen Light",
    showBackground = true
)
@Composable
fun HomeScreenLightPreview() {
    PawPalTheme(
        darkTheme = false
    ) {
        HomeScreen(
            pets = pets,
            onPetClick = {},
            onAddPetClick = {}
        )
    }
}


// HOME SCREEN DARK PREVIEW
@Preview(
    name = "Home Screen Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun HomeScreenDarkPreview() {
    PawPalTheme(
        darkTheme = true
    ) {
        HomeScreen(
            pets = pets,
            onPetClick = {},
            onAddPetClick = {}
        )
    }
}


// CARE TIP COMPONENT PREVIEW
@Preview(
    name = "Care Tip",
    showBackground = true
)
@Composable
fun CareTipCardPreview() {
    PawPalTheme {
        CareTipCard(
            tip = "🩺 Vet Check"
        )
    }
}


// EMPTY STATE COMPONENT PREVIEW
@Preview(
    name = "Empty Pets State",
    showBackground = true
)
@Composable
fun EmptyPetsStatePreview() {
    PawPalTheme {
        EmptyPetsState()
    }
}