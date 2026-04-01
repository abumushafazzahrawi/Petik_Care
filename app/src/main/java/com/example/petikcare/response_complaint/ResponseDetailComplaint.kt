package com.example.petikcare.response_complaint

import com.google.gson.annotations.SerializedName

data class ResponseDetailComplaint(
    val success: Boolean,
    val message: String,
    val data: DataDetailComplaint
)

data class DataDetailComplaint(
    @SerializedName("complaint_id")
    val complaintId: String,
    val status: String,
    @SerializedName("handled_at")
    val handledAt: String,
    val treatment: TreatmentDetail
)

data class TreatmentDetail(
    val note: String,
    @SerializedName("medicines_given")
    val medicinesGiven: List<MedicineGiven>
)

data class MedicineGiven(
    val name: String,
    val quantity: Int
)