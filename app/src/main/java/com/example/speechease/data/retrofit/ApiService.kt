package com.example.speechease.data.retrofit

import com.example.speechease.data.response.AudioFeedbackResponse
import com.example.speechease.data.response.ContentDetailResponse
import com.example.speechease.data.response.ContentListResponse
import com.example.speechease.data.response.DeleteUserResponse
import com.example.speechease.data.response.LoginResponse
import com.example.speechease.data.response.LogoutResponse
import com.example.speechease.data.response.RegisterResponse
import com.example.speechease.data.response.UpdateUserResponse
import com.example.speechease.data.response.UserDetailResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @POST("/register")
    suspend fun registerUser(@Body requestBody: Map<String, String>): RegisterResponse

    @POST("/login")
    suspend fun loginUser(@Body requestBody: Map<String, String>): LoginResponse

    @POST("/logout")
    suspend fun logoutUser(@Header("Authorization") token: String): LogoutResponse

    @GET("users/{userId}")
    suspend fun getUserDetails(@Path("userId") userId: String): Response<UserDetailResponse>

    @PUT("users/{userId}")
    suspend fun updateUserDetails(
        @Path("userId") userId: String,
        @Body requestBody: Map<String, String>
    ): Response<UpdateUserResponse>

    @DELETE("/delete/{userId}")
    suspend fun deleteUser(@Path("userId") userId: String): DeleteUserResponse

    @GET("/content")
    suspend fun getContentList(): ContentListResponse

    @GET("content/{contentId}")
    suspend fun getContentDetails(@Path("contentId") contentId: String): Response<ContentDetailResponse>

    @Headers("Content-Type: multipart/form-data")
    @Multipart
    @POST("feedback")
    suspend fun submitAudioFeedback(
        @Part file: MultipartBody.Part,
        @Header("Authorization") authorization: String
    ): Response<AudioFeedbackResponse>
}