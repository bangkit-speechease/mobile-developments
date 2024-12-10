package com.example.speechease.ui.practicedetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechease.data.pref.UserPreference
import com.example.speechease.data.repository.ContentRepository
import com.example.speechease.data.response.ContentData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class PracticeDetailViewModel(
    private val userPreference: UserPreference,
    private val contentRepository: ContentRepository,
) : ViewModel() {

    private val _predictedLabel = MutableLiveData<String>()
    val predictedLabel: LiveData<String> get() = _predictedLabel

    private val _uploadStatus = MutableLiveData<String>()
    val uploadStatus: LiveData<String> get() = _uploadStatus

    private val _contentId = MutableLiveData<String?>()
    private val contentId: LiveData<String?> get() = _contentId

    private val _contentDetailState = MutableLiveData<ContentDetailState>()
    val contentDetailState: LiveData<ContentDetailState> = _contentDetailState

    private val _audioUrlState = MutableLiveData<AudioUrlState>(AudioUrlState.Loading)
    val audioUrlState: LiveData<AudioUrlState> get() = _audioUrlState


    fun setContentId(contentId: String?) {
        _contentId.value = contentId
    }

    fun fetchContentDetail(contentId: String) {
        _audioUrlState.value = AudioUrlState.Loading
        _contentDetailState.value = ContentDetailState.Loading
        viewModelScope.launch {
            try {
                val response = contentRepository.getContentDetails(contentId)
                if (response.isSuccessful && response.body() != null) {
                    val detailData = response.body()?.data?.firstOrNull()
                    if (detailData != null && detailData.audioGuideUrl != null) {
                        _audioUrlState.value = AudioUrlState.Success(detailData.audioGuideUrl)
                        _contentDetailState.value = ContentDetailState.Success(detailData)
                    } else {
                        _audioUrlState.value = AudioUrlState.Error("URL audio tidak tersedia")
                        _contentDetailState.value = ContentDetailState.Error("URL audio tidak tersedia")
                    }
                } else {
                    _audioUrlState.value = AudioUrlState.Error("Gagal mengambil data: ${response.message()}")
                    _contentDetailState.value = ContentDetailState.Error("Gagal mengambil data: ${response.message()}")
                }
            } catch (e: Exception) {
                _audioUrlState.value = AudioUrlState.Error("Error: ${e.message}")
                _contentDetailState.value = ContentDetailState.Error("Error: ${e.message}")
            }
        }
    }

    suspend fun uploadAudio(filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            _uploadStatus.value = "File audio tidak ditemukan."
            return
        }

        val requestFile = file.asRequestBody("audio/wav".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val token = userPreference.getSession().first().token
        val userId = userPreference.getSession().first().userId
        val contentIdValue = _contentId.value ?: return

        try {

            Log.d("PracticeDetailViewModel", "Mengirim request ke API...")
            val response = contentRepository.submitAudioFeedback(
                file = multipartBody,
                userId = userId,
                contentId = contentIdValue,
                token = token
            )
            Log.d("PracticeDetailViewModel", "Request URL: https://speechease-iw10810.et.r.appspot.com/feedback")
            Log.d("PracticeDetailViewModel", "Request Payload: userId=$userId, contentId=$contentId, token=$token")
            Log.d("PracticeDetailViewModel", "Respons dari API: $response")
            if (response.isSuccessful && response.body() != null) {
                val audioResponse = response.body()!!
                // Correctly access predicted_label from feedback
                val label = audioResponse.feedback?.predictedLabel ?: "Label tidak tersedia"

                Log.d("PracticeDetailViewModel", "Predicted Label: $label")
                _predictedLabel.value = label
                _uploadStatus.value = "Audio diproses dengan sukses."
            } else {
                Log.e("PracticeDetailViewModel", "Response gagal: ${response.message()}")
                _uploadStatus.value = "Gagal memproses audio. ${response.message()}"
            }

        } catch (e: Exception) {
            Log.e("PracticeDetailViewModel", "Error saat mengirim request ke API: ${e.message}", e)
            _uploadStatus.value = "Terjadi kesalahan: ${e.message}"
        }
    }
}