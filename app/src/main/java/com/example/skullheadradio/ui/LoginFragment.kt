package com.example.skullheadradio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.skullheadradio.databinding.FragmentLoginBinding

class LoginFragment: Fragment() {

    private lateinit var bnd: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bnd = FragmentLoginBinding.inflate(inflater, container, false)
        return bnd.root
    }

}