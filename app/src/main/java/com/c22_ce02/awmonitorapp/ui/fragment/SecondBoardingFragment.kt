package com.c22_ce02.awmonitorapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.FragmentSecondBoardingBinding


class SecondBoardingFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSecondBoardingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecondBoardingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnFinish = binding.btnFinish
        btnFinish.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_finish) {
            v.startAnimation(AlphaAnimation(1f, .7f))
            val mSecondSlideFragment = ThirdBoardingFragment()
            val mFragmentManager = parentFragmentManager
            mFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out
                )
                replace(
                    R.id.frame_container,
                    mSecondSlideFragment,
                    ThirdBoardingFragment::class.java.simpleName
                )
                addToBackStack(null)
                commit()
            }
        }
    }
}