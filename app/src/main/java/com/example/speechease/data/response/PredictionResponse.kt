package com.example.speechease.data.response

data class PredictionResponse(
    val prediction_score: Float,
    val predicted_label: String
)