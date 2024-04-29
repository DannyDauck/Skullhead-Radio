package com.example.skullheadradio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.skullheadradio.datamodels.Genre
import com.example.skullheadradio.datamodels.Song
import com.example.skullheadradio.datamodels.Station
import com.example.skullheadradio.remote.LautFmApiService

class AppRepository(

    val lautFmApi: LautFmApiService.LautFmApi,
    var genres: List<Genre> = listOf()

) {

    private var _currentSong = MutableLiveData<Song?>()
    val currentSong: LiveData<Song?>
        get() = _currentSong

    private var _currentStation = MutableLiveData<Station?>()
    val currentStation: LiveData<Station?>
        get() = _currentStation

    private var _stations = MutableLiveData<List<Station>>()
    val stations: LiveData<List<Station>>
        get() = _stations

    suspend fun getStationsByGenre(genre: String){
        try {
            _stations.value =  lautFmApi.retrofitService.getStationsByGenre(genre)
        }catch (e: Exception){
            println(e.stackTrace)
            _stations.value = listOf()
        }
    }

    suspend fun setStation(station: String){
        try{
            _currentStation.value = lautFmApi.retrofitService.getStation(station)
        }catch (e: Exception){
            println("Could not set station")
            println(e.stackTrace)
        }
    }

    suspend fun getAllGenres(){
        try {
            genres = lautFmApi.retrofitService.getAllGenres()
        }catch(e: Exception){
            println("Could not fetch genres")
        }
    }

}