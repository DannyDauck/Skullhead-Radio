package com.example.skullheadradio.viewmodel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.skullheadradio.AppRepository
import com.example.skullheadradio.datamodels.Genre
import com.example.skullheadradio.datamodels.Song
import com.example.skullheadradio.datamodels.Station
import com.example.skullheadradio.remote.LautFmApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var repo: AppRepository = AppRepository(LautFmApiService.LautFmApi)
    private var player = MediaPlayer()

    var volume: Float = 0.8F

    val stations: LiveData<List<Station>>
        get() = repo.stations

    val currentSong: LiveData<Song?>
        get() = repo.currentSong

    private var _currentStation = MutableLiveData<Station?>()
    val currentStation: LiveData<Station?>
        get() = _currentStation

    val genres: List<Genre>
        get() = repo.genres
    val isPlayingRadio: Boolean
        get() = player.isPlaying

    private var _currentGenres = MutableLiveData<List<Genre>>()
    val currentGenres: LiveData<List<Genre>>
        get() = _currentGenres

    private var _currentStationList = MutableLiveData<List<Station>>()
    val currentStations: LiveData<List<Station>>
        get() = _currentStationList


    fun getStationsByGenre(genre: String) {
        viewModelScope.launch {
            repo.getStationsByGenre(genre)
        }
    }

    fun toggleGenre(genre: Genre) {
        if (_currentGenres.value?.contains(genre) == true) {
            _currentGenres.value = _currentGenres.value?.filter { it != genre }
            _currentStationList.value?.forEach { station: Station ->
                station.genres.forEach { genrename: String ->
                    if (!_currentGenres.value!!.filter { genre: Genre ->
                            genre.name == genrename
                        }.isEmpty()) {
                        _currentStationList.value = _currentStationList.value?.filter {
                            it != station
                        }
                    }
                }
            }
        } else {
            if (_currentGenres.value != null) {
                _currentGenres.value = _currentGenres.value!!.plus(genre)
            } else {
                listOf(genre).also { this._currentGenres.value = it }
            }
            viewModelScope.launch {
                repo.getStationsByGenre(genre.name)
                delay(1500)

                val currentStationsValue = _currentStationList.value ?: emptyList<Station>()
                val newStations: List<Station> =
                    currentStationsValue + stations.value!!.filter { station ->
                        station !in currentStationsValue
                    }
                _currentStationList.value = newStations
                println("vm: stations found: " + _currentStationList.value?.size.toString())
                _currentStationList.value = listOf<Station>() + newStations

            }
        }
    }

    fun getAllGenres() {
        viewModelScope.launch {
            repo.getAllGenres()
        }
    }

    fun setStation(station: Station){
        if(_currentStation.value!=null){
            repo.lastStation = _currentStation.value
        }
        _currentStation.value = station
    }

    fun play(){
        if (player.isPlaying) {
            pause()
        }
        val volumeAnimator = ValueAnimator.ofFloat(volume, 0.0f)
        volumeAnimator.duration = 1000
        volumeAnimator.addUpdateListener { animator ->
            val newVolume = animator.animatedValue as Float
            player.setVolume(newVolume, newVolume)
        }
        try{
            player.reset()
            player.setVolume(0f, 0f)
            player.setDataSource("https://stream.laut.fm/" + currentStation.value!!.name.lowercase())
            player.prepare()
            player.start()
            volumeAnimator.start()
        }catch (e: Exception){
            println(e.stackTrace)
        }

    }

    fun pause() {
        player?.let { currentPlayer ->
            if (currentPlayer.isPlaying) {
                val volumeAnimator = ValueAnimator.ofFloat(volume, 0.0f)
                volumeAnimator.duration = 1000
                volumeAnimator.addUpdateListener { animator ->
                    val newVolume = animator.animatedValue as Float
                    currentPlayer.setVolume(newVolume, newVolume)
                }
                volumeAnimator.start()
                volumeAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        currentPlayer.pause()
                    }
                })
            }
        }
    }


}