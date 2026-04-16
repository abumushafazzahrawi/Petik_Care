package com.example.petikcare.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.petikcare.data.local.entity.ComplaintEntity
import com.example.petikcare.data.local.entity.ObatEntity

@Dao
interface ObatDao {
    @Query("SELECT * FROM obat")
    suspend fun getAllObat(): List<ObatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObat(obat: List<ObatEntity>)
}