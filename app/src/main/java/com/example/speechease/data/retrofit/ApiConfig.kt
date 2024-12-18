package com.example.speechease.data.retrofit

import com.example.speechease.data.pref.AuthInterceptor
import com.example.speechease.data.pref.UserPreference
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    private const val BASE_URL = "https://speechease-iw10810.et.r.appspot.com/"

    fun getApiService(userPreference: UserPreference): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = AuthInterceptor(userPreference)

        val client = OkHttpClient.Builder()
            .apply {
                addInterceptor(loggingInterceptor)
                addInterceptor(authInterceptor)
                connectTimeout(300, TimeUnit.SECONDS)
                readTimeout(300, TimeUnit.SECONDS)
                writeTimeout(300, TimeUnit.SECONDS)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}