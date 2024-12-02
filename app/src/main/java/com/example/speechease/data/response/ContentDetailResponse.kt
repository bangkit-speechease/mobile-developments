package com.example.speechease.data.response

data class ContentDetailResponse(
    val error: Boolean,
    val message: String,
    val data: List<ContentData>?
)
