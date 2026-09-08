package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        LeadEntity::class,
        ActivityLogEntity::class,
        PropertyEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun leadDao(): LeadDao
    abstract fun activityLogDao(): ActivityLogDao
    abstract fun propertyDao(): PropertyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "housing_worlds_crm.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
