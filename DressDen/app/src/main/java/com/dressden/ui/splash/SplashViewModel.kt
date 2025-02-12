package com.dressden.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dressden.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> = _isUserLoggedIn

    fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser()
                _isUserLoggedIn.value = currentUser != null
            } catch (e: Exception) {
                _isUserLoggedIn.value = false
            }
        }
    }
}
