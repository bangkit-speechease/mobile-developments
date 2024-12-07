package com.example.speechease.di

import android.content.Context
import com.example.speechease.data.pref.UserPreference
import com.example.speechease.data.repository.ContentRepository
import com.example.speechease.data.repository.UserRepository
import com.example.speechease.data.retrofit.ApiConfig
import com.example.speechease.ui.practicedetail.PracticeDetailViewModel

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context)
        val apiService = ApiConfig.getApiService(context, userPreference)
        return UserRepository.getInstance(context, apiService)
    }

    fun provideContentRepository(context: Context): ContentRepository {
        val userPreference = UserPreference.getInstance(context)
        val apiService = ApiConfig.getApiService(context, userPreference)
        return ContentRepository(apiService, userPreference)
    }

    fun providePracticeDetailViewModel(context: Context): PracticeDetailViewModel {
        val userPreference = UserPreference.getInstance(context)
        val apiService = ApiConfig.getApiService(context, userPreference)
        val contentRepository = provideContentRepository(context)
        return PracticeDetailViewModel(apiService, userPreference, contentRepository)
    }
}
