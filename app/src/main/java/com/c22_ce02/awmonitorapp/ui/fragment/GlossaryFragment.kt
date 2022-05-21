package com.c22_ce02.awmonitorapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.FragmentGlossaryBinding
import com.c22_ce02.awmonitorapp.utils.viewBinding

class GlossaryFragment : Fragment(R.layout.fragment_glossary) {

    private val binding by viewBinding<FragmentGlossaryBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btn.setOnClickListener{

        }
        binding.tvTest.text = "Ini Fragment Glosarium"
    }


    companion object {

    }
}