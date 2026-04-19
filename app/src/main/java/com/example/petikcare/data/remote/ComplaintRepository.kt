package com.example.petikcare.data.remote

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.petikcare.data.local.entity.ComplaintEntity
import com.example.petikcare.data.local.entity.MedicineEntity
import com.example.petikcare.data.local.room.ComplaintDao
import com.example.petikcare.response_complaint.DataComplaints
import com.example.petikcare.response_complaint.RespondRequest
import com.example.petikcare.response_complaint.ResponseComplaints
import com.example.petikcare.response_complaint.ResponseDetailComplaint
import com.example.petikcare.response_complaint.RevertResponse
import com.example.petikcare.utils.NotificationHelper
import com.example.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import java.lang.Exception
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import retrofit2.Response

class ComplaintRepository(
    private val apiService: ApiService,
    private val complaintDao: ComplaintDao,
    private val context: Context
) {

    private val _isLoading = MutableLiveData<Boolean>()

    fun getComplaints(): LiveData<List<ComplaintEntity>> {
        refreshFromApi()
        return complaintDao.getAllComplaints()
    }

    fun refreshFromApi() {
        CoroutineScope(Dispatchers.IO).launch {
            _isLoading.postValue(true)
            try {
                val response = apiService.getAllComplaint()
                if (response.isSuccessful) {
                    val responseData = response.body()?.data ?: emptyList()

                    // Ambil data lokal saat ini sekali saja untuk pengecekan
                    val currentLocalData =
                        complaintDao.getAllComplaintsList() // Gunakan fungsi List biasa, bukan LiveData

                    val complaints = responseData.map { item ->
                        val treatment = item.treatment
                        val medicines = treatment?.medicinesGiven
                        val wasPending = currentLocalData.find { it.id == item.id }?.status == "PENDING"
                        if (wasPending && item.status == "SELESAI") {
                            NotificationHelper.showNotification(context, "Keluhan Selesai", "Keluhan Anda telah ditangani")
                        }

                        // Cari apakah di database lokal sudah ada data obat untuk ID ini
                        val existing = currentLocalData.find { it.id == item.id }

                        val medicineNames = medicines?.joinToString(", ") { it.name }
                            ?: existing?.medicineName // Jika API null, pakai yang sudah ada di lokal

                        val medicineQuantities =
                            medicines?.joinToString(", ") { it.quantity.toString() }
                                ?: existing?.medicineQuantity // Jika API null, pakai yang sudah ada di lokal

                        // RETURN entity agar masuk ke list 'complaints'
                        ComplaintEntity(
                            id = item.id,
                            namaSantri = item.santri.name,
                            title = item.title,
                            description = item.description,
                            status = item.status,
                            createdAt = item.createdAt,
                            handledNote = treatment?.note ?: existing?.handledNote
                            ?: item.handledNote,
                            handledAt = item.handledAt ?: existing?.handledAt,
                            medicineName = medicineNames,
                            medicineQuantity = medicineQuantities
                        )
                    }

                    complaintDao.insertComplaints(complaints)
                }
            } catch (e: Exception) {
                Log.e("REPO_DEBUG", "Error refresh: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    suspend fun complaintRespond(
        id: String,
        request: RespondRequest
    ): Response<ResponseDetailComplaint> {
        val response = apiService.respondComplaint(id, request)

        if (response.isSuccessful) {
            val data = response.body()?.data
            if (data != null) {
                val treatment = data.treatment
                val medicines = treatment.medicinesGiven

                val medicineNames = medicines.joinToString(", ") { it.name }
                val medicineQuantities = medicines.joinToString(", ") { it.quantity.toString() }

                // Langsung update database lokal agar tidak perlu nunggu refresh API
                CoroutineScope(Dispatchers.IO).launch {
                    val existing =
                        complaintDao.getComplaintById(id) // Pastikan fungsi ini ada di Dao
                    if (existing != null) {
                        val updated = ComplaintEntity(
                            id = existing.id,
                            namaSantri = existing.namaSantri,
                            title = existing.title,
                            description = existing.description,
                            status = "SELESAI",
                            createdAt = existing.createdAt,
                            handledNote = treatment.note,
                            handledAt = data.handledAt,
                            medicineName = medicineNames,
                            medicineQuantity = medicineQuantities
                        )
                        complaintDao.insertComplaints(listOf(updated))
                    }
                }
            }
        }
        return response

    }
    suspend fun revertComplaint(id: String): Response<RevertResponse> {
        val response = apiService.revertComplaint(id)

            if (response.isSuccessful) {
                CoroutineScope(Dispatchers.IO).launch {
                    val existing = complaintDao.getComplaintById(id)
                    if (existing != null) {
                        val updated = ComplaintEntity(
                            id = existing.id,
                            namaSantri = existing.namaSantri,
                            title = existing.title,
                            description = existing.description,
                            status = "PENDING",
                            createdAt = existing.createdAt,
                            handledNote = null,
                            handledAt = null,
                            medicineName = null,
                            medicineQuantity = null
                        )
                        complaintDao.insertComplaints(listOf(updated))
                    }

                }
            }

        return response
        }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: ComplaintRepository? = null
            fun getInstance(
                apiService: ApiService,
                dao: ComplaintDao,
                context: Context
            ): ComplaintRepository =
                instance ?: synchronized(this) {
                    instance ?: ComplaintRepository(apiService, dao, context)
                        .also { instance = it }
                }
        }
}