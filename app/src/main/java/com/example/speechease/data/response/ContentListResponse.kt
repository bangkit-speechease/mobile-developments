package com.example.speechease.data.response

data class ContentListResponse(
    val error: Boolean,
    val message: String,
    val data: List<ContentData>?
)

data class ContentData(
    val documentId: String,
    val title: String,
    val description: String,
    val contentType: String,
    val imageUrl: String,
    val textPhrase: String,
    val audioGuideUrl: String,
    val recordInstructionEndpoint: String
)