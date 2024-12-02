package com.example.speechease.data.response

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val data: LoginData?
)

data class LoginData(
    val userId: String,
    val name: String,
    val email: String,
    val token: String
)