package com.example.speechease.data.response

data class LoginResponse(
    val status: String,
    val message: String,
    val error: Boolean,
    val data: UserData
)

data class UserData(
    val userId: String,
    val name: String,
    val token: String
)