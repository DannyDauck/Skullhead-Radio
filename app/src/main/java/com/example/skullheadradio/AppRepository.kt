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
    var lastStation: Station? = null

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
            val response = lautFmApi.retrofitService.getStationsByGenre("rock", null, 40)
            if (response.isSuccessful) {
                try {
                    _stations.value = response.body()
                }catch (e: Exception){
                    println(e)
                    println(response.body())
                }

            } else {
                // Hier kannst du den Fehler behandeln, z.B. basierend auf dem Statuscode
                val errorCode = response.code()
                val errorMessage = response.message()
                println("API response: " + errorCode.toString() + ": " + errorMessage)
            }
        }catch (e: Exception){
            println(e)
        }
    }

    suspend fun getStationImage(stationName: String): String {
        try {
            return lautFmApi.retrofitService.getImage(stationName.lowercase(), type = "station_80x80")
        }catch (e: Exception){
            println(e)
            return ""
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