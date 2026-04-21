package com.example.petikcare.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.petikcare.data.local.entity.ComplaintEntity
import com.example.petikcare.data.local.entity.MedicineEntity

@Dao
interface ComplaintDao {
    @Query("SELECT * FROM complaints")
    fun getAllComplaints() : LiveData<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints")
    fun getAllComplaintsList() : List<ComplaintEntity>

    @Query("SELECT * FROM complaints WHERE id = :complaintId")
    fun getComplaintById(complaintId: String): ComplaintEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaints(data: List<ComplaintEntity>)

    @Query("DELETE FROM complaints")
    suspend fun deleteAllComplaints()

    @Insert
    suspend fun insertMedicines(medicineEntity: List<MedicineEntity>)

    @Query("DELETE FROM medicines")
    suspend fun deleteAllMedicines()

    @Query("DELETE from complaints WHERE id = :id")
    suspend fun deleteComplaintById(id: String)


}