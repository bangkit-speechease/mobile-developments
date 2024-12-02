package com.example.speechease.data.response

data class UserDetailResponse(
    val error: Boolean,
    val message: String,
    val data: UserDetailData?
)

data class UserDetailData(
    val userId: String,
    val name: String,
    val email: String
)