package com.example.speechease.ui.practicedetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.speechease.data.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class PracticeDetailViewModel(private val apiService: ApiService) : ViewModel() {

    private val _predictedLabel = MutableLiveData<String>()
    val predictedLabel: LiveData<String> get() = _predictedLabel

    private val _uploadStatus = MutableLiveData<String>()
    val uploadStatus: LiveData<String> get() = _uploadStatus

    suspend fun uploadAudio(filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            _uploadStatus.value = "File audio tidak ditemukan."
            return
        }

        val requestFile = file.asRequestBody("audio/wav".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)

        try {
            val response = apiService.submitAudioFeedback(multipartBody)
            if (response.isSuccessful && response.body() != null) {
                val audioResponse = response.body()!!
                // Correctly access predicted_label from feedback
                val label = audioResponse.feedback?.predicted_label ?: "Label tidak tersedia"

                Log.d("PracticeDetailViewModel", "Predicted Label: $label")
                _predictedLabel.value = label
                _uploadStatus.value = "Audio diproses dengan sukses."
            } else {
                Log.e("PracticeDetailViewModel", "Response gagal: ${response.message()}")
                _uploadStatus.value = "Gagal memproses audio. ${response.message()}"
            }

        } catch (e: Exception) {
            _uploadStatus.value = "Terjadi kesalahan: ${e.message}"
        }
    }
}