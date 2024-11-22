package com.example.taskmovie.ui.toprated

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskmovie.apis.WebService
import com.example.taskmovie.database.MovieDataBase

class TopRatedModelFactory(
    private val database: MovieDataBase,
    private val webServices: WebService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TopRatedViewModel::class.java)) {
            return TopRatedViewModel(database, webServices) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
