
package com.example.pawpal

data class HomeUiState(
    val pets: List<Pet> = emptyList(),
    val searchQuery: String = "",
    val selectedType: String = "All",
    val isLoading: Boolean = false,
    val error: String? = null,
    val sortAscending: Boolean = true
)
