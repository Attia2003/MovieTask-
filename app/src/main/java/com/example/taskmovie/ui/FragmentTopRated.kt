package com.example.taskmovie.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskmovie.apis.ApiManager
import com.example.taskmovie.apis.apiresponse.ResponsetTopRated
import com.example.taskmovie.apis.apiresponse.ResultsItemres
import com.example.taskmovie.apis.showmessage
import com.example.taskmovie.database.MovieDataBase
import com.example.taskmovie.database.TopRatedEntity
import com.example.taskmovie.databinding.TopratedfragBinding
import com.example.taskmovie.recyler.TopRecAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import java.util.concurrent.TimeUnit

class FragmentTopRated : Fragment() {
    lateinit var binding: TopratedfragBinding
    private lateinit var adapter: TopRecAdapter
    private  lateinit var database : MovieDataBase
    private val cashexpiretime  = TimeUnit.HOURS.toMillis(4)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TopratedfragBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Room.databaseBuilder(
            requireContext(),
            MovieDataBase::class.java,
            "movie_database"

        ).addMigrations(MIGRATION_1_2)

            .build()




        adapter = TopRecAdapter { movieId ->

            val intent = Intent(requireContext(), DetailsActivity::class.java)
            intent.putExtra("ITEM_ID", movieId)
            startActivity(intent)
        }
        binding.recycler.adapter = adapter

        datacashtopmovie()

    }
    private fun datacashtopmovie() {
        lifecycleScope.launch(Dispatchers.IO) {
            val currenttime = System.currentTimeMillis()
            val cachedmovies =
                database.moviedao().getCachedTopRatedMovies(currenttime - cashexpiretime)
            if (cachedmovies.isNotEmpty()) {
             val apimove = cachedmovies.map { entity ->
                 ResultsItemres(
                      id = entity.id,
                      title = entity.title,
                      overview = entity.overview,
                     posterPath = entity.posterPath
                 )
             }
                withContext(Dispatchers.Main){
                    adapter.bindTopmovie(apimove)
                }
            } else {
                database.moviedao().deleteExpiredTopRatedMovies(currenttime - cashexpiretime)
                showTopMovie()

            }
        }
    }
    private fun showTopMovie() {

        binding.prograssBaarr.isVisible = true
        ApiManager.getWebService().gettopmovie("9fdf52bbb519e79fc39b86f417677541")
            .enqueue(object : Callback<ResponsetTopRated> {
                override fun onResponse(
                    call: Call<ResponsetTopRated>,
                    response: retrofit2.Response<ResponsetTopRated>
                ) {
                    binding.prograssBaarr.isVisible = false
                    if (response.isSuccessful) {
                        val movie = response.body()
                        movie?.results?.let {results->
                            val topRatedEntitys = results.map { apiMovie ->
                                TopRatedEntity(
//                                database.moviedao().getallTopRatedMovies()

                                    id = apiMovie?.id,
                                    title = apiMovie?.title,
                                    overview = apiMovie?.overview,
                                    posterPath = apiMovie?.posterPath,
                                    voteAverage = apiMovie?.voteAverage,
                                    popularity = apiMovie?.popularity,
                                    timestamp = System.currentTimeMillis()
                                )

                            }
                             lifecycleScope.launch(Dispatchers.IO) {
                                 database.moviedao().InserTopRated(topRatedEntitys)


                             }
                            Log.d("movietopsuccess", "${adapter.bindTopmovie(results)}")
                        } ?: showError("No movies found")
                    } else {
                        showError("Error finding movies")
                    }
                }

                override fun onFailure(call: Call<ResponsetTopRated>, t: Throwable) {
                    showError(t.localizedMessage ?: "Something went wrong")
                    Log.d("movie failure", "${t.localizedMessage}")
                }
            })
    }

    private fun showError(message: String) {
        showmessage(
            message = message,
            posActionName = "Try Again",
            posAction = { dialogInterface, _ ->
                dialogInterface.dismiss()
                showTopMovie()
            },
            negActionName = "Cancel",
            negAction = { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        )
    }
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {

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
