package com.example.petikcare.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.petikcare.data.local.entity.ObatEntity
import com.example.petikcare.data.local.room.ObatDao
import com.example.petikcare.response_obat.ObatRequest
import com.example.petikcare.response_obat.ResponseEditObat
import com.example.petikcare.response_obat.ResponseRestockObat
import com.example.petikcare.response_obat.RestockObat
import com.example.retrofit.ApiService
import retrofit2.Response

class ObatRepository(
    private val apiService: ApiService,
    private val obatDao: ObatDao
) {
    fun getObat(): LiveData<Result<List<ObatEntity>>> = liveData {
        emit(Result.Loading)

        val localData = obatDao.getAllObat()
        if (localData.isNotEmpty()) {
            emit(Result.Success(localData))
        }

        try {
            val response = apiService.getAllObat()
            if (response.isSuccessful) {
                val body = response.body()
                val data = body?.data?.map { item ->
                    ObatEntity(
                        id = item.id,
                        name = item.name,
                        description = item.description,
                        stock = item.stock,
                        createdAt = item.createdAt,
                        updatedAt = item.updatedAt
                    )
                } ?: emptyList()

                obatDao.insertObat(data)

                val updateData = obatDao.getAllObat()
                if (updateData.isNotEmpty()) {
                    emit(Result.Success(updateData))
                }
            } else {
                emit(Result.Error("Gagal mengambil data"))
            }
        } catch (e: Exception) {
            if (localData.isEmpty()) {
                emit(Result.Error("Tidak ada koneksi & data kosong"))
            }
        }
    }

    suspend fun createObat(request: ObatRequest) = apiService.createObat(request)

    suspend fun restockObat(id: String, stok: Int): Response<ResponseRestockObat> {
        val request = RestockObat(stok)
        return apiService.restockObat(id, request)
    }

    companion object {
        @Volatile
        private var instance: ObatRepository? = null
        fun getInstance(
            apiService: ApiService,
            dao: ObatDao
        ): ObatRepository =
            instance ?: synchronized(this) {
                instance ?: ObatRepository(apiService, dao)
                    .also { instance = it }
            }
    }
}