package com.example.breakingblock.roomdb

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM User ORDER BY score DESC")
    fun selectAll(): MutableList<User>
}

