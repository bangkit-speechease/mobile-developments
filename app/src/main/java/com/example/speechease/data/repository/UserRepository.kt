package com.example.speechease.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.speechease.data.pref.UserModel
import com.example.speechease.data.pref.UserPreference
import com.example.speechease.data.pref.dataStore
import com.example.speechease.data.response.DeleteUserResponse
import com.example.speechease.data.response.LoginResponse
import com.example.speechease.data.response.LogoutResponse
import com.example.speechease.data.response.RegisterResponse
import com.example.speechease.data.response.UpdateUserResponse
import com.example.speechease.data.response.UserDetailResponse
import com.example.speechease.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class UserRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

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

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
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

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            context: Context,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, UserPreference.getInstance(context.dataStore))
            }.also { instance = it }
    }
}