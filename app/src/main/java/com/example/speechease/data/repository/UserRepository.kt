package com.example.speechease.data.repository

import com.example.speechease.data.response.DeleteUserResponse
import com.example.speechease.data.response.LoginResponse
import com.example.speechease.data.response.LogoutResponse
import com.example.speechease.data.response.RegisterResponse
import com.example.speechease.data.response.UpdateUserResponse
import com.example.speechease.data.response.UserDetailResponse
import com.example.speechease.data.retrofit.ApiService
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {

    suspend fun registerUser(name: String, email: String, password: String): RegisterResponse {
        val requestBody = mapOf(
            "name" to name,
            "email" to email,
            "password" to password
        )
        return apiService.registerUser(requestBody)
    }

    suspend fun loginUser(token: String): LoginResponse {
        val requestBody = mapOf(
            "token" to token
        )
        return apiService.loginUser(requestBody)
    }

    suspend fun logoutUser(authToken: String): LogoutResponse {
        return apiService.logoutUser("Bearer $authToken")
    }

    suspend fun getUserDetails(userId: String): Response<UserDetailResponse> {
        return apiService.getUserDetails(userId)
    }

    suspend fun updateUserDetails(userId: String, name: String): Response<UpdateUserResponse> {
        val requestBody = mapOf(
            "name" to name
        )
        return apiService.updateUserDetails(userId, requestBody)
    }

    suspend fun deleteUser(userId: String): DeleteUserResponse {
        return apiService.deleteUser(userId)
    }
}