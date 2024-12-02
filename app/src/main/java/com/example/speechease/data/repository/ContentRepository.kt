package com.example.speechease.data.repository

import com.example.speechease.data.response.AudioFeedbackResponse
import com.example.speechease.data.response.ContentDetailResponse
import com.example.speechease.data.response.ContentListResponse
import com.example.speechease.data.retrofit.ApiService
import okhttp3.MultipartBody
import retrofit2.Response

class ContentRepository(private val apiService: ApiService) {

    suspend fun getContentList(): ContentListResponse {
        return apiService.getContentList()
    }

    suspend fun getContentDetails(contentId: String): Response<ContentDetailResponse> {
        return apiService.getContentDetails(contentId)
    }

    suspend fun submitAudioFeedback(audioFile: MultipartBody.Part): Response<AudioFeedbackResponse> {
        return apiService.submitAudioFeedback(audioFile)
    }
}