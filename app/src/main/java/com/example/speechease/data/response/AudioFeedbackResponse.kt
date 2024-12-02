package com.example.speechease.data.response

data class AudioFeedbackResponse(
    val error: Boolean,
    val message: String,
    val data: AudioFeedbackData? = null
)

data class AudioFeedbackData(
    val prediction_score: Float,
    val predicted_label: String
)