package com.example.breakingblock.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "user")
data class user(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val score: Int
)
