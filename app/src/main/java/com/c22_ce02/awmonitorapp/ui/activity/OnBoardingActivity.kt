package com.c22_ce02.awmonitorapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.ActivityOnBoardingBinding
import com.c22_ce02.awmonitorapp.ui.fragment.FirstBoardingFragment
import com.c22_ce02.awmonitorapp.utils.forcePortraitScreenOrientation
import com.c22_ce02.awmonitorapp.utils.setFullscreen


class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        forcePortraitScreenOrientation()
        setFullscreen()
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mFragmentManager = supportFragmentManager
        val mFirstSlideFragment = FirstBoardingFragment()

        mFragmentManager
            .beginTransaction()
            .add(
                R.id.frame_container,
                mFirstSlideFragment,
                mFirstSlideFragment::class.java.simpleName
            )
            .commit()
    }
}