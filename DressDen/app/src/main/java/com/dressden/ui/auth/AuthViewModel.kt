package com.dressden.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dressden.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = userRepository.signIn(email, password)
                result.fold(
                    onSuccess = { user ->
                        _authState.value = AuthState.Success(user)
                    },
                    onFailure = { exception ->
                        _authState.value = AuthState.Error(
                            exception.message ?: "Authentication failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Authentication failed")
            }
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = userRepository.signUp(email, password, displayName)
                result.fold(
                    onSuccess = { user ->
                        _authState.value = AuthState.Success(user)
                    },
                    onFailure = { exception ->
                        _authState.value = AuthState.Error(
                            exception.message ?: "Registration failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                userRepository.resetPassword(email)
                _authState.value = AuthState.ResetPasswordSuccess
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "Failed to send reset password email"
                )
            }
        }
    }

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        // Password must be at least 6 characters long
        return password.length >= 6
    }

    fun validateDisplayName(displayName: String): Boolean {
        // Display name must be at least 3 characters long
        return displayName.length >= 3
    }

    fun clearAuthState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: com.dressden.data.model.User) : AuthState()
    data class Error(val message: String) : AuthState()
    object ResetPasswordSuccess : AuthState()
}
