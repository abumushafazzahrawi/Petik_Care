package com.example.petikcare.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petikcare.event.Event
import com.example.petikcare.response_complaint.DataComplaints
import com.example.petikcare.response_complaint.RespondRequest
import com.example.petikcare.response_complaint.ResponseDetailComplaint
import com.example.retrofit.ApiConfig
import kotlinx.coroutines.launch

class ComplaintViewModel : ViewModel() {
    private val _complaints = MutableLiveData<List<DataComplaints>>()
    val complaints: LiveData<List<DataComplaints>> = _complaints

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<Event<String?>>()
    val errorMessage: LiveData<Event<String?>> = _errorMessage

    private val _responseComplaint = MutableLiveData<Event<ResponseDetailComplaint?>>()
    val responseComplaint: LiveData<Event<ResponseDetailComplaint?>> = _responseComplaint


    fun getComplaints(context: Context, forceRefresh: Boolean = false) {
        // Jika data sudah ada bukan force refresh, jangan ambil lagi
        if (_complaints.value != null && !forceRefresh) return // Biar gak reload
        if (_isLoading.value == true) return

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // Dari Login
                val response = ApiConfig.getApiService(context).getAllComplaint()
                if (response.isSuccessful) {
                    _complaints.value = response.body()?.data
                } else if (response.code() == 401) {
                    _errorMessage.value = Event("Sesi habis, silahkan login kembali")
                } else {
                    _errorMessage.value = Event("Gagal mengambil data: ${response.message()}")
                }
            } catch (e: Exception) {
                // error koneksi internet biasanya disini
                _errorMessage.value = Event("Koneksi bermasalah: ${e.localizedMessage}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun respondComplaint(id: String, request: RespondRequest, context: Context) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService(context).respondComplaint(id, request)

                if (response.isSuccessful) {
                    _responseComplaint.value = Event(response.body())
                } else {
                    _errorMessage.value = Event("Gagal mengirim respon: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Koneksi error: ${e.localizedMessage}")
                e.printStackTrace()
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

    fun getDetailComplaint(id: String, context: Context) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService(context).getDetailComplaint(id)

                if (response.isSuccessful) {
                    _responseComplaint.value = Event(response.body())
                } else {
                    _errorMessage.value = Event("Gagal mengambil detail: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Koneksi error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false

            }
        }
    }
}