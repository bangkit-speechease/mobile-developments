package com.example.speechease.di

import android.content.Context
import com.example.speechease.data.repository.ContentRepository
import com.example.speechease.data.repository.UserRepository
import com.example.speechease.data.retrofit.ApiConfig

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        return UserRepository(apiService)
    }

    fun provideContentRepository(context: Context): ContentRepository {
        val apiService = ApiConfig.getApiService()
        return ContentRepository(apiService)
    }
}
