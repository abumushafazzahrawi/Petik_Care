package com.example.petikcare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import coil.transform.Transformation
import com.example.petikcare.data.local.entity.ObatEntity
import com.example.petikcare.data.remote.ObatRepository
import com.example.petikcare.data.remote.Result
import com.example.petikcare.event.Event
import com.example.petikcare.pengasuhan.response_obat.EditObat
import com.example.petikcare.pengasuhan.response_obat.ObatRequest
import com.example.petikcare.pengasuhan.response_obat.ResponseEditObat
import com.example.petikcare.pengasuhan.response_obat.ResponseRestockObat
import kotlinx.coroutines.launch

class ObatViewModel(val repository: ObatRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _refreshTrigger = MutableLiveData<Unit>()
    val listObat: LiveData<Result<List<ObatEntity>>> = _refreshTrigger.switchMap {
        repository.getObat()
    }
    init {
        refreshObat()
    }

    fun refreshObat() {
        _refreshTrigger.value = Unit
    }

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _restockResult = MutableLiveData<Event<ResponseRestockObat>>()
    val restockResult: LiveData<Event<ResponseRestockObat>> = _restockResult

    private val _editResult = MutableLiveData<Event<ResponseEditObat>>()
    val editResult: LiveData<Event<ResponseEditObat>> = _editResult

    fun createObat(request: ObatRequest) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.createObat(request)
                if (response.isSuccessful) {
                    refreshObat()
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
                    refreshObat()
                } else {
                    _errorMessage.value = "Gagal restock: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.postValue(false)

            }
        }
    }

    fun editObat(id:String, namaObat: String, sediaan: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = EditObat(namaObat, sediaan)
                val response = repository.editObat(id, request)
                if (response.isSuccessful) {
                    _editResult.value = Event(response.body()!!)
                    refreshObat()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal Edit: ${e.message}"
            } finally {
                _isLoading.value = false

            }
        }
    }
}