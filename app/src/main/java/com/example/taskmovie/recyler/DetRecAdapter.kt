package com.example.taskmovie.recyler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taskmovie.apis.apiresponse.ResponseDetails
import com.example.taskmovie.databinding.ItemDetailsBinding

class DetRecAdapter(var movedetails : ResponseDetails?= null) : RecyclerView.Adapter<DetRecAdapter.detailsviewholder>(){

    class detailsviewholder(val binding :ItemDetailsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): detailsviewholder {
         val binding = ItemDetailsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
          return  detailsviewholder(binding)
    }

    override fun getItemCount(): Int = if (movedetails != null) 1 else 0


    override fun onBindViewHolder(holder: detailsviewholder, position: Int) {
        movedetails?.let { details ->
            holder.binding.titleDetails.text = details.title
            holder.binding.overviewDetails.text = details.overview
            holder.binding.rated.text=details.popularity.toString()


            Glide.with(holder.binding.imageHeadDetails)
                .load("https://image.tmdb.org/t/p/w500/${details.posterPath}")
                .into(holder.binding.imageHeadDetails)
        }
    }

    fun binddetails(moves: ResponseDetails) {
        movedetails = moves
        notifyDataSetChanged()

    }


}