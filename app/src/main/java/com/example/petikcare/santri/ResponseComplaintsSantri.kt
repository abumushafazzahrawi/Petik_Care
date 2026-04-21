package com.example.petikcare.santri

import com.google.gson.annotations.SerializedName

data class ResponseComplaintsSantri(
    val success: Boolean,
    val message: String,
    val data: DataComplaintSantri
)

data class DataComplaintSantri(
    val status: String,
    val id : String,
    @SerializedName("santri_id")
    val santriId: String,
    val title: String,
    val description: String,
    @SerializedName("updateAt")
    val updateAt: String,
    @SerializedName("createdAt")
    val createdAt: String
)
