package com.example.speechease.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _navigateToFragment = MutableLiveData<Int?>()
    val navigateToFragment: LiveData<Int?> get() = _navigateToFragment

    fun navigateTo(destinationId: Int) {
        _navigateToFragment.value = destinationId
    }

    fun resetNavigation() {
        _navigateToFragment.value = null
    }
}
