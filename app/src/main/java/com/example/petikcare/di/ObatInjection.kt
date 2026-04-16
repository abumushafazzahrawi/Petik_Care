package com.example.petikcare.di

import android.content.Context
import com.example.petikcare.data.local.room.ObatDatabase
import com.example.petikcare.data.remote.ObatRepository
import com.example.retrofit.ApiConfig

object ObatInjection {
    fun provideObatRepository(context: Context): ObatRepository {
        val apiService = ApiConfig.getApiService(context)
        val database = ObatDatabase.getInstance(context)
        val dao = database.obatDao()

        return ObatRepository.getInstance(apiService, dao)

    }
}