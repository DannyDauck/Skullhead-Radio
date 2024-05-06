package com.example.skullheadradio.ui

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
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
        bnd.searchItName.translationX = 1300f
        vm.genreFilterString = ""
        var adapter = GenreAdapter(vm.genres.sortedBy { vm.currentGenres.value?.contains(it)==false }, vm, requireContext())
        var stationAdapter = StationAdapter(listOf(), vm, requireContext())
        bnd.rvGenres.adapter = adapter
        bnd.rvStation.adapter = stationAdapter
        bnd.searchScreenGenreContainer.translationX = genreContainerOffset
        bnd.searchScreenCloseBigBtn.translationX = -genreContainerOffset
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        bnd.searchScreenHome.setOnClickListener {
            findNavController().navigate(SearchScreenFragmentDirections.actionSearchScreenFragmentToHomeScreenFragment())
        }

        bnd.searchScreenBtnGenre.setOnClickListener {
            //move spinner
            bnd.searchScreenGenreContainer.animate()
                .translationX(0f)
                .setDuration(500)
                .start()
            bnd.searchScreenCloseBigBtn.animate()
                .translationX(0f)
                .setDuration(500)
            if(bnd.searchItName.translationX == 0f){
                bnd.searchItName.animate()
                    .translationX(1300f)
                    .setDuration(1000)
                    .start()
                bnd.rvStation.animate()
                    .translationY(0f)
                    .setDuration(500)
                    .setStartDelay(1000)
                    .start()
            }
            println("all genres: " + vm.allGenres)
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

        bnd.searchScreenBtnName.setOnClickListener {
            var offset = if (bnd.searchItName.translationX == 0f) {
                1300.0f
            } else {
                0f
            }
            bnd.searchItName.animate()
                .translationX(offset)
                .setDuration(1000)
                .setStartDelay(if (offset == 0f) 200 else 0)
                .start()

            bnd.rvStation.animate()
                .translationY(if (bnd.rvStation.translationY == 0f) (bnd.searchItName.height.toFloat() + 10f) else 0f)
                .setDuration(500)
                .setStartDelay(if (bnd.rvStation.translationY == 0f) 0 else 500)
                .start()
            if (offset == 0f) {
                bnd.searchItName.requestFocus()
                imm.showSoftInput(bnd.searchItName, InputMethodManager.SHOW_IMPLICIT)
            } else {
                bnd.searchItName.requestFocus()
                imm.hideSoftInputFromWindow(bnd.searchItName.windowToken, 0)
            }
        }

        bnd.searchItName.addTextChangedListener {
            vm.nameFilterString = bnd.searchItName.text.toString()
            if(vm.currentGenres.value.isNullOrEmpty()){
                if(vm.nameFilterString != "") {
                    vm.getStationsByName()
                }else{
                    println("clear stations was called")
                    vm.clearCurrentStations()
                }
            }
            else{
                stationAdapter.update()
            }
        }

        bnd.searchItName.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                keyEvent.action == KeyEvent.ACTION_DOWN &&
                keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {

                //Hide keyboard on pressing return
                imm.hideSoftInputFromWindow(bnd.searchItName.windowToken, 0)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }



        bnd.itGenreSearch.addTextChangedListener{
            vm.genreFilterString = bnd.itGenreSearch.text.toString()
            adapter.update()
        }
    }
}