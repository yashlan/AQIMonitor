package com.c22_ce02.awmonitorapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.FragmentFirstBoardingBinding


class FirstBoardingFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentFirstBoardingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnNext = binding.btnNext
        val btnSkip = binding.btnSkip

        btnNext.setOnClickListener(this)
        btnSkip.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_next) {
            v.startAnimation(AlphaAnimation(1f, .7f))
            val mThirdSlideFragment = SecondBoardingFragment()
            val mFragmentManager = parentFragmentManager
            mFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out
                )
                replace(
                    R.id.frame_container,
                    mThirdSlideFragment,
                    SecondBoardingFragment::class.java.simpleName
                )
                addToBackStack(null)
                commit()
            }
        } else if (v?.id == R.id.btn_skip) {
            v.startAnimation(AlphaAnimation(1f, .7f))
            val mThirdSlideFragment = ThirdBoardingFragment()
            val mFragmentManager = parentFragmentManager
            mFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out
                )
                replace(
                    R.id.frame_container,
                    mThirdSlideFragment,
                    ThirdBoardingFragment::class.java.simpleName
                )
                addToBackStack(null)
                commit()
            }
        }
    }
}
