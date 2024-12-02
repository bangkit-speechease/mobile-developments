package com.example.speechease.data.response

data class RegisterResponse(
    val error: Boolean,
    val message: String,
    val data: RegisterData?
)

data class RegisterData(
    val userId: String,
    val name: String,
    val email: String
)
