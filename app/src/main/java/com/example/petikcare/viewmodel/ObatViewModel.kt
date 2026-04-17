package com.example.petikcare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petikcare.data.local.entity.ObatEntity
import com.example.petikcare.data.remote.ObatRepository
import com.example.petikcare.data.remote.Result
import com.example.petikcare.event.Event
import com.example.petikcare.response_obat.ObatRequest
import com.example.petikcare.response_obat.ResponseRestockObat
import kotlinx.coroutines.launch

class ObatViewModel(val repository: ObatRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listObat = MutableLiveData<Event<List<ObatEntity>>>()
    val listObat: LiveData<Event<List<ObatEntity>>> = _listObat

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _restockResult = MutableLiveData<Event<ResponseRestockObat>>()
    val restockResult: LiveData<Event<ResponseRestockObat>> = _restockResult

    fun getAllObat() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
              repository.getObat().observeForever { result ->
                    when (result) {
                        is Result.Loading -> _isLoading.value = false
                        is Result.Success -> {
                            _isLoading.value = false
                            _listObat.value = Event(result.data)
                            // Convert Entity -> UI
                    }
                        is Result.Error -> {
                            _isLoading.value = false
                            _errorMessage.value = result.error
                        }
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Koneksi bermasalah: ${e.localizedMessage}"

            }
        }
    }

    fun createObat(request: ObatRequest) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.createObat(request)
                if (response.isSuccessful) {
                    getAllObat()
                } else {
                    _isLoading.value = false
                    _errorMessage.value = "Gagal membuat obat"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun restockObat(id: String, stok: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.restockObat(id, stok)
                if (response.isSuccessful) {
                    _restockResult.value = Event(response.body()!!)
                    getAllObat()
                } else {
                    _errorMessage.value = Event("Gagal restock: ${response.message()}").toString()
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Error: ${e.message}").toString()
            } finally {
                _isLoading.postValue(false)

            }
        }
    }
}