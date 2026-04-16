package com.example.petikcare.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.petikcare.data.local.entity.ObatEntity

@Database(entities = [ObatEntity::class], version = 2, exportSchema = false)
abstract class ObatDatabase : RoomDatabase() {
    abstract fun obatDao(): ObatDao

    companion object {
        @Volatile
        private var instance: ObatDatabase? = null
        fun getInstance(context: Context): ObatDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ObatDatabase::class.java,
                    "obat.db"
                ).build()
            }
    }
}