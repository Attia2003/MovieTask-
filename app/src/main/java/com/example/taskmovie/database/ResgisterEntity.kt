package com.example.taskmovie.database

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "Users")
data class ResgisterEntity (

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val password: String
)
