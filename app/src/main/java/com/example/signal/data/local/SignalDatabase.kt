package com.example.signal.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MemoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SignalDatabase : RoomDatabase() {

    abstract fun memoryDao(): MemoryDao

    companion object {

        @Volatile
        private var INSTANCE: SignalDatabase? = null

        fun getDatabase(context: Context): SignalDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SignalDatabase::class.java,
                    "signal_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}