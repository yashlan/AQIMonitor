package com.c22_ce02.awmonitorapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.FragmentGlossaryBinding

class GlossaryFragment : Fragment(R.layout.fragment_glossary) {

    private val binding by viewBinding(FragmentGlossaryBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tvTest.text = "Ini Fragment Glosarium"
    }


    companion object {

    }
}