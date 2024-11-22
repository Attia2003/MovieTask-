package com.example.taskmovie.ui.toprated

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.example.taskmovie.ui.DetailsActivity
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
    private lateinit var viewmodeltop: TopRatedViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Room.databaseBuilder(
            requireContext(),
            MovieDataBase::class.java,
            "movie_database"

        ).addMigrations(MIGRATION_1_2)

            .build()

        val apiService = ApiManager.getWebService()

        viewmodeltop = ViewModelProvider(
            this,
            TopRatedModelFactory(database,apiService)
        ).get(TopRatedViewModel::class.java)

    }


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

        adapter = TopRecAdapter { movieId ->

            val intent = Intent(requireContext(), DetailsActivity::class.java)
            intent.putExtra("ITEM_ID", movieId)
            startActivity(intent)
        }
        binding.recycler.adapter = adapter
        viewmodeltop.datacashtopmovie()
        initobserver()

    }

    private fun initobserver() {
        viewmodeltop.showprogresspar.observe(viewLifecycleOwner) { value ->
            binding.prograssBaarr.isVisible = value

        }
        viewmodeltop.MovieTopModel.observe(viewLifecycleOwner) { movies ->
            adapter.bindTopmovie(movies)
        }
    }

    private fun showError(message: String) {
        showmessage(
            message = message,
            posActionName = "Try Again",
            posAction = { dialogInterface, _ ->
                dialogInterface.dismiss()

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
