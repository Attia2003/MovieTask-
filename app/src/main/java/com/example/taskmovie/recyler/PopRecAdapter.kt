package com.example.taskmovie.recyler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taskmovie.apis.apiresponse.ResultsItem
import com.example.taskmovie.databinding.ItemPopMovBinding

class PopRecAdapter ( private val onItemClicked: (Int) -> Unit):RecyclerView.Adapter<PopRecAdapter.popviewholder>(){
    var movie:List<ResultsItem>?=null
    class popviewholder( val binding: ItemPopMovBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): popviewholder {
       val binding = ItemPopMovBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return popviewholder(binding)
    }

    override fun getItemCount(): Int= movie?.size?:0

    override fun onBindViewHolder(holder: popviewholder, position: Int) {
        val movies = movie!![position]
        holder.binding.title.text = movies.title
        holder.binding.decription.text = movies.overview
        Glide.with(holder.binding.headimage)
            .load("https://image.tmdb.org/t/p/w500${movies.posterPath}")
            .into(holder.binding.headimage)



        holder.binding.title.setOnClickListener{
            onItemClicked(movies.id?:-1)
        }
        holder.binding.headimage.setOnClickListener{
            onItemClicked(movies.id?:-1)
        }



    }

    fun bindmovie(newresults: List<ResultsItem?>) {
         movie= newresults as List<ResultsItem>?
        notifyDataSetChanged()
    }



}