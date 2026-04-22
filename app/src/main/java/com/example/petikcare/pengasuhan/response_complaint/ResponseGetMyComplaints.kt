package com.example.petikcare.pengasuhan.response_complaint

data class ResponseGetMyComplaints(
    val success: Boolean,
    val message: String,
    val data: List<DataComplaints>
)
