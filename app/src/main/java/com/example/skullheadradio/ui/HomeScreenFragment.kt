package com.example.skullheadradio.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.skullheadradio.R
import com.example.skullheadradio.databinding.FragmentHomeScreenBinding
import com.example.skullheadradio.tools.SongProgressbarHandler
import com.example.skullheadradio.viewmodel.MainViewModel

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
    ): View? {
        bnd = FragmentHomeScreenBinding.inflate(inflater, container, false)
        vm.getAllGenres()
        vm.startMarqueeTextUpdate()
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if(vm.currentStation.value == null){
            bnd.homeScreenNoRadioSelectedTxt.isVisible = true
            bnd.homeScreenStationContainer.isVisible = false
        }else{
            bnd.homeScreenNoRadioSelectedTxt.isVisible = false
            bnd.homeScreenStationContainer.isVisible = true
            bnd.marqueeText.text = vm.marqueeText.value
            bnd.marqueeText.isSelected = true
        }

        if(vm.currentSong.value != null){
            val song = vm.currentSong.value!!
            bnd.homeScreenCurrentSongName.text = song.title
            bnd.homeScreenCurrentSongArtist.text = song.artist.name
            bnd.homeScreenCurrentSongAlbum.text = song.album
            bnd.homeScreenCurrentSongReleaseYear.text = song.releaseyear
            val progressbarHandler = SongProgressbarHandler()
            progressbarHandler.generateProgressView(bnd.homeScreenCurrentSongProgress, bnd.homeScreenCurrentSongRemainTime, bnd.homeScreenCurrentSongElapsedTime, song.started_at, song.ends_at, requireContext())
        }

        vm.marqueeText.observe(viewLifecycleOwner){
            bnd.marqueeText.text = vm.marqueeText.value
            bnd.marqueeText.isSelected = true
        }

        vm.currentStation.observe(viewLifecycleOwner){
            if (vm.currentStation.value != null){
                val station = vm.currentStation.value!!
                bnd.homeScreenStationname.text = station.display_name
                bnd.homeScreenStationGenres.text = station.genres.joinToString(separator = ", ")
                bnd.homeScreenStationLocation.text = station.location
                if(station.current_playlist != null) {
                    bnd.homeScreenPlaylistname.text = station.current_playlist?.name
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
    }
}