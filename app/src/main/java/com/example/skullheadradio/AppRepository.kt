package com.example.skullheadradio

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.skullheadradio.datamodels.Artist
import com.example.skullheadradio.datamodels.Genre
import com.example.skullheadradio.datamodels.Song
import com.example.skullheadradio.datamodels.Station
import com.example.skullheadradio.remote.LautFmApiService

class AppRepository(

    val lautFmApi: LautFmApiService.LautFmApi,
    var genres: List<Genre> = listOf()

) {
    var lastStation: Station? = null
    var currentSong: Song? = null

    private var _currentStation = MutableLiveData<Station?>()
    val currentStation: LiveData<Station?>
        get() = _currentStation

    private var _stations = MutableLiveData<List<Station>>()
    val stations: LiveData<List<Station>>
        get() = _stations

    private var _marqueeTextUpdate = MutableLiveData(" ")
    val  marqueeText: LiveData<String>
        get() = _marqueeTextUpdate

    private var _nextArtist = MutableLiveData("")
    val nextArtist: LiveData<String>
        get() = _nextArtist

    suspend fun getStationsByGenre(genre: String){
        try {
            val response = lautFmApi.retrofitService.getStationsByGenre(genre.lowercase(), null, 40)
            if (response.isSuccessful) {
                try {
                    _stations.value = response.body()
                }catch (e: Exception){
                    println(e)
                    println(response.body())
                }

            } else {
                val errorCode = response.code()
                val errorMessage = response.message()
                println("API response: " + errorCode.toString() + ": " + errorMessage)
            }
        }catch (e: Exception){
            println(">>>AppRepository:")
            println(e)
        }
    }

    suspend fun getAllGenres(){
        try {
            genres = lautFmApi.retrofitService.getAllGenres()
        }catch(e: Exception){
            println("Could not fetch genres")
            println(e)
        }
    }

    suspend fun getCurrentSong(station: String){
        try {
            currentSong = lautFmApi.retrofitService.getCurrentSong(station)
            getNextArtist(station)
            println(currentStation)
        }catch(e: Exception){
            println(e)
            println("something went wrong while getting current song")
        }
    }

    private suspend fun getNextArtist(station: String){
        try{
            var nextArtists = lautFmApi.retrofitService.getNextArtists(station)
            _nextArtist.value =  nextArtists.first().artist.name
        }catch (e: Exception){
            println("could not fetch artists")
            _nextArtist.value = ""
        }
    }
}