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
import com.example.taskmovie.apis.WebService
import com.example.taskmovie.apis.apiresponse.Response
import com.example.taskmovie.apis.apiresponse.ResultsItem
import com.example.taskmovie.apis.showmessage
import com.example.taskmovie.database.MovieDataBase
import com.example.taskmovie.database.PopularEntity
import com.example.taskmovie.databinding.PoupfragBinding
import com.example.taskmovie.recyler.PopRecAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import java.util.concurrent.TimeUnit

class FragmentPoup:Fragment() {
    lateinit var binding: PoupfragBinding
    private lateinit var adapter: PopRecAdapter
    private lateinit var apiService: WebService
    private  lateinit var database : MovieDataBase
    private val cashexpiretime  = TimeUnit.HOURS.toMillis(4)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = PoupfragBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        database = Room.databaseBuilder(
            requireContext(),
            MovieDataBase::class.java,
            "movie_database"
        ).addMigrations(MIGRATION_4_5)
            .build()


        adapter = PopRecAdapter { movieId ->
            val intent = Intent(requireContext(), DetailsActivity::class.java)
            intent.putExtra("ITEM_ID", movieId)
            startActivity(intent)
        }


        binding.recycler.adapter = adapter
        datacashpopmovie()
        Log.d("TAG", "showmovie: ")



    }

    private fun datacashpopmovie() {
        lifecycleScope.launch(Dispatchers.IO) {
            val currenttime = System.currentTimeMillis()
            val cachedmovies =
                database.moviedao().getCashedPopularMovies(currenttime - cashexpiretime)
                if (cachedmovies.isNotEmpty()) {
                    val apimpvie = cachedmovies.map {entit ->
                        ResultsItem(
                            id = entit.id,
                            title = entit.title,
                            overview = entit.overview,
                            posterPath = entit.posterPath

                        )
                        }
                    withContext(Dispatchers.Main){
                        adapter.bindmovie(apimpvie)

                    }

                }else{
                    database.moviedao().deleteExpiredPopularMovies(currenttime - cashexpiretime)

                    showpopumovie()

                }
        }




    }



    private fun showpopumovie() {
        binding.progressBar.isVisible = true
      ApiManager.getWebService().getapi("9fdf52bbb519e79fc39b86f417677541")
          .enqueue(object : Callback<Response> {
              override fun onResponse(
                  call: Call<Response>,
                  response: retrofit2.Response<Response>
              ) {
                  binding.progressBar.isVisible = false
                  if (response.isSuccessful){
                      val movie = response.body()
                      movie?.results?.let{ results->
                          val popularEntitys = results.map { apiMovie ->
                              PopularEntity(
                                  id = apiMovie?.id,
                                  title = apiMovie?.title,
                                  overview = apiMovie?.overview,
                                  posterPath = apiMovie?.posterPath,
                                  voteAverage = apiMovie?.voteAverage,
                                  popularity = apiMovie?.popularity,
                                  timestamp = System.currentTimeMillis()
                              )

                              }
                          lifecycleScope.launch(Dispatchers.IO){
                              database.moviedao().InsertPopular(popularEntitys)


                          }
                     Log.d("movie success","${ adapter.bindmovie(results)}")
                      }?: showError("No movies found")

                  }else{
                      showError("Error find movies")
                  }


              }

              override fun onFailure(call: Call<Response>, t: Throwable) {
                  binding.progressBar.isVisible = false
                  showError(t.localizedMessage?:"Something went wrong")
                  Log.d("movie failure","${t.localizedMessage}")
              }

          })
    }

    private fun showError(message: String) {
        showmessage(
            message = message,
            posActionName = "Try Again",
            posAction = { dialogInterface, _ ->
                dialogInterface.dismiss()
                showpopumovie()
            },
            negActionName = "Cancel",
            negAction = { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        )
   }
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {

            database.execSQL("ALTER TABLE PopularMovies ADD COLUMN another_column_name INTEGER")
        }
    }

}