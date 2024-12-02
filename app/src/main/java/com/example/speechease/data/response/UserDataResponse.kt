package com.example.speechease.data.response

data class UserDataResponse(
    val status: String,
    val message: String,
    val error: Boolean,
    val data: UserProfile
)

data class UserProfile(
    val email: String,
    val name: String,
    val gender: String,
    val country: String,
    val address: String
)