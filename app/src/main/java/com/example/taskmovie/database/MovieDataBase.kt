package com.example.taskmovie.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TopRatedEntity::class, PopularEntity::class,DetailsEntity::class,ResgisterEntity::class], version = 5)
 abstract class MovieDataBase: RoomDatabase() {
        abstract fun moviedao() : MovieDao


//    companion object {
//        private var instance: MovieDataBase? = null
//
//
//        val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//
//                database.execSQL("ALTER TABLE PopularMovies ADD COLUMN new_column_name TEXT")
//            }
//        }
//
//        val MIGRATION_2_3 = object : Migration(2, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//
//                database.execSQL("ALTER TABLE DetailsEntity ADD COLUMN new_column_name TEXT")
//            }
//        }
//
//        val MIGRATION_3_4 = object : Migration(3, 4) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//
//                database.execSQL("ALTER TABLE PopularMovies ADD COLUMN another_column_name INTEGER")
//            }
//        }
//
//        fun getInstance(context: Context): MovieDataBase {
//            if (instance == null) {
//                instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    MovieDataBase::class.java,
//                    "movie-database"
//                )
//                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4) // Add all necessary migrations
//                    .build()
//            }
//            return instance!!
//        }
//    }
}