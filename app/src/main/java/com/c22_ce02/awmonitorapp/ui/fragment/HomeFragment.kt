package com.c22_ce02.awmonitorapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.FragmentHomeBinding
import com.c22_ce02.awmonitorapp.utils.viewBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding<FragmentHomeBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        //todo
    }
}