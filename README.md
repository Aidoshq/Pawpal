# PawPal 🐾

PawPal is an Android app for pet owners. I created it to make it easier to keep pet information and daily care tasks in one place.

## About the App

Pet owners need to remember many things, like feeding their pets, walking them, giving medicine, or visiting the vet. When someone has more than one pet, it can be hard to keep everything organized.

With PawPal, users can create profiles for their pets and keep track of their care tasks.

## What the App Can Do

- Add a pet with its name, type, age, and breed.
- See all saved pets on the home screen.
- Open a pet profile to see more information.
- Add, edit, and delete care tasks.
- Mark tasks as completed.
- Save pets and tasks locally on the device.
- Search for pets by name.
- Filter pets by type and sort them by name.

## How It Works

The user opens the app and sees the list of pets. They can select a pet to view its details and care tasks, or add a new pet.

Inside a pet profile, the user can create a task, edit it, or mark it as completed.

## Technologies

I used Kotlin and Jetpack Compose to build the app in Android Studio. I also used Git and GitHub to save my work and track changes.

Pet profiles and tasks are stored locally using SharedPreferences.

## SIS3: UI Development

For SIS3, I worked on the app's screens and navigation.

I created the home screen, pet details screen, add pet screen, and care task screen. I also added search, filters, reusable UI components, and support for light and dark mode.

## SIS4: MVVM

For SIS4, I refactored the home screen using MVVM architecture.

MVVM separates the user interface from the logic and data. In my project:

- `HomeScreen.kt` displays the UI.
- `HomeRoute.kt` connects the screen to the ViewModel.
- `HomeViewModel.kt` handles searching, filtering, sorting, and loading.
- `HomeUiState.kt` stores the current screen state.
- `PetRepository.kt` loads and saves pet data.
- `LocalStorage.kt` uses SharedPreferences to store data.

The architecture diagram is in `design/SIS4_ARCHITECTURE.md`.

The home screen uses StateFlow to receive updates from the ViewModel. It can show loading, content, empty, and error states.

I also added a button to simulate a loading error. When the error appears, the user can press Retry to load the pets again.

## Screen Rotation

I tested the app by entering text in the search bar, choosing a pet filter, and rotating the emulator.

The search text and selected filter stayed the same because HomeViewModel keeps its state during screen rotation.

I also checked the Activity lifecycle in Logcat using the tag `PawPalLifecycle`.

## Current Status

The main features of PawPal are working. The home screen uses MVVM, while some pet details and care task operations still use state managed in MainActivity.

I tested the main features, including adding pets, managing tasks, searching, filtering, error handling, and screen rotation.

## Screenshots

Screenshots of the app in light and dark mode, different UI states, and Logcat can be added here.

## AI Usage

I used AI to help understand some Android concepts and solve coding problems. More details are available in `AI_USAGE.md`.