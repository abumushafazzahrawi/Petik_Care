package com.example.petikcare.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.petikcare.data.local.entity.ComplaintEntity
import com.example.petikcare.data.local.entity.ObatEntity

@Dao
interface ObatDao {
    @Query("SELECT * FROM obat")
    fun getAllObat(): LiveData<List<ObatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObat(obat: List<ObatEntity>)

    @Query("SELECT * FROM obat WHERE id = :id")
    suspend fun getObatByid(id: String): ObatEntity?

    @Query("DELETE FROM obat")
    suspend fun deleteAllObat()
}