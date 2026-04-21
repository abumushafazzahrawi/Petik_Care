package com.example.petikcare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petikcare.data.local.entity.ComplaintEntity
import com.example.petikcare.data.remote.ComplaintRepository
import com.example.petikcare.event.Event
import com.example.petikcare.pengasuhan.response_complaint.RespondRequest
import com.example.petikcare.pengasuhan.response_complaint.ResponseDetailComplaint
import com.example.petikcare.santri.RequestCreateComplaints
import com.example.petikcare.santri.ResponseComplaintsSantri
import kotlinx.coroutines.launch

class ComplaintViewModel(
    private val repository: ComplaintRepository)
    : ViewModel() {

    val complaints: LiveData<List<ComplaintEntity>> = repository.getComplaints()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _errorMessage = MutableLiveData<Event<String?>?>()
    val errorMessage: LiveData<Event<String?>?> = _errorMessage

    private val _createComplaintSantri = MutableLiveData<ResponseComplaintsSantri>()
    val createComplaintSantri: LiveData<ResponseComplaintsSantri> = _createComplaintSantri

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

    fun createComplaintSantri(request: RequestCreateComplaints) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createComplaintSantri(request)
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        _message.value = Event("Keluhan berhasil dibuat")
                        _createComplaintSantri.value = result
                } else {
                        if (response.code() == 401) {
                            _errorMessage.value = Event("Sesi habis, silahkan login kembali")
                        } else {
                            _errorMessage.value = Event("Gagal: ${response.message()}")
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Koneksi bermasalah")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun deleteComplaintSantri(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.deleteComplaintSantri(id)
                if (response.isSuccessful) {
                    _message.value = Event("Keluhan berhasil dihapus")
                    repository.refreshFromApi()
                } else {
                    _errorMessage.value = Event("Gagal menghapus keluhan")
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Gagal: ${e.message}")
            } finally {
                _isLoading.postValue(false)
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