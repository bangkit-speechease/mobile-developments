package com.example.speechease.ui.signup

import androidx.lifecycle.ViewModel
import com.example.speechease.data.repository.UserRepository
import javax.inject.Inject

class SignupViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    suspend fun register(name: String, email: String, password: String) =
        userRepository.registerUser(name, email, password)
}