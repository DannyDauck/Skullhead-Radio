package com.example.skullheadradio.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.skullheadradio.databinding.FragmentHomeScreenBinding
import java.io.IOException

class HomeScreenFragment: Fragment() {

    private lateinit var bnd: FragmentHomeScreenBinding
    private var player: MediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        bnd.homePlay.setOnClickListener {
            player = MediaPlayer()
            try{
                player.setDataSource("https://stream.laut.fm/jahfari")
                player.start()
            }catch(e: IOException){
                e.printStackTrace()
            }
        }

        bnd.homePlay.setOnClickListener {
            player.pause()
        }
    }
}