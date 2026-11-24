package com.example.pawpal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LoginUiState(
            isLoggedIn = authRepository.currentUserId != null
        )
    )

    val uiState = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        authenticate(email, password, false)
    }

    fun signUp(email: String, password: String) {
        authenticate(email, password, true)
    }

    private fun authenticate(
        email: String,
        password: String,
        createAccount: Boolean
    ) {
        if (_uiState.value.isLoading) return

        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState(
                error = "Enter email and password."
            )
            return
        }

        _uiState.value = LoginUiState(isLoading = true)

        viewModelScope.launch {
            try {
                if (createAccount) {
                    authRepository.signUp(email.trim(), password)
                } else {
                    authRepository.signIn(email.trim(), password)
                }

                _uiState.value = LoginUiState(
                    isLoggedIn = true
                )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = LoginUiState(
                    error = e.toUserMessage()
                )
            }
        }
    }

    fun signOut() {
        if (_uiState.value.isLoading) return
        authRepository.signOut()
        _uiState.value = LoginUiState()
    }
}

fun Throwable.toUserMessage(): String = when (this) {

    is FirebaseAuthWeakPasswordException ->
        "Use at least 6 characters."

    is FirebaseAuthInvalidCredentialsException ->
        "Wrong email or password."

    is FirebaseAuthInvalidUserException ->
        "Wrong email or password."

    is FirebaseAuthUserCollisionException ->
        "This email already has an account."

    is FirebaseNetworkException ->
        "No connection. Try again."

    else ->
        "Something went wrong. Try again."
}