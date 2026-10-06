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
    )
)