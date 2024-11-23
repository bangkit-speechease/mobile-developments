package com.example.speechease.data.retrofit

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    // latihan
//    @GET("exercises")
//    fun getExercises(): Call<List<ExerciseModel>>

    // interaktif

    // analisis pengiriman suara
//    @Multipart
//    @POST("analyze-voice")
//    fun uploadVoice(
//        @Part file: MultipartBody.Part
//    ): Call<VoiceAnalysisResponse>

    // mengambil hasil analisis
//    @GET("result/{sessionId}")
//    fun getAnalysisResult(
//        @Path("sessionId") sessionId: String
//    ): Call<AnalysisResult>

    // progress tracking
//    @POST("progress")
//    @FormUrlEncoded
//    fun submitProgress(
//        @Field("userId") userId: String,
//        @Field("exerciseId") exerciseId: String,
//        @Field("score") score: Int
//    ): Call<ProgressResponse>

}