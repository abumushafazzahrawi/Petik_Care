package com.example.petikcare.data.remote

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.petikcare.data.local.entity.ComplaintEntity
import com.example.petikcare.data.local.room.ComplaintDao
import com.example.petikcare.pengasuhan.response_complaint.RespondRequest
import com.example.petikcare.pengasuhan.response_complaint.ResponseDetailComplaint
import com.example.petikcare.pengasuhan.response_complaint.ResponseGetMyComplaints
import com.example.petikcare.pengasuhan.response_complaint.RevertResponse
import com.example.petikcare.santri.RequestCreateComplaints
import com.example.petikcare.santri.ResponseComplaintsSantri
import com.example.petikcare.santri.ResponseDeleteComplaintSantri
import com.example.petikcare.utils.NotificationHelper
import com.example.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import java.lang.Exception
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import retrofit2.Response

class ComplaintRepository(
    private val apiService: ApiService,
    private val complaintDao: ComplaintDao,
    private val context: Context
) {

    private val _isLoading = MutableLiveData<Boolean>()

    fun getComplaints(): LiveData<List<ComplaintEntity>> {
        return complaintDao.getAllComplaints()
    }

    suspend fun refreshFromApi() = withContext(Dispatchers.IO) {
        _isLoading.postValue(true)
        try {
            val response = apiService.getAllComplaint()
            if (response.isSuccessful) {
                val responseData = response.body()?.data ?: emptyList()
                val currentLocalData = complaintDao.getAllComplaintsList()

                val complaints = responseData.map { item ->
                    val treatment = item.treatment
                    val medicines = treatment?.medicinesGiven

                    val existing = currentLocalData.find { it.id == item.id }

                    // Notifikasi jika status berubah dari PENDING ke SELESAI
                    if (existing?.status == "PENDING" && item.status == "SELESAI") {
                        NotificationHelper.showNotification(context, "Keluhan Selesai", "Keluhan Anda telah ditangani")
                    }

                    val medicineNames = medicines?.joinToString(", ") { it.name }
                        ?: existing?.medicineName

                    val medicineQuantities = medicines?.joinToString(", ") { it.quantity.toString() }
                        ?: existing?.medicineQuantity

                    ComplaintEntity(
                        id = item.id,
                        namaSantri = item.santri.name,
                        title = item.title,
                        description = item.description,
                        status = item.status,
                        createdAt = item.createdAt,
                        handledNote = treatment?.note ?: existing?.handledNote ?: item.handledNote,
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

                // Gunakan withContext agar update DB ditunggu sampai selesai sebelum return
                withContext(Dispatchers.IO) {
                    val existing = complaintDao.getComplaintById(id)
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
            withContext(Dispatchers.IO) {
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

    suspend fun createComplaintSantri(request: RequestCreateComplaints): Response<ResponseComplaintsSantri> {
        val response = apiService.createComplaints(request)
        if (response.isSuccessful) {
            val item = response.body()?.data
            if (item != null) {
                val sharedPref = context.getSharedPreferences("petikCare", Context.MODE_PRIVATE)
                val username = sharedPref.getString("USERNAME", "Saya") ?: "Saya"
                withContext(Dispatchers.IO) {
                    val newComplaint = ComplaintEntity(
                        id = item.id,
                        namaSantri = username,
                        title = item.title,
                        description = item.description,
                        status = item.status,
                        createdAt = item.createdAt,
                        handledNote = null,
                        handledAt = null,
                        medicineName = null,
                        medicineQuantity = null
                    )
                    complaintDao.insertComplaints(listOf(newComplaint))
                }
            }
        }
        return response
    }

    suspend fun deleteComplaintSantri(id: String): Response<ResponseDeleteComplaintSantri> {
        val response = apiService.deleteComplaints(id)
        if (response.isSuccessful) {
            withContext(Dispatchers.IO) {
                complaintDao.deleteComplaintById(id)
            }
        } else {
            complaintDao.deleteComplaintById(id)
        }
        return response
    }

    suspend fun getMyComplaint(): Response<ResponseGetMyComplaints> {
        val response = apiService.getMyComplaints()
        if (response.isSuccessful) {
            val items = response.body()?.data
            if (items != null) {
                val sharedPref = context.getSharedPreferences("petikCare", Context.MODE_PRIVATE)
                val username = sharedPref.getString("USERNAME", "Saya") ?: "Saya"
                withContext(Dispatchers.IO) {
                    val complaints = items.map { item ->
                        ComplaintEntity(
                            id = item.id,
                            namaSantri = username,
                            title = item.title,
                            description = item.description,
                            status = item.status,
                            createdAt = item.createdAt,
                            handledNote = null,
                            handledAt = null,
                            medicineName = null,
                            medicineQuantity = null
                        )
                    }
                    complaintDao.insertComplaints(complaints)
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