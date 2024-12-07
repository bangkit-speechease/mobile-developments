package com.example.speechease.data.pref

import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val userPreference: UserPreference) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { userPreference.getSession().first().token } // Ambil token dari UserPreference
        val request = chain.request()
        Log.d("AuthInterceptor", "Token: $token") // Tambahkan log untuk menampilkan token

        val requestHeaders = request.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        Log.d("AuthInterceptor", "Intercepting request: ${request.url}")
        Log.d("AuthInterceptor", "Adding Authorization header: Bearer $token")

        return chain.proceed(requestHeaders)
    }
}