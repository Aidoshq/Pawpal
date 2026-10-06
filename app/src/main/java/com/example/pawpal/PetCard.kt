package com.example.pawpal

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import com.example.pawpal.ui.theme.PawPalTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun PetCard(
    pet: Pet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // PET IMAGE
            Image(
                painter = painterResource(
                    id = R.drawable.pet_placeholder
                ),
                contentDescription = "${pet.name} profile image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(
                        RoundedCornerShape(18.dp)
                    )
            )

            Spacer(
                modifier = Modifier.width(18.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                // PET NAME
                Text(
                    text = pet.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                // PET TYPE
                Text(
                    text = pet.type,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                // BREED AND AGE
                Text(
                    text = "${pet.breed} · ${pet.age}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "›",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
@Preview(
    name = "Pet Card Light",
    showBackground = true
)
@Composable
fun PetCardLightPreview() {

    PawPalTheme(
        darkTheme = false
    ) {

        PetCard(
            pet = Pet(
                id = 1,
                name = "Milo",
                type = "Dog",
                age = "2 years",
                breed = "Golden Retriever"
            ),
            onClick = {}
        )
    }
}

@Preview(
    name = "Pet Card Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PetCardDarkPreview() {

    PawPalTheme(
        darkTheme = true
    ) {

        PetCard(
            pet = Pet(
                id = 1,
                name = "Milo",
                type = "Dog",
                age = "2 years",
                breed = "Golden Retriever"
            ),
            onClick = {}
        )
    }
}