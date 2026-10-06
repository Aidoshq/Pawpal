package com.example.pawpal

data class Pet(
    val id: Int,
    val name: String,
    val type: String,
    val age: String,
    val breed: String
)

val pets = listOf(
    Pet(
        id = 1,
        name = "Milo",
        type = "Dog",
        age = "2 years",
        breed = "Golden Retriever"
    ),
    Pet(
        id = 2,
        name = "Luna",
        type = "Cat",
        age = "1 year",
        breed = "British Shorthair"
    ),
    Pet(
        id = 3,
        name = "Pepper",
        type = "Rabbit",
        age = "8 months",
        breed = "Mini Lop"
    ),
    Pet(
        id = 4,
        name = "Charlie",
        type = "Dog",
        age = "3 years",
        breed = "Beagle"
    ),
    Pet(
        id = 5,
        name = "Bella",
        type = "Cat",
        age = "2 years",
        breed = "Siamese"
    ),
    Pet(
        id = 6,
        name = "Rocky",
        type = "Dog",
        age = "4 years",
        breed = "Husky"
    ),
    Pet(
        id = 7,
        name = "Coco",
        type = "Rabbit",
        age = "1 year",
        breed = "Holland Lop"
    ),
    Pet(
        id = 8,
        name = "Leo",
        type = "Cat",
        age = "3 years",
        breed = "Maine Coon"
    ),
    Pet(
        id = 9,
        name = "Teddy",
        type = "Dog",
        age = "1 year",
        breed = "Poodle"
    ),
    Pet(
        id = 10,
        name = "Nala",
        type = "Cat",
        age = "2 years",
        breed = "Scottish Fold"
    )
)