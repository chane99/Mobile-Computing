package com.example.breakingblock.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [user::class], version = 1)
abstract class ScoreDatabase : RoomDatabase() {
    abstract fun scoreDao(): scoreDao

    companion object {
        private var instance: ScoreDatabase? = null

        @Synchronized
        fun getInstance(context: Context): ScoreDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScoreDatabase::class.java,
                    "user-database"
                ).build()
            }
            return instance as ScoreDatabase
        }
    }
}