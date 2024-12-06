package com.example.speechease.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechease.data.pref.UserModel
import com.example.speechease.data.repository.UserRepository
import com.example.speechease.data.response.LoginResponse
import kotlinx.coroutines.launch
import javax.inject.Inject


class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Result.Loading
            try {
                val response = userRepository.loginUser(email, password)
                if (!response.error) {
                    val user = UserModel(
                        email = response.data?.email ?: "",
                        token = response.data?.token ?: "",
                        isLogin = true
                    )
                    userRepository.saveSession(user)
                    _loginResult.value = Result.Success(response)
                } else {
                    _loginResult.value = Result.Error(response.message)
                }
            } catch (e: Exception) {
                _loginResult.value = Result.Error(e.message.toString())
            }
        }
    }
}