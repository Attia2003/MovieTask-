package com.example.taskmovie.recyler

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taskmovie.apis.apiresponse.ResponseDetails
import com.example.taskmovie.apis.apiresponse.ResultsItemMovies
import com.example.taskmovie.databinding.ItemDetailsBinding

class DetRecAdapter(
    private var movedetails: ResponseDetails? = null,
    private val fetchTrailer: (Int, (ResultsItemMovies?) -> Unit) -> Unit
) : RecyclerView.Adapter<DetRecAdapter.DetailsViewHolder>() {

    class DetailsViewHolder(val binding: ItemDetailsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
        val binding = ItemDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailsViewHolder(binding)
    }

    override fun getItemCount(): Int = if (movedetails != null) 1 else 0

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        movedetails?.let { details ->
            holder.binding.titleDetails.text = details.title
            holder.binding.overviewDetails.text = details.overview
            holder.binding.rated.text = details.popularity.toString()

            Glide.with(holder.binding.imageHeadDetails)
                .load("https://image.tmdb.org/t/p/w500/${details.posterPath}")
                .into(holder.binding.imageHeadDetails)


        }
    }

    fun bindDetails(moves: ResponseDetails) {
        movedetails = moves
        notifyDataSetChanged()
    }
}