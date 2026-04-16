package com.example.petikcare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "medicines")
data class MedicineEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @SerializedName("complaint_id")
    val complaintId: String,
    val name: String,
    val quantity: Int

)