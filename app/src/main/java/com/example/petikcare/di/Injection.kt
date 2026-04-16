package com.example.petikcare.di

import android.content.Context
import com.example.petikcare.data.local.room.ComplaintDatabase
import com.example.petikcare.data.remote.ComplaintRepository
import com.example.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): ComplaintRepository {
        val apiService = ApiConfig.getApiService(context)
        val database = ComplaintDatabase.getInstance(context)
        val dao = database.complaintDao()

        return ComplaintRepository.getInstance(apiService, dao)

    }
}