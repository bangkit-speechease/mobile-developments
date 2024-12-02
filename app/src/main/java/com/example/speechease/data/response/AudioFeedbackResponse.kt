package com.example.speechease.data.response

data class AudioFeedbackResponse(
    val error: Boolean = false,
    val message: String,
    val feedback: AudioFeedbackData? = null
)

data class AudioFeedbackData(
    val prediction_score: Double,
    val predicted_label: String
)