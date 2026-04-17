package com.example.petikcare.response_obat

import com.google.gson.annotations.SerializedName

data class EditObat(
    @SerializedName("nama_obat")
    val namaObat: String,
    @SerializedName("sediaan")
    val sediaan: String
)
