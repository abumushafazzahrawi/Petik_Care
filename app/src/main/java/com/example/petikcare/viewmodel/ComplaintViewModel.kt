package com.example.petikcare.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petikcare.data.local.entity.ComplaintEntity
import com.example.petikcare.data.local.room.ComplaintDao
import com.example.petikcare.data.remote.ComplaintRepository
import com.example.petikcare.data.remote.Result
import com.example.petikcare.event.Event
import com.example.petikcare.response_complaint.RespondRequest
import com.example.petikcare.response_complaint.ResponseDetailComplaint
import com.example.retrofit.ApiConfig
import com.example.retrofit.ApiService
import kotlinx.coroutines.launch

class ComplaintViewModel(
    private val repository: ComplaintRepository)
    : ViewModel() {

    val complaints: LiveData<List<ComplaintEntity>> = repository.getComplaints()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<Event<String?>?>()
    val errorMessage: LiveData<Event<String?>?> = _errorMessage

    private val _responseComplaint = MutableLiveData<Event<ResponseDetailComplaint?>?>()
    val responseComplaint: LiveData<Event<ResponseDetailComplaint?>?> = _responseComplaint

    fun refreshComplaints() {
        viewModelScope.launch {
            repository.refreshFromApi()
        }
    }

    fun respondComplaint(id: String, request: RespondRequest) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.complaintRespond(id, request)
                if (response.isSuccessful) {
                    repository.refreshFromApi()
                    _responseComplaint.value = Event(response.body())
                } else {
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = Event("Error: ${e.message}")
            }
        }
    }

    fun revertComplaint(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.revertComplaint(id)
                if (response.isSuccessful) {
                    _errorMessage.value = Event("Berhasil membatalkan")
                    repository.refreshFromApi()
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Gagal: ${e.message}")

            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fungsi tambahan untuk mereset error setelah di tampilkan
    fun clearError() {
        _errorMessage.value = null
    }

    fun clearResponse() {
        _responseComplaint.value = null
    }
}