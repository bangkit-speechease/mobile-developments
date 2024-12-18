package com.example.speechease.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechease.data.pref.UserModel
import com.example.speechease.data.repository.UserRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<UserModel>>()
    val loginResult: LiveData<Result<UserModel>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Result.Loading
            try {
                val response = userRepository.loginUser(email, password)
                if (!response.error) {
                    val user = UserModel(
                        userId = response.loginResult.userId,
                        name = response.loginResult.name,
                        email = email,
                        token = response.loginResult.token,
                        isLogin = true
                    )
                    userRepository.saveSession(user)
                    _loginResult.value = Result.Success(user)
                } else {
                    _loginResult.value = Result.Error(response.message)
                }
            } catch (e: Exception) {
                _loginResult.value = Result.Error(e.message.toString())
            }
        }
    }
}