package com.example.taskmovie.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskmovie.apis.ApiManager
import com.example.taskmovie.apis.apiresponse.ResponseDetails
import com.example.taskmovie.apis.apiresponse.ResponseMoviesTrailer
import com.example.taskmovie.apis.apiresponse.ResultsItemMovies
import com.example.taskmovie.apis.showmessage
import com.example.taskmovie.database.DetailsEntity
import com.example.taskmovie.database.MovieDataBase
import com.example.taskmovie.databinding.ActivityDetailsBinding
import com.example.taskmovie.recyler.DetRecAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var adapter: DetRecAdapter
    private lateinit var database: MovieDataBase
    private val cashexpiretime = TimeUnit.HOURS.toMillis(4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Room.databaseBuilder(
            this,
            MovieDataBase::class.java,
            "movie_database"

        ).addMigrations(MIGRATION_1_2)
            .build()


        adapter = DetRecAdapter(fetchTrailer = { movieId, callback ->
            showmovietrailer(movieId)
        })

//        adapter = DetRecAdapter()
        binding.recyclerdetails.adapter = adapter

        val movieId = intent.getIntExtra("ITEM_ID", -1)
        Log.d("CHECKID", "Movie ID: $movieId")

        if (movieId != -1) {
            datacashdetails(movieId)
        } else {
            showError("Invalid movie ID")
        }
        showDetails(movieId)
        showmovietrailer(movieId)
    }

    private fun showmovietrailer(movieId: Int){
        Log.d("Trailergo", "Fetching trailer for movie ID: $movieId")
        fetchTrailer(movieId){
            if (it!=null){
                Log.d("Trailerenter?", "Trailer found: ${it.key}")
                binding.webViewTrailer.visibility = View.VISIBLE
                val videoUrl = "https://www.youtube.com/embed/${it.key}"
                binding.webViewTrailer.apply {
                    settings.javaScriptEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    loadUrl(videoUrl)
                }
                }else{
                binding.webViewTrailer.visibility = View.GONE
            }
        }

    }



    private fun fetchTrailer(movieId: Int, callback: (ResultsItemMovies?) -> Unit) {
        Log.d("enterfetchtrailer ", "Fetching trailer for movie ID: $movieId")
        val apiKey = "9fdf52bbb519e79fc39b86f417677541"
        ApiManager.getWebService().gettrailer(movieId, apiKey).enqueue(object : Callback<ResponseMoviesTrailer> {
            override fun onResponse(
                call: Call<ResponseMoviesTrailer>,
                response: Response<ResponseMoviesTrailer>
            ) {
                if (response.isSuccessful) {

                    val trailerList = response.body()?.results
                    val trailer = trailerList?.firstOrNull { it?.site == "YouTube" && it?.official == true }
                    Log.d("TrailerResponsecheck", "Trailer: $trailer")
                        callback(trailer)


                } else {
                    Log.d("TrailerResponse", "Failed to get trailer: ${response.message()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ResponseMoviesTrailer>, t: Throwable) {

                callback(null)
            }
        })

    }


    private fun datacashdetails(movieId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val currentTime = System.currentTimeMillis()
            val cachedDetail = database.moviedao().getCachedDetailsMovies(currentTime - cashexpiretime)

            val cachedMovie = cachedDetail.firstOrNull { it.id == movieId }

            if (cachedMovie != null) {
                val movieDetails = ResponseDetails(
                    id = cachedMovie.id,
                    title = cachedMovie.title,
                    overview = cachedMovie.overview,
                    posterPath = cachedMovie.posterPath,
                    voteAverage = cachedMovie.voteAverage,
                    popularity = cachedMovie.popularity
                )

                withContext(Dispatchers.Main) {
                    adapter.bindDetails(movieDetails)
                }
            } else {
                database.moviedao().deleteExpiredDetailsMovies(currentTime - cashexpiretime)

                showDetails(movieId)
            }
        }
    }

    private fun showDetails(movieId: Int) {
        if (movieId == -1) {
            showError("Invalid movie ID")
            return
        }

        val apiKey = "9fdf52bbb519e79fc39b86f417677541"

        ApiManager.getWebService().getdetails(movieId, apiKey)
            .enqueue(object : Callback<ResponseDetails> {
                override fun onResponse(
                    call: Call<ResponseDetails>,
                    response: Response<ResponseDetails>
                ) {
                    binding.progressBar.isVisible = false
                    if (response.isSuccessful) {
                        val movieDetails = response.body()

                        movieDetails?.let {
                            // Cache the details in the database
                            lifecycleScope.launch(Dispatchers.IO) {
                                val entity = DetailsEntity(
                                    id = it.id,
                                    title = it.title,
                                    overview = it.overview,
                                    posterPath = it.posterPath,
                                    voteAverage = it.voteAverage,
                                    popularity = it.popularity,
                                    timestamp = System.currentTimeMillis()
                                )
                                database.moviedao().InserDetails(entity)

                                withContext(Dispatchers.Main) {
                                    adapter.bindDetails(it)
                                }
                            }
                        }
                    } else {
                        showError("Error fetching details: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseDetails>, t: Throwable) {
                    binding.progressBar.isVisible = false
                    Log.d("DetailsActivity", "onFailure: ${t.localizedMessage}")
                    showError("Failure: ${t.localizedMessage}")
                }
            })
    }

    private fun showError(message: String) {
        showmessage(
            message = message,
            posActionName = "Try Again",
            posAction = { dialogInterface, _ ->
                dialogInterface.dismiss()
                val movieId = intent.getIntExtra("ITEM_ID", -1)
                if (movieId != -1) {
                    showDetails(movieId)
                } else {
                    showError("Invalid movie ID")
                }
            },
            negActionName = "Cancel",
            negAction = { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        )
    }
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create the table if it doesn't exist
            database.execSQL("""
            CREATE TABLE IF NOT EXISTS DetailsEntity (
                id INTEGER PRIMARY KEY NOT NULL,
                title TEXT NOT NULL,
                overview TEXT NOT NULL,
                posterPath TEXT,
                voteAverage REAL NOT NULL,
                popularity REAL NOT NULL,
                timestamp INTEGER NOT NULL
            )
        """)


        }
    }



}
