package com.example.skullheadradio.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.skullheadradio.R
import com.example.skullheadradio.databinding.StationItemBinding
import com.example.skullheadradio.datamodels.Station
import com.example.skullheadradio.viewmodel.MainViewModel
import coil.load

class StationAdapter(
    private var data: List<Station>,
    private  var vm: MainViewModel,
    private val context: Context
): RecyclerView.Adapter<StationAdapter.ListHolder>(){

    inner class ListHolder(val bnd: StationItemBinding): RecyclerView.ViewHolder(bnd.root)
    private lateinit var bnd: StationItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        bnd = StationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListHolder(bnd)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        var station = data[position]

        holder.bnd.stationItemStationName.text = station.name
        holder.bnd.stationItemStationLocation.text = station.location
        holder.bnd.stationItemStationGenres.text = station.genres.joinToString(", ")

        bnd.stationItemStationLable.load(station.images.station_80x80){
            placeholder(R.drawable.radio)
        }


        if(station == vm.currentStation.value && vm.isPlayingRadio){
            holder.bnd.stationItemPlayBtn.setImageResource(R.drawable.pause)
            holder.bnd.stationBg.setBackgroundResource(R.drawable.active_station_item_bg)
        }else{
            holder.bnd.stationItemPlayBtn.setImageResource(R.drawable.play)
            holder.bnd.stationBg.setBackgroundResource(R.drawable.inactive_station_bg)
        }

        holder.bnd.stationItemPlayBtn.setOnClickListener {
            if (vm.currentStation.value != station && vm.isPlayingRadio) {
                vm.setStation(station)
                vm.play()
            }else{
                vm.pause()
                update()
            }
        }
    }

    fun update(){
        if(vm.currentStations.value != null) {
            println("stations was setted")
            data = vm.currentStations.value!!
            println(vm.currentStations.value!!.first().images.station_80x80)
        }else{
            println("stations was empty")
            data = listOf()
        }
        notifyDataSetChanged()
    }


}