package com.example.pawpal

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = PetRepository(application)

    private var allPets: List<Pet> = emptyList()

    private val _uiState = MutableStateFlow(
        HomeUiState(isLoading = true)
    )

    val uiState: StateFlow<HomeUiState> =
        _uiState.asStateFlow()

    init {
        Log.d("PawPalLifecycle", "HomeViewModel created")
        loadPets()
    }

    fun loadPets() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }

            try {
                allPets = repository.getPets()
                updateVisiblePets()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun onSearchChange(query: String) {
        _uiState.update {
            it.copy(searchQuery = query)
        }
        updateVisiblePets()
    }

    fun onTypeSelected(type: String) {
        _uiState.update {
            it.copy(selectedType = type)
        }
        updateVisiblePets()
    }

    fun onSortClick() {
        _uiState.update {
            it.copy(sortAscending = !it.sortAscending)
        }
        updateVisiblePets()
    }

    fun onRetry() {
        loadPets()
    }

    fun setSimulatedError(enabled: Boolean) {
        repository.shouldFail = enabled
    }

    private fun updateVisiblePets() {
        val state = _uiState.value

        val filtered = allPets
            .filter { pet ->
                pet.name.contains(
                    state.searchQuery,
                    ignoreCase = true
                )
            }
            .filter { pet ->
                state.selectedType == "All" ||
                        pet.type == state.selectedType
            }

        val sorted = if (state.sortAscending) {
            filtered.sortedBy { it.name.lowercase() }
        } else {
            filtered.sortedByDescending { it.name.lowercase() }
        }

        _uiState.update {
            it.copy(
                pets = sorted,
                isLoading = false,
                error = null
            )
        }
    }

    override fun onCleared() {
        Log.d("PawPalLifecycle", "HomeViewModel cleared")
        super.onCleared()
    }
}
