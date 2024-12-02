package com.example.speechease.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.speechease.data.retrofit.ApiService
import com.example.speechease.ui.practicedetail.PracticeDetailViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PracticeDetailViewModel::class.java)) {
            return PracticeDetailViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
