package com.example.petikcare.response_complaint

import com.google.gson.annotations.SerializedName

data class ResponseComplaints(
    val success: Boolean,
    val message: String,
    val data: List<DataComplaints>
)

data class DataComplaints(
    val id: String,
    @SerializedName("santri_id")
    val santriId: String,
    val title: String,
    val description: String,
    val status: String,
    @SerializedName("handled_by")
    val handledBy: String?,
    @SerializedName("handled_note")
    val handledNote: String?,
    @SerializedName("handled_at")
    val handledAt: String?,
    val createdAt: String,
    val updatedAt: String,
    val santri: Santri,
    val treatment: TreatmentDetail?
)

