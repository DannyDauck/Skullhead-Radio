package com.example.skullheadradio.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.skullheadradio.R
import com.example.skullheadradio.databinding.FragmentHomeScreenBinding
import java.io.IOException

class HomeScreenFragment: Fragment() {

    private lateinit var bnd: FragmentHomeScreenBinding
    private var player: MediaPlayer? = null

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
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        bnd.marqueeText.isSelected = true

        bnd.homePlay.setOnClickListener {
            println("play stream")
            player = MediaPlayer()
            try{
                player?.apply {
                    setDataSource("https://stream.laut.fm/jahfari")
                    setOnPreparedListener {
                        start()
                        bnd.homscreenBg.setImageResource(R.drawable.rastafariskullhead)
                        bnd.marqueeText.text = "playing JAHFARI Radio"
                        bnd.marqueeText.isSelected = true
                    }
                    prepareAsync()
                }
            }catch(e: IOException){
                e.printStackTrace()
            }
        }

        bnd.homeStop.setOnClickListener {
            println("stop stream")
            bnd.marqueeText.text = getString(R.string.no_station_is_playing)
            bnd.marqueeText.isSelected = true
            player?.pause()
        }

        bnd.homeScreenSearch.setOnClickListener {
            findNavController().navigate(HomeScreenFragmentDirections.actionHomeScreenFragmentToSearchScreenFragment())
        }
    }
}