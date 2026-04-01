package com.example.petikcare.response_complaint

import com.google.gson.annotations.SerializedName

data class RespondRequest(
    val note: String,
    @SerializedName("medicines_given")
    val medicinesGiven: List<MedicineGiven>
)

