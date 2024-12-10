package com.example.speechease.ui.practicedetail

import com.example.speechease.data.response.ContentData

sealed class ContentDetailState {
    object Loading : ContentDetailState()
    data class Success(val contentDetail: ContentData) : ContentDetailState()
    data class Error(val message: String) : ContentDetailState()
}