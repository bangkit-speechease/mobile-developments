package com.example.speechease.data.pref

import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val userPreference: UserPreference) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { userPreference.getSession().first().token } ?: "" // Ambil token atau set "" jika null

        val request = originalRequest.newBuilder()
            .apply {
                if (token.isNotEmpty()) {
                    header("Authorization", "Bearer $token")
                }
            }
            .build()

        Log.d("AuthInterceptor", "Intercepting request: ${request.url}")
        Log.d("AuthInterceptor", "Adding Authorization header: Bearer $token")

        return chain.proceed(request)
    }
}