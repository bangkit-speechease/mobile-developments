package com.example.speechease.data.response

data class AudioFeedbackResponse(
    val error: Boolean = false,
    val message: String,
    val feedback: AudioFeedbackData? = null
)

data class AudioFeedbackData(
    val feedbackId: String,
    val predictionScore: Double,
    val predictedLabel: String,
    val starScore: Int
)