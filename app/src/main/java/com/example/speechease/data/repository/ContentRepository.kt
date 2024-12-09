package com.example.speechease.data.repository

import com.example.speechease.data.pref.UserPreference
import com.example.speechease.data.response.AudioFeedbackResponse
import com.example.speechease.data.response.ContentDetailResponse
import com.example.speechease.data.response.ContentListResponse
import com.example.speechease.data.retrofit.ApiService
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class ContentRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun getContentList(): ContentListResponse {
        val token = userPreference.getSession().first().token
        return apiService.getContentList("Bearer $token")
    }

    suspend fun getContentDetails(contentId: String): Response<ContentDetailResponse> {
        val token = userPreference.getSession().first().token
        return apiService.getContentDetails(contentId, "Bearer $token")
    }

    suspend fun submitAudioFeedback(
        file: MultipartBody.Part,
        userId: String,
        contentId: String,
        token: String
    ): Response<AudioFeedbackResponse> {
        return apiService.submitAudioFeedback(
            file = file,
            userId = userId.toRequestBody("text/plain".toMediaTypeOrNull()),
            contentId = contentId.toRequestBody("text/plain".toMediaTypeOrNull()),
            authorization = "Bearer $token"
        )
    }
}