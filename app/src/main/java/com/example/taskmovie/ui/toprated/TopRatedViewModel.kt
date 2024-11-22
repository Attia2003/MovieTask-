package com.example.taskmovie.ui.toprated

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmovie.apis.ApiManager
import com.example.taskmovie.apis.WebService
import com.example.taskmovie.apis.apiresponse.ResponsetTopRated
import com.example.taskmovie.apis.apiresponse.ResultsItemres
import com.example.taskmovie.database.MovieDataBase
import com.example.taskmovie.database.TopRatedEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import java.util.concurrent.TimeUnit

class TopRatedViewModel(private val movieDataBase: MovieDataBase, webServices: WebService):ViewModel() {
      private val cashexpiretime = TimeUnit.HOURS.toMillis(4)
      val showprogresspar = MutableLiveData<Boolean>()
      val MovieTopModel = MutableLiveData<List<ResultsItemres>>()


     fun datacashtopmovie() { viewModelScope
        .launch(Dispatchers.IO) {
            val currenttime = System.currentTimeMillis()
            val cachedmovies =
                movieDataBase.moviedao().getCachedTopRatedMovies(currenttime - cashexpiretime)
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
                    MovieTopModel.postValue(apimove)
                }
            } else {
                movieDataBase.moviedao().deleteExpiredTopRatedMovies(currenttime - cashexpiretime)
                showTopMovie()

            }
        }
    }
    private fun showTopMovie() {

        showprogresspar.postValue(true)
        ApiManager.getWebService().gettopmovie("9fdf52bbb519e79fc39b86f417677541")
            .enqueue(object : Callback<ResponsetTopRated> {
                override fun onResponse(
                    call: Call<ResponsetTopRated>,
                    response: retrofit2.Response<ResponsetTopRated>
                ) {
                    showprogresspar.postValue(false)
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
                            viewModelScope.launch(Dispatchers.IO) {
                                movieDataBase.moviedao().InserTopRated(topRatedEntitys)

                            }
                            MovieTopModel.postValue(results as List<ResultsItemres>?)

                        }
                    }
                }

                override fun onFailure(call: Call<ResponsetTopRated>, t: Throwable) {
                    showprogresspar.postValue(false)
                    Log.d("movie failure", "${t.localizedMessage}")
                }
            })
    }



}