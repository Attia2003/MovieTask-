package com.example.taskmovie.ui.popular

import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.taskmovie.apis.ApiManager
import com.example.taskmovie.apis.WebService
import com.example.taskmovie.apis.apiresponse.Response
import com.example.taskmovie.apis.apiresponse.ResultsItem
import com.example.taskmovie.database.MovieDataBase
import com.example.taskmovie.database.PopularEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import java.util.concurrent.TimeUnit

class PopularViewModel(private val database: MovieDataBase, private val webServices: WebService): ViewModel() {

    val showprogress = MutableLiveData<Boolean>()
    val MoviePopModel = MutableLiveData<List<ResultsItem>>()
    private val cacheExpireTime = TimeUnit.HOURS.toMillis(4)
    val webServicemanager= ApiManager.getWebService()


    fun datacashpopmovie() {
       viewModelScope.launch(Dispatchers.IO) {
            val currenttime = System.currentTimeMillis()
            val cachedmovies =
                database.moviedao().getCashedPopularMovies(currenttime - cacheExpireTime)
            if (cachedmovies.isNotEmpty()) {
                val apimpvie = cachedmovies.map {entit ->
                    ResultsItem(
                        id = entit.id,
                        title = entit.title,
                        overview = entit.overview,
                        posterPath = entit.posterPath

                    )
                }
                    MoviePopModel.postValue(apimpvie)
                } else{
                database.moviedao().deleteExpiredPopularMovies(currenttime - cacheExpireTime)
                showpopumovie()

            }
        }


    }


    private fun showpopumovie() {
        showprogress.postValue(true)
        webServicemanager .getapi("9fdf52bbb519e79fc39b86f417677541").enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                if (response.isSuccessful) {
                    showprogress.postValue(false)
                    val moviesResponse = response.body()
                    moviesResponse?.results?.let { results ->
                        val popularEntities = results.map { apiMovie ->
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
                        viewModelScope.launch(Dispatchers.IO) {
                            database.moviedao().InsertPopular(popularEntities)
                        }
                        MoviePopModel.postValue(results as List<ResultsItem>?)
                    }
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                showprogress.postValue(false)
                Log.d("movie failure", "${t.localizedMessage}")
            }
        })
    }
}
