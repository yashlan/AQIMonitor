package com.c22_ce02.awmonitorapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.FragmentFirstSlideBinding


class FirstSlideFragment : Fragment(), View.OnClickListener {

    private lateinit var binding : FragmentFirstSlideBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstSlideBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnStarted = binding.btnStarted
        btnStarted.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_started){
            val mSecondSlideFragment = SecondSlideFragment()
            val mFragmentManager = parentFragmentManager
            mFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out
                )
                replace(R.id.frame_container,mSecondSlideFragment,SecondSlideFragment::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }
    }
}