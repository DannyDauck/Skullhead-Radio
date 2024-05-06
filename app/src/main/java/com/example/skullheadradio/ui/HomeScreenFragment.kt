package com.example.skullheadradio.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.skullheadradio.R
import com.example.skullheadradio.databinding.FragmentHomeScreenBinding
import com.example.skullheadradio.tools.SongProgressbarHandler
import com.example.skullheadradio.viewmodel.MainViewModel
import kotlin.math.roundToInt

class HomeScreenFragment: Fragment() {

    private lateinit var bnd: FragmentHomeScreenBinding
    private var player: MediaPlayer? = null
    private val vm: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("HomeView onCreate")
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bnd = FragmentHomeScreenBinding.inflate(inflater, container, false)
        vm.getAllGenres()
        vm.startMarqueeTextUpdate()
        return bnd.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //first move out the media control view and then check if there is a station and move it in if is
        bnd.homeScreenFragmentMediaControll.translationY = 200f
        bnd.hoemScreenVolumeViewFg.scaleX = vm.volume
        bnd.hoemScreenVolumeViewFg.scaleY = vm.volume
        bnd.hoemScreenVolumeViewFgStroke.scaleY = vm.volume
        bnd.hoemScreenVolumeViewFgStroke.scaleX = vm.volume


        if(vm.currentStation.value == null){
            bnd.homeScreenNoRadioSelectedTxt.isVisible = true
            bnd.homeScreenStationContainer.isVisible = false

        }else{
            bnd.homeScreenNoRadioSelectedTxt.isVisible = false
            bnd.homeScreenStationContainer.isVisible = true
            bnd.marqueeText.text = vm.marqueeText.value
            bnd.marqueeText.isSelected = true
            bnd.homeScreenFragmentMediaControll.animate()
                .translationY(0F)
                .setDuration(800)
                .start()
        }

        if(vm.isPlayingRadio()){
            bnd.homeScreenPlayBtnPlayImage.setImageResource(R.drawable.pause_uncircled)
        }else{
            bnd.homeScreenPlayBtnPlayImage.setImageResource(R.drawable.play_uncircled)
        }

        bnd.hooemScreenPlayBtn.setOnClickListener {
            if(vm.isPlayingRadio()){
                vm.pause(null)
                bnd.homeScreenPlayBtnPlayImage.setImageResource(R.drawable.play_uncircled)
            }else{
                bnd.homeScreenPlayBtnPlayImage.setImageResource(R.drawable.pause_uncircled)
                vm.restartRadio()
            }
        }

        if(vm.currentSong.value != null){
            val song = vm.currentSong.value!!
            bnd.homeScreenCurrentSongName.text = song.title
            bnd.homeScreenCurrentSongArtist.text = song.artist.name
            bnd.homeScreenCurrentSongAlbum.text = song.album
            bnd.homeScreenCurrentSongReleaseYear.text = song.releaseyear
            bnd.marqueeText.isSelected = true
            val progressbarHandler = SongProgressbarHandler()
            progressbarHandler.generateProgressView(bnd.homeScreenCurrentSongProgress, bnd.homeScreenCurrentSongRemainTime, bnd.homeScreenCurrentSongElapsedTime, song.started_at, song.ends_at, requireContext())
        }

        vm.currentStation.observe(viewLifecycleOwner){
            if (vm.currentStation.value != null){
                val station = vm.currentStation.value!!
                bnd.homeScreenStationname.text = station.display_name
                bnd.homeScreenStationGenres.text = station.genres.joinToString(separator = ", ")
                bnd.homeScreenStationLocation.text = station.location
                if(station.current_playlist != null) {
                    bnd.homeScreenPlaylistname.text = station.current_playlist.name
                    if (station.current_playlist.description != null) {
                        bnd.homeScreenPlaylistDescription.text =
                            station.current_playlist.description
                    } else {
                        bnd.homeScreenPlaylistDescription.isVisible = false
                    }
                }else{
                    bnd.homeScreenPlaylistcontainer.isVisible = false
                }
                Glide.with(requireContext())
                    .load(station.images.station)
                    .placeholder(R.drawable.radio)
                    .into(bnd.homeScreenStationImage)
            }
        }

        vm.currentSong.observe(viewLifecycleOwner){
            if(vm.currentSong.value != null){
                val song = vm.currentSong.value!!
                bnd.homeScreenCurrentSongName.text = song.title
                bnd.homeScreenCurrentSongArtist.text = song.artist.name
                bnd.homeScreenCurrentSongAlbum.text = song.album ?: ""
                bnd.homeScreenCurrentSongReleaseYear.text = song.releaseyear ?: ""
                val progressbarHandler = SongProgressbarHandler()
                progressbarHandler.generateProgressView(bnd.homeScreenCurrentSongProgress, bnd.homeScreenCurrentSongRemainTime, bnd.homeScreenCurrentSongElapsedTime, song.started_at, song.ends_at, requireContext())
            }
        }
        bnd.homeScreenSearch.setOnClickListener {
            findNavController().navigate(HomeScreenFragmentDirections.actionHomeScreenFragmentToSearchScreenFragment())
        }

        bnd.homeScreenVolumeUp.setOnClickListener {
            vm.volumeUo()
        }

        bnd.homeScreenVolumeDown.setOnClickListener{
            vm.volumeDown()
        }

        vm.marqueeText.observe(viewLifecycleOwner){
            bnd.marqueeText.text = vm.marqueeText.value
            bnd.marqueeText.isSelected = true
        }



        vm.volumeViewIsVisible.observe(viewLifecycleOwner){
            if(it == 0){
                bnd.homeScreenVolumeViewBg.isVisible = false
                bnd.hoemScreenVolumeViewFg.isVisible = false
                bnd.homeScreenVolumeTxt.isVisible = false
                bnd.hoemScreenVolumeViewFgStroke.isVisible = false
            }else{
                bnd.homeScreenVolumeViewBg.isVisible = true
                bnd.hoemScreenVolumeViewFg.isVisible = true
                bnd.hoemScreenVolumeViewFgStroke.isVisible = true
                bnd.homeScreenVolumeTxt.isVisible = true
                val newVolume = (vm.volume * 100).roundToInt()
                bnd.homeScreenVolumeTxt.text = "Volume\n$newVolume%"
                bnd.hoemScreenVolumeViewFg.animate()
                    .scaleX(vm.volume)
                    .scaleY(vm.volume)
                    .setDuration(300)
                    .start()
                bnd.hoemScreenVolumeViewFgStroke.animate()
                    .scaleX(vm.volume)
                    .scaleY(vm.volume)
                    .setDuration(300)
                    .start()
            }
        }
    }
}