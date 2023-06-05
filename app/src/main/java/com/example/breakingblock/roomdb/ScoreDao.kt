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
    @Query("DELETE FROM User WHERE id IN (SELECT id FROM User ORDER BY score ASC LIMIT 1)")
    fun deleteLowestScore()



    @Query("SELECT * FROM User ORDER BY score DESC LIMIT 1")
    fun getHighestScore(): User?

    @Query("SELECT * FROM User ORDER BY score DESC LIMIT 1 OFFSET 15")
    fun getLowestScore(): User?

    @Query("SELECT COUNT(*) FROM User")
    fun getCount(): Int
}

