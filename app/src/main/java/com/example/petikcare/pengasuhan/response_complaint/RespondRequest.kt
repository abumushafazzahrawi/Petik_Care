package com.example.petikcare.pengasuhan.response_complaint

import com.google.gson.annotations.SerializedName

data class RespondRequest(
    val status: String,
    val catatan: String,
    val medicines: List<MedicinesList>
)

data class MedicinesList(
    @SerializedName("medicine_id")
    val medicineId: String,
    val quantity: Int
)