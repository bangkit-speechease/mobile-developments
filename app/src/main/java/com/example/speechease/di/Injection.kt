package com.example.speechease.di

import android.content.Context
import com.example.speechease.data.pref.UserPreference
import com.example.speechease.data.pref.dataStore
import com.example.speechease.data.repository.ContentRepository
import com.example.speechease.data.repository.UserRepository
import com.example.speechease.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        //val apiService = ApiConfig.getApiService()
        val apiService = ApiConfig.getApiService(context)
        return UserRepository.getInstance(context, apiService)
    }
}
