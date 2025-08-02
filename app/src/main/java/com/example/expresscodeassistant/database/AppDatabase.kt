package com.example.expresscodeassistant.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.expresscodeassistant.dao.ExpressDao
import com.example.expresscodeassistant.model.ExpressInfo
import com.example.expresscodeassistant.model.RegexRule

@Database(entities = [ExpressInfo::class, RegexRule::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expressDao(): ExpressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "express_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}