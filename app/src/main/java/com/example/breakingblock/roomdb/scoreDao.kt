package com.example.breakingblock.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface scoreDao {
    @Insert
    suspend fun insert(user: user): Long

    @Query("SELECT * FROM user")
    suspend fun selectAll(): MutableList<user>
}

