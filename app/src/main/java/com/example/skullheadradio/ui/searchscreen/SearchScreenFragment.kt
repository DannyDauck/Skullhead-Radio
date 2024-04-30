package com.example.skullheadradio.ui.searchscreen

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.skullheadradio.adapter.GenreAdapter
import com.example.skullheadradio.databinding.FragmentSearchScreenBinding
import com.example.skullheadradio.viewmodel.MainViewModel

class SearchScreenFragment(): Fragment() {

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
        bnd.rvGenres.adapter = adapter
        bnd.searchScreenGenreContainer.translationX = genreContainerOffset
        bnd.searchScreenCloseBigBtn.translationX = -genreContainerOffset

        bnd.searchScreenHome.setOnClickListener {
            findNavController().navigate(SearchScreenFragmentDirections.actionSearchScreenFragmentToHomeScreenFragment())
        }
    }
}