package com.example.taskmovie.ui.popular

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskmovie.apis.ApiManager
import com.example.taskmovie.apis.WebService
import com.example.taskmovie.apis.showmessage
import com.example.taskmovie.database.MovieDataBase
import com.example.taskmovie.databinding.PoupfragBinding
import com.example.taskmovie.recyler.PopRecAdapter
import com.example.taskmovie.ui.DetailsActivity
import java.util.concurrent.TimeUnit

class FragmentPoup : Fragment() {
    private lateinit var binding: PoupfragBinding
    private lateinit var adapter: PopRecAdapter
    private lateinit var apiService: WebService
    private lateinit var database: MovieDataBase
    private lateinit var viewmodelpop: PopularViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        database = Room.databaseBuilder(
            requireContext(),
            MovieDataBase::class.java,
            "movie_database"
        ).addMigrations(MIGRATION_4_5)
            .build()

        apiService = ApiManager.getWebService()


        viewmodelpop = ViewModelProvider(
            this,
            PoupViewModelFactory(database, apiService)
        ).get(PopularViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PoupfragBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PopRecAdapter { movieId ->
            val intent = Intent(requireContext(), DetailsActivity::class.java)
            intent.putExtra("ITEM_ID", movieId)
            startActivity(intent)
        }

        binding.recycler.adapter = adapter
        viewmodelpop.datacashpopmovie()
        Log.d("TAG", "showmovie: ")


        initobserver()
    }

    private fun initobserver() {
        viewmodelpop.showprogress.observe(viewLifecycleOwner) { value ->
            binding.progressBar.isVisible = value
        }

        viewmodelpop.MoviePopModel.observe(viewLifecycleOwner) { movies ->
            adapter.bindmovie(movies)
        }
    }

    private fun showError(message: String) {
        showmessage(
            message = message,
            posActionName = "Try Again",
            posAction = { dialogInterface, _ ->
                dialogInterface.dismiss()
                viewmodelpop.datacashpopmovie()
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
