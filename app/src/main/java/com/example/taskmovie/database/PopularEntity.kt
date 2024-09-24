package com.example.taskmovie.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Popular")
data class PopularEntity (
    @PrimaryKey val id: Int?,


    val title: String?,
    val overview: String?,
    val posterPath: String?,
    val voteAverage: Double?,
    val popularity: Double?,
    val timestamp: Long

)




