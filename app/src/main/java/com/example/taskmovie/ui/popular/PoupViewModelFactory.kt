package com.example.taskmovie.ui.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskmovie.apis.WebService
import com.example.taskmovie.database.MovieDataBase

class PoupViewModelFactory(private val database: MovieDataBase,
                           private val webServices: WebService
) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PopularViewModel::class.java)) {
                return PopularViewModel(database,webServices) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
}