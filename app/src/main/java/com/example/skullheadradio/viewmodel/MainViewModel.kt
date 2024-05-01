package com.example.skullheadradio.viewmodel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Application
import android.media.MediaPlayer
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.skullheadradio.AppRepository
import com.example.skullheadradio.R
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
    var genreFilterString = ""
    var allGenres = listOf<Genre>()

    val stations: LiveData<List<Station>>
        get() = repo.stations

    val currentSong: LiveData<Song?>
        get() = repo.currentSong

    private var _currentStation = MutableLiveData<Station?>()
    val currentStation: LiveData<Station?>
        get() = _currentStation

    val genres: List<Genre>
        get() = repo.genres

    private var _currentGenres = MutableLiveData<List<Genre>>(listOf())
    val currentGenres: LiveData<List<Genre>>
        get() = _currentGenres

    private var _currentStationList = MutableLiveData<List<Station>>()
    val currentStations: LiveData<List<Station>>
        get() = _currentStationList


    fun isPlayingRadio(): Boolean{
        return  player.isPlaying
    }

    fun getAllSelectedGenreNames(): List<String>{
        var newlist = mutableListOf<String>()
        _currentGenres.value?.forEach {
            newlist = newlist.plus(it.name) as MutableList<String>
        }
        return newlist
    }

    fun toggleGenre(genre: Genre) {
        var remove = false
        if (_currentGenres.value?.contains(genre) == true) {
            _currentGenres.value = _currentGenres.value?.filter { it != genre }

            println(genre.name)
            _currentStationList?.value = _currentStationList.value?.filter { station ->
                //removes all where station genres contains genre name and which has non of the other genres
                !station.genres.contains(genre.name) && station.genres.filter { genreName ->
                    !getAllSelectedGenreNames().contains(genreName)
                } != station.genres
            }
            remove = true
        } else {
            if (_currentGenres.value != null) {
                _currentGenres.value = _currentGenres.value!!.plus(genre)
            } else {
                listOf(genre).also { this._currentGenres.value = it }
            }
        }
        if(_currentGenres.value!!.isNotEmpty() && !remove){
            viewModelScope.launch {
                repo.getStationsByGenre(genre.name)
                delay(1500)
                val oldStations = _currentStationList.value ?: listOf()

                val newStations: List<Station> = stations.value!! + oldStations!!.filter{station ->
                    station !in stations.value!!
                }
                _currentStationList.value = newStations
                println("vm: stations found: " + _currentStationList.value?.size.toString())
                _currentStationList.value = listOf<Station>() + newStations

            }
        }else if(_currentGenres.value!!.isEmpty() && !remove){
            _currentStationList.value = listOf()
        }
    }

    fun getAllGenres() {
        viewModelScope.launch {
            repo.getAllGenres()
            delay(1000)
            allGenres = repo.genres
        }
    }

    fun setStation(station: Station){
        if(_currentStation.value!=null){
            repo.lastStation = _currentStation.value
        }
        _currentStation.value = station
    }

    fun play(){
        val volumeAnimator = ValueAnimator.ofFloat(0.0f, volume)
        volumeAnimator.duration = 1000
        volumeAnimator.addUpdateListener { animator ->
            val newVolume = animator.animatedValue as Float
            player.setVolume(newVolume, newVolume)
        }
        try{
            player.reset()
            player.setVolume(0f, 0f)
            player.setDataSource("https://stream.laut.fm/" + currentStation.value!!.name)
            player.prepare()
            player.start()
            volumeAnimator.start()
            println("player is playing: " + player.isPlaying + "volume: " + volume)
        }catch (e: Exception){
            println(e.stackTrace)
        }
    }
    fun changeStation(station: Station){
        println("Change station called")
        val turnDownVolume = ValueAnimator.ofFloat(volume, 0f)
        turnDownVolume.duration = 1000
        turnDownVolume.addUpdateListener {animator ->
            val newVolume = animator.animatedValue as Float
            player.setVolume(newVolume, newVolume)
        }
        turnDownVolume.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator) {
                setStation(station)
                play()
                turnDownVolume.removeAllListeners()
            }
        })
        turnDownVolume.start()

    }

    fun pause(view: ImageView) {
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
                        volumeAnimator.removeAllListeners()
                        view.setImageResource(R.drawable.play)
                    }
                })
            }
        }
    }

    fun restartRadio(){
        val volumeAnimator = ValueAnimator.ofFloat(0.0f, volume)
        volumeAnimator.duration = 1000
        volumeAnimator.addUpdateListener { animator ->
            val newVolume = animator.animatedValue as Float
            player.setVolume(newVolume, newVolume)
        }
        player.start()
        volumeAnimator.start()
    }


}