package com.example.petikcare.pengasuhan.response_obat

data class ResponseGetObat(
    val success: Boolean,
    val message: String,
    val data: List<DataGetObat>

)

data class DataGetObat(
    val id: String,
    val name: String,
    val description: String,
    val stock: Int,
    val createdAt: String,
    val updatedAt: String
)
