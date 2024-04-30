package com.example.skullheadradio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.skullheadradio.AppRepository
import com.example.skullheadradio.datamodels.Genre
import com.example.skullheadradio.datamodels.Song
import com.example.skullheadradio.datamodels.Station
import com.example.skullheadradio.remote.LautFmApiService
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var repo: AppRepository = AppRepository(LautFmApiService.LautFmApi)

    val stations: LiveData<List<Station>>
        get() = repo.stations

    val currentSong: LiveData<Song?>
        get() = repo.currentSong

    val currentStation: LiveData<Station?>
        get() = repo.currentStation

    val genres: List<Genre>
        get() = repo.genres

    private var _currentGenres = MutableLiveData<List<Genre>>()
    val currentGenres: LiveData<List<Genre>>
        get() = _currentGenres

    private var _currentStationList = MutableLiveData<List<Station>>()
    val currentStations: LiveData<List<Station>>
        get() = _currentStationList

    init {
        viewModelScope.launch {
            repo.getAllGenres()
        }
    }

    fun getStationsByGenre(genre: String) {
        viewModelScope.launch {
            repo.getStationsByGenre(genre)
        }
    }

    fun toggleGenre(genre: Genre) {
        if (_currentGenres.value?.contains(genre) == true) {
            _currentGenres.value = _currentGenres.value?.filter { it != genre }
            _currentStationList.value?.forEach {station: Station ->
                station.genres.forEach{genrename: String ->
                    if (!_currentGenres.value!!.filter { genre: Genre ->
                        genre.name == genrename
                        }.isEmpty()){
                        _currentStationList.value = _currentStationList.value?.filter {
                            it != station
                        }
                    }
                }
            }
        } else {
            viewModelScope.launch {

                repo.getStationsByGenre(genre.name)

                val currentStationsValue = _currentStationList.value ?: emptyList<Station>()
                val newStations: List<Station> =
                    currentStationsValue + stations.value!!.filter { station ->
                        station !in currentStationsValue
                    }
                _currentStationList.value = newStations

            }
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