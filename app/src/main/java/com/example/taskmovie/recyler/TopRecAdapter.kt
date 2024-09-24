package com.example.taskmovie.recyler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taskmovie.apis.apiresponse.ResultsItemres
import com.example.taskmovie.databinding.ItemRatedMovBinding

class TopRecAdapter(
    private val onItemClicked: (Int) -> Unit
) : RecyclerView.Adapter<TopRecAdapter.TopViewHolder>() {

    var ratedMovies: List<ResultsItemres>? = null

    class TopViewHolder(val binding: ItemRatedMovBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopViewHolder {
        val binding = ItemRatedMovBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopViewHolder(binding)
    }

    override fun getItemCount(): Int = ratedMovies?.size ?: 0

    override fun onBindViewHolder(holder: TopViewHolder, position: Int) {
        val movie = ratedMovies!![position]

        // Bind data to views
        holder.binding.titlemov.text = movie.title
        holder.binding.decriptionmov.text = movie.overview

        Glide.with(holder.binding.headimage)
            .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .into(holder.binding.headimage)

        holder.binding.titlemov.setOnClickListener {
            onItemClicked(movie.id ?: -1)
        }

        holder.binding.headimage.setOnClickListener {
            onItemClicked(movie.id ?: -1)
        }
    }

    fun bindTopmovie(newMovieList: List<ResultsItemres?>) {
        ratedMovies = newMovieList as List<ResultsItemres>
        notifyDataSetChanged()
    }
}
