package com.example.taskmovie

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmovie.apis.ApiManager
import com.example.taskmovie.apis.apiresponse.ResponseSearch
import com.example.taskmovie.apis.apiresponse.ResultsItemresultser
import com.example.taskmovie.apis.showmessage
import com.example.taskmovie.databinding.ActivitySearchBinding
import com.example.taskmovie.recyler.SerRecAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivityview : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: SerRecAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        adapter = SerRecAdapter()
        binding.recyclerView.adapter = adapter

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val keyword = s.toString()
                if (keyword.isNotEmpty()) {
                    searchMovies(keyword)
                } else {

                    adapter.updateMovies(emptyList())
                }
            }
        })
    }

    private fun searchMovies(keyword: String) {
        ApiManager.getWebService().getsearch("9fdf52bbb519e79fc39b86f417677541",keyword )
            .enqueue(object : Callback<ResponseSearch> {
                override fun onResponse(
                    call: Call<ResponseSearch>,
                    response: Response<ResponseSearch>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val movieList = response.body()?.results ?: emptyList()


                        val filteredMovies = movieList.filter { movie ->
                            movie?.title?.contains(keyword, ignoreCase = true) == true
                        }

                        adapter.updateMovies(filteredMovies as List<ResultsItemresultser>)
                    } else {
                        val errorCode = response.code()
                        val errorBody = response.errorBody()?.string()
                        showError("Error: ${response.message()} (Code: $errorCode, Body: $errorBody)", keyword)
                    }
                }

                override fun onFailure(call: Call<ResponseSearch>, t: Throwable) {
                    showError("Failed to fetch results.", keyword)
                }
            })
    }

    private fun showError(message: String, keyword: String) {
        showmessage(
            message = message,
            posActionName = "Try Again",
            posAction = { dialogInterface, _ ->
                dialogInterface.dismiss()
                searchMovies(keyword)
            },
            negActionName = "Cancel",
            negAction = { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        )
    }
}
