package com.example.skullheadradio.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.skullheadradio.adapter.GenreAdapter.*
import com.example.skullheadradio.databinding.GenreItemBinding
import com.example.skullheadradio.datamodels.Genre
import com.example.skullheadradio.viewmodel.MainViewModel

class GenreAdapter(
    private var data: List<Genre>,
    private  var vm: MainViewModel,
    private val context: Context
): RecyclerView.Adapter<ListHolder>(){

    inner class ListHolder(val bnd: GenreItemBinding): RecyclerView.ViewHolder(bnd.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        var bnd = GenreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListHolder(bnd)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        holder.bnd.genreItemName.text = data[position].name
        holder.bnd.root.setOnClickListener {

        }
    }
}