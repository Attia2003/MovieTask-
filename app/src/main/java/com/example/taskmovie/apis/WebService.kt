package com.example.taskmovie.apis

import com.example.taskmovie.apis.apiresponse.Response
import com.example.taskmovie.apis.apiresponse.ResponseDetails
import com.example.taskmovie.apis.apiresponse.ResponseSearch
import com.example.taskmovie.apis.apiresponse.ResponsetTopRated
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WebService {

    //https://api.themoviedb.org/3/movie/popular?apiKey=9fdf52bbb519e79fc39b86f417677541
    //https://api.themoviedb.org/3/movie/top_rated?api_key=9fdf52bbb519e79fc39b86f417677541

   @GET("movie/popular")
    fun getapi(@Query("api_key")api_key:String):Call<Response>



    @GET("movie/top_rated")
    fun gettopmovie(@Query("api_key")api_key:String):Call<ResponsetTopRated>

    @GET("movie/{movie_id}")
    fun getdetails(@Path("movie_id") movieId: Int, @Query("api_key")api_key:String):Call<ResponseDetails>


    @GET("discover/movie")
    fun getsearch(@Query("api_key") api_key: String, @Query("query") query: String,
    )


    : Call<ResponseSearch>


}