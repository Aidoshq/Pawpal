package com.example.pawpal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawpal.data.repository.TipsRepositoryImpl
import com.example.pawpal.domain.model.Tip
import com.example.pawpal.domain.repository.TipsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

sealed interface TipsUiState {
    data object Loading : TipsUiState

    data class Success(
        val items: List<Tip>
    ) : TipsUiState

    data class Error(
        val message: String
    ) : TipsUiState
}

class TipsViewModel(
    private val repository: TipsRepository = TipsRepositoryImpl()
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<TipsUiState>(TipsUiState.Loading)

    val uiState = _uiState.asStateFlow()

    init {
        loadTips()
    }

    fun loadTips() {
        viewModelScope.launch {
            _uiState.value = TipsUiState.Loading

            try {
                val items = repository.getItems()
                _uiState.value = TipsUiState.Success(items)

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _uiState.value = TipsUiState.Error(
                    e.message ?: "Failed to load tips"
                )
            }
        }
    }
}
