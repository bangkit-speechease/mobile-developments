package com.example.speechease.data.repository

import com.example.speechease.data.pref.UserPreference
import com.example.speechease.data.response.AudioFeedbackResponse
import com.example.speechease.data.response.ContentDetailResponse
import com.example.speechease.data.response.ContentListResponse
import com.example.speechease.data.retrofit.ApiService
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import retrofit2.Response

class ContentRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun getContentList(): ContentListResponse {
        return apiService.getContentList()
    }

    suspend fun getContentDetails(contentId: String): Response<ContentDetailResponse> {
        return apiService.getContentDetails(contentId)
    }

    suspend fun submitAudioFeedback(audioFile: MultipartBody.Part): Response<AudioFeedbackResponse> {
        val token = userPreference.getSession().first().token
        return apiService.submitAudioFeedback(audioFile, "Bearer $token")
    }
}