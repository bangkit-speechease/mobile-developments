package com.example.speechease.ui.practice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechease.data.repository.ContentRepository
import com.example.speechease.data.response.ContentData
import kotlinx.coroutines.launch

class PracticeViewModel(
    private val repository: ContentRepository
) : ViewModel() {

    private val _practiceList = MutableLiveData<List<ContentData>?>()
    val practiceList: MutableLiveData<List<ContentData>?> = _practiceList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchPracticeList() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getContentList()
                _isLoading.value = false

                if (response.data != null) {
                    _practiceList.value = response.data
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Gagal memuat data: ${e.message}"
            }
        }
    }
}