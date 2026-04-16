package com.example.petikcare.data.local.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

@Entity(tableName = "complaints")
class ComplaintEntity (
    @PrimaryKey
    @field:ColumnInfo(name = "id")
    val id: String,

    @field:ColumnInfo(name = "nama_santri")
    val namaSantri: String,

    @field:ColumnInfo(name = "judul_keluhan")
    val title: String,

    @field:ColumnInfo(name = "deskripsi")
    val description: String,

    @field:ColumnInfo(name = "status")
    val status: String,

    @field:ColumnInfo(name = "tanggal_buat")
    val createdAt: String,

    @field:ColumnInfo(name = "Proses_Catatan")
    val handledNote: String?,

    @field:ColumnInfo(name = "tanggal_diproses")
    val handledAt: String?,

    @field:ColumnInfo(name = "nama_obat")
    val medicineName: String? = null,

    @field:ColumnInfo(name = "jumlah_obat")
    val medicineQuantity: String? = null
)