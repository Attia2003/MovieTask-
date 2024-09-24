package com.example.taskmovie.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Dtails")
data class DetailsEntity(




    @PrimaryKey val id: Int?,
    val title: String?,
    val overview: String?,
    val posterPath: String?,
    val voteAverage: Double?,
    val popularity: Float?,
    val timestamp: Long
)