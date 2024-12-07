package com.example.speechease.data.response

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val token: String
)