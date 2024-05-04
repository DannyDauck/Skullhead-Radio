package com.example.skullheadradio.viewmodel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Application
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
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
import java.time.Duration
import java.util.Date
import kotlin.time.toDuration

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var repo: AppRepository = AppRepository(LautFmApiService.LautFmApi)
    private var player = MediaPlayer()



    var volume: Float = 0.8F
    var genreFilterString = ""
    var allGenres = listOf<Genre>()

    val stations: LiveData<List<Station>>
        get() = repo.stations

    private var _marqueeText = MutableLiveData<String>( "No station selected")
    val marqueeText: LiveData<String>
        get() = _marqueeText

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

    private var _currentSong = MutableLiveData<Song?>()
    val currentSong: LiveData<Song?>
            get() = _currentSong

    val nextArtist: LiveData<String>
        get() = repo.nextArtist

    //Die Integer ist notwendig um zu überprüfen ob im HomeScreen mehrfach hintereinander volumeUp/volumeDown gedrückt wurde,
    //sonst würden sich die Timer kreuzen, die die Visibility wieder auf 0 setzten
    private var _volumeViewIsVisible = MutableLiveData(0)
    val volumeViewIsVisible: LiveData<Int>
        get() = _volumeViewIsVisible



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
        startMarqueeTextUpdate()
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

    fun pause(view: ImageView?) {
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
                        if(view!=null) {
                            view?.setImageResource(R.drawable.play)
                        }
                    }
                })
            }
        }
    }

    fun startMarqueeTextUpdate(){
        if (currentStation.value == null){
            marqueeText.value
        }else{
            viewModelScope.launch {
                repo.getCurrentSong(currentStation.value!!.name)
                delay(1000)
                val song = repo.currentSong
                _currentSong.value = song
                var newMarqueeTextString = getApplication<Application>().applicationContext.getString(R.string.now_playing) + song?.title + " - " + song?.artist?.name + "     ***     "
                if(nextArtist.value!!.isNotEmpty()){
                    newMarqueeTextString = newMarqueeTextString + getApplication<Application>().applicationContext.getString(R.string.next_artist) + nextArtist.value + "    ***    "
                }
                newMarqueeTextString = newMarqueeTextString + currentStation.value!!.display_name + "   " + currentStation.value!!.description + "     ***     "
                _marqueeText.value = newMarqueeTextString
                try {
                    delay(compareTime(song!!.ends_at))
                    startMarqueeTextUpdate()
                }catch (e: Exception) {
                    println(song)
                }
            }
        }
    }

    fun volumeUo(){
        if (volume < 1.0f){
            volume += 0.1f
            player.setVolume(volume, volume)
            _volumeViewIsVisible.value = _volumeViewIsVisible.value?.plus(1)
            viewModelScope.launch {
                delay(1000)
                _volumeViewIsVisible.value = _volumeViewIsVisible.value?.minus(1)
            }
        }
    }

    fun volumeDown(){
        if (volume > 0f){
            volume -= 0.1f
            player.setVolume(volume, volume)
            _volumeViewIsVisible.value = _volumeViewIsVisible.value?.plus(1)
            viewModelScope.launch {
                delay(1000)
                _volumeViewIsVisible.value = _volumeViewIsVisible.value?.minus(1)
            }
        }
    }

    fun compareTime(endTime: String): Long{
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z")
        format.timeZone = TimeZone.getTimeZone("UTC")

        val currentTime = Date()
        val songEndTime = format.parse(endTime)

        if(songEndTime != null && songEndTime.after(currentTime)){
            var duration = songEndTime.time  - currentTime.time
            duration += 500
            return duration
        }else{
            //if the end_time string from currentSong is invalid this will restart marqueeTextUpdate in a minute
            return 60000
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