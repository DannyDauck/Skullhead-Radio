package com.example.skullheadradio.ui

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bnd = FragmentSearchScreenBinding.inflate(inflater, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var adapter = GenreAdapter(vm.genres, vm, requireContext())
        bnd.rvGenres.adapter = adapter

        bnd.searchScreenHome.setOnClickListener {
            findNavController().navigate(SearchScreenFragmentDirections.actionSearchScreenFragmentToHomeScreenFragment())
        }
    }
}