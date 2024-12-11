package com.example.speechease.ui.practicedetail

sealed class AudioUrlState {
    data object Loading : AudioUrlState()
    data class Success(val audioUrl: String) : AudioUrlState()
    data class Error(val message: String) : AudioUrlState()
}