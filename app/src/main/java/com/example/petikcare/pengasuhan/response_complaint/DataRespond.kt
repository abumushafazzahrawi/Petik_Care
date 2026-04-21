package com.example.petikcare.pengasuhan.response_complaint

import com.google.gson.annotations.SerializedName

data class DataRespond(
    @SerializedName("complaint_id")
    val complaintId: String,
    val status: String,
    @SerializedName("handled_at")
    val handledAt: String,
    val treatment: RespondRequest
)
