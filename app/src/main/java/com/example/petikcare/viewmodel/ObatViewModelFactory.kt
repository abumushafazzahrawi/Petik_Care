package com.example.petikcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.petikcare.data.remote.ObatRepository

class ObatViewModelFactory(private val repository: ObatRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ObatViewModel::class.java)) {
            return ObatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}