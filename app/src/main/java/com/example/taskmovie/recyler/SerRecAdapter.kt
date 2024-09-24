package com.example.taskmovie.recyler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taskmovie.apis.apiresponse.ResultsItemresultser
import com.example.taskmovie.databinding.ItemSearchBinding

class SerRecAdapter (var movieserach : List<ResultsItemresultser> = emptyList()):RecyclerView.Adapter<SerRecAdapter.serviewholder>(){


    class serviewholder(val binding: ItemSearchBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): serviewholder {
       val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return serviewholder(binding)
    }

    override fun getItemCount(): Int{

        return movieserach.size
    }

    override fun onBindViewHolder(holder: serviewholder, position: Int) {
        val movie = movieserach!![position]
        holder.binding.movieTitle.text = movie.title
        holder.binding.moviePopularity.text = "Popularity: ${movie.popularity}"

        Glide.with(holder.binding.moviePoster)
            .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .into(holder.binding.moviePoster)
    }
    fun updateMovies(newMovies: List<ResultsItemresultser>) {
        movieserach = newMovies
        notifyDataSetChanged()
    }


    }
