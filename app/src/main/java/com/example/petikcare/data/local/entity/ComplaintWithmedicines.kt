package com.example.petikcare.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ComplaintWithmedicines(
    @Embedded val complaint: ComplaintEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "complaintId"
    )
    val medicines: List<MedicineEntity>
)
