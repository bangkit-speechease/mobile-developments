package com.example.speechease.data.response

data class UpdateUserResponse(
    val error: Boolean,
    val message: String,
    val data: UpdateUserData?
)

data class UpdateUserData(
    val userId: String,
    val name: String
)
