package com.example.skullheadradio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.skullheadradio.AppRepository
import com.example.skullheadradio.datamodels.Genre
import com.example.skullheadradio.datamodels.Song
import com.example.skullheadradio.datamodels.Station
import com.example.skullheadradio.remote.LautFmApiService
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {

    private var repo: AppRepository = AppRepository(LautFmApiService.LautFmApi)

    val stations: LiveData<List<Station>>
        get() = repo.stations

    val currentSong: LiveData<Song?>
        get() = repo.currentSong

    val currentStation: LiveData<Station?>
            get() = repo.currentStation

    val genres: List<Genre>
        get() = repo.genres

    init {
        viewModelScope.launch {
            repo.getAllGenres()
        }
    }

    fun getStationsByGenre(genre: String){
        viewModelScope.launch {
            repo.getStationsByGenre(genre)
        }
    }


    companion object {
        @Volatile
        private var instance: MainViewModel? = null

        fun getInstance(application: Application): MainViewModel {
            return instance ?: synchronized(this) {
                instance ?: MainViewModel(application).also { instance = it }
            }
        }
    }
}