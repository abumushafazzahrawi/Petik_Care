package com.example.petikcare.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.petikcare.data.local.entity.ComplaintEntity
import com.example.petikcare.data.local.entity.MedicineEntity

@Database(entities = [ComplaintEntity::class, MedicineEntity::class], version = 2, exportSchema = false)
abstract class ComplaintDatabase : RoomDatabase() {
    abstract fun complaintDao(): ComplaintDao
    companion object {
        @Volatile
        private var instance: ComplaintDatabase? = null
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS medicines (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                complaintId TEXT NOT NULL,
                name TEXT NOT NULL,
                quantity INTEGER NOT NULL
                )
            """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): ComplaintDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ComplaintDatabase::class.java,
                    "complaint.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }
    }
}