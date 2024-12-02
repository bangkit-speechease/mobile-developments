package com.example.speechease.data.retrofit

import com.example.speechease.data.response.LoginResponse
import com.example.speechease.data.response.PredictionResponse
import com.example.speechease.data.response.RegisterResponse
import com.example.speechease.data.response.UserDataResponse
import com.example.speechease.data.response.UserDetailResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    // Endpoint untuk mengunggah file audio dan mendapatkan prediksi
    @Multipart
    @POST("predict")
    fun uploadAudio(
        @Part file: MultipartBody.Part
    ): Call<PredictionResponse>

    // Endpoint untuk Register Pengguna
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    // Endpoint untuk Login Pengguna
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    // Endpoint untuk Data Pengguna
    @GET("user_data/{{email}}")
    fun getUserData(
        @Query("email") email: String)
    : Call<UserDataResponse>

    // Endpoint untuk Detail Pengguna
    /*@POST("/add_user")
    fun createUserProfile(
        @Body userProfileRequest: UserProfileRequest)
    : Call<UserDetailResponse>*/
}