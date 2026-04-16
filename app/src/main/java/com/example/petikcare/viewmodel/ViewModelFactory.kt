package com.example.petikcare.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.petikcare.data.local.room.ComplaintDao
import com.example.petikcare.data.remote.ComplaintRepository
import com.example.petikcare.di.Injection
import com.example.retrofit.ApiService

class ViewModelFactory(
    private val repository: ComplaintRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ComplaintViewModel::class.java)) {
            return ComplaintViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

//    companion object {
//        @Volatile
//        private var instance: ViewModelFactory? = null
//        fun getInstance(context: Context): ViewModelFactory =
//            instance?: synchronized(this) {
//                instance?: ViewModelFactory(Injection.provideRepository(context))
//            }.also { instance = it }
//    }
//}