package com.example.skullheadradio.ui

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.skullheadradio.adapter.GenreAdapter
import com.example.skullheadradio.adapter.StationAdapter
import com.example.skullheadradio.databinding.FragmentSearchScreenBinding
import com.example.skullheadradio.viewmodel.MainViewModel
import androidx.core.widget.addTextChangedListener

class SearchScreenFragment() : Fragment() {

    private val vm: MainViewModel by activityViewModels()
    private lateinit var bnd: FragmentSearchScreenBinding
    private var genreContainerOffset: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bnd = FragmentSearchScreenBinding.inflate(inflater, container, false)
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        genreContainerOffset = -screenWidth.toFloat()
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var adapter = GenreAdapter(vm.genres, vm, requireContext())
        var stationAdapter = StationAdapter(listOf(), vm, requireContext())
        bnd.rvGenres.adapter = adapter
        bnd.rvStation.adapter = stationAdapter
        bnd.searchScreenGenreContainer.translationX = genreContainerOffset
        bnd.searchScreenCloseBigBtn.translationX = -genreContainerOffset

        bnd.searchScreenHome.setOnClickListener {
            findNavController().navigate(SearchScreenFragmentDirections.actionSearchScreenFragmentToHomeScreenFragment())
        }

        bnd.searchScreenBtnGenre.setOnClickListener {
            //Spinner ein und ausfahren
            bnd.searchScreenGenreContainer.animate()
                .translationX(0f)
                .setDuration(500)
                .start()
            bnd.searchScreenCloseBigBtn.animate()
                .translationX(0f)
                .setDuration(500)
        }

        bnd.searchScreenCloseBigBtn.setOnClickListener {
            bnd.searchScreenGenreContainer.animate()
                .translationX(genreContainerOffset)
                .setDuration(500)
                .start()
            bnd.searchScreenCloseBigBtn.animate()
                .translationX(-genreContainerOffset)
                .setDuration(500)
        }

        bnd.searchScreenSearchgenreCloseBtn.setOnClickListener {
            bnd.searchScreenGenreContainer.animate()
                .translationX(genreContainerOffset)
                .setDuration(500)
                .start()
            bnd.searchScreenCloseBigBtn.animate()
                .translationX(-genreContainerOffset)
                .setDuration(500)
        }

        vm.currentStations.observe(viewLifecycleOwner){
            println("change was recognized")
            println(vm.currentGenres.value.toString())
            if(vm.currentStations.value.isNullOrEmpty()){
                bnd.searchScreenNoResultText.isVisible = true
                stationAdapter.update()
            }else{
                bnd.searchScreenNoResultText.isVisible = false
                stationAdapter.update()
                println(vm.currentStations.value!!.first().images.station)
            }
        }

        vm.currentStation.observe(viewLifecycleOwner){
            //update the adapter to set the right bg and play or pause image
            stationAdapter.update()
        }

        bnd.itGenreSearch.addTextChangedListener{
            vm.genreFilterString = bnd.itGenreSearch.text.toString()
            adapter.update()
        }
    }
}