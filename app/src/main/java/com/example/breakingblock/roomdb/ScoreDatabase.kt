package com.example.breakingblock.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class ScoreDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao
    companion object {
        private var instance: ScoreDatabase? = null

        @Synchronized
        fun getInstance(context: Context): ScoreDatabase? {
            if (instance == null) {
                synchronized(ScoreDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ScoreDatabase::class.java,
                        "user-database"
                    ).build()
                }
            }
            return instance
        }
    }
}