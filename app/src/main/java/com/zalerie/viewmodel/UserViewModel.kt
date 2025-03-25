package com.zalerie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalerie.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _isUserRegistered = MutableStateFlow<Boolean?>(null)
    val isUserRegistered: StateFlow<Boolean?> = _isUserRegistered

    fun checkUserRegistration(userId: String) {
        viewModelScope.launch {
            _isUserRegistered.value = userRepository.isUserRegistered(userId)
        }
    }

    fun registerUser(userId: String, userData: UserData) {
        viewModelScope.launch {
            _isUserRegistered.value = userRepository.registerUser(userId, userData)
        }
    }
}