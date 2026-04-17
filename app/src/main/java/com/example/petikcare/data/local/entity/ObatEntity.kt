package com.example.petikcare.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "obat")
class ObatEntity(
    @PrimaryKey
    @field:ColumnInfo(name = "id")
    val id: String,

    @field:ColumnInfo(name = "name")
    val name: String,

    @field:ColumnInfo(name = "description")
    val description: String,

    @field:ColumnInfo(name = "stock")
    val stock: Int,

    @field:ColumnInfo(name = "created_at")
    val createdAt: String,

    @field:ColumnInfo(name = "updated_at")
    val updatedAt: String,

    @field:ColumnInfo(name= "preparation")
    val preparation: String? = null

)