package com.example.petikcare.pengasuhan.response_obat

data class ObatRequest(
    val nama_obat: String,
    val deskripsi: String,
    val stok: Int,
    val sediaan: String
)
