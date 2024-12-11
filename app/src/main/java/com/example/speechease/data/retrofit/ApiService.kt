package com.example.speechease.data.retrofit

import com.example.speechease.data.response.AudioFeedbackResponse
import com.example.speechease.data.response.ContentDetailResponse
import com.example.speechease.data.response.ContentListResponse
import com.example.speechease.data.response.LoginResponse
import com.example.speechease.data.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @POST("/register")
    suspend fun registerUser(@Body requestBody: Map<String, String>): RegisterResponse

    @POST("/login")
    suspend fun loginUser(@Body requestBody: Map<String, String>): LoginResponse

    @GET("/content")
    suspend fun getContentList(
        @Header("Authorization") token: String
    ): ContentListResponse

    @GET("content/{contentId}")
    suspend fun getContentDetails(
        @Path("contentId") contentId: String,
        @Header("Authorization") token: String
    ): Response<ContentDetailResponse>

    @Multipart
    @POST("feedback")
    suspend fun submitAudioFeedback(
        @Part file: MultipartBody.Part,
        @Part("userId") userId: RequestBody,
        @Part("contentId") contentId: RequestBody,
        @Header("Authorization") authorization: String
    ): Response<AudioFeedbackResponse>

}