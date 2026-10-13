
package com.example.pawpal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    onPetClick: (Int) -> Unit,
    onAddPetClick: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onPetClick = onPetClick,
        onAddPetClick = onAddPetClick,
        onSearchChange = homeViewModel::onSearchChange,
        onTypeSelected = homeViewModel::onTypeSelected,
        onSortClick = homeViewModel::onSortClick,
        onRetry = homeViewModel::retryAfterError,
        onSimulateError = homeViewModel::simulateError
    )
}
