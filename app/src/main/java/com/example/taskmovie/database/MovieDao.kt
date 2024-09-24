package com.example.taskmovie.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun InserTopRated(movies: List<TopRatedEntity>)

        @Query("Select * from TopRated where timestamp >= :cacheExpiration")
        suspend fun getCachedTopRatedMovies(cacheExpiration: Long): List<TopRatedEntity>

        @Query("DELETE FROM popular WHERE timestamp < :expiredTime")
        suspend fun deleteExpiredTopRatedMovies(expiredTime: Long)


        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun InserDetails(movies:DetailsEntity)

        @Query("Select * from Dtails where timestamp > :cacheExpiration")
        suspend fun getCachedDetailsMovies(cacheExpiration: Long): List<DetailsEntity>

        @Query("DELETE FROM popular WHERE timestamp < :expiredTime")
        suspend fun deleteExpiredDetailsMovies(expiredTime: Long)


        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun InsertPopular(movies: List<PopularEntity>)

        @Query("Select * from Popular where timestamp > :cacheExpiration")
        suspend fun getCashedPopularMovies(cacheExpiration: Long): List<PopularEntity>

        @Query("DELETE FROM popular WHERE timestamp < :expiredTime")
        suspend fun deleteExpiredPopularMovies(expiredTime: Long)
}