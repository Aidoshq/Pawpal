# PawPal - SIS4 MVVM Architecture

## Architecture Diagram

```mermaid
flowchart TD
    A[HomeScreen] -->|Events| B[HomeViewModel]
    B -->|StateFlow UiState| A
    B --> C[PetRepository]
    C --> D[LocalStorage]
    D --> E[SharedPreferences]
## Views
HomeScreen, DetailScreen, AddPetScreen, CareTaskScreen.

## ViewModel
HomeViewModel manages the home screen.

## UiState
pets, searchQuery, selectedType, isLoading, error.

## Events
onSearchChange, onTypeSelected, onSortClick, onRetry.

## Repository
PetRepository loads pet data from LocalStorage.
It simulates delays and errors.

## Trace One Click
When the user enters a pet name, HomeScreen calls
onSearchChange. HomeViewModel updates StateFlow.
The ViewModel filters the pets, and HomeScreen
displays the updated results.
