package com.example.speechease.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.example.speechease.data.pref.UserModel
import com.example.speechease.data.pref.UserPreference
import com.example.speechease.data.response.LoginResponse
import com.example.speechease.data.response.RegisterResponse
import com.example.speechease.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val context: Context,
    private val apiService: ApiService
) {

    private val userPreference: UserPreference by lazy {
        UserPreference.getInstance(context)
    }

    suspend fun registerUser(name: String, email: String, password: String): RegisterResponse {
        val requestBody = mapOf(
            "name" to name,
            "email" to email,
            "password" to password
        )
        return apiService.registerUser(requestBody)
    }

    suspend fun loginUser(email: String, password: String): LoginResponse {
        val requestBody = mapOf(
            "email" to email,
            "password" to password
        )
        return apiService.loginUser(requestBody)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun getUser(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            context: Context,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(context, apiService)
            }.also { instance = it }
    }
}