package com.c22_ce02.awmonitorapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.ActivityOnBoardingBinding
import com.c22_ce02.awmonitorapp.ui.fragment.FirstSlideFragment
import com.c22_ce02.awmonitorapp.utils.forcePortraitScreenOrientation


class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        forcePortraitScreenOrientation()
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mFragmentManager = supportFragmentManager
        val mFirstSlideFragment = FirstSlideFragment()

        mFragmentManager
            .beginTransaction()
            .add(R.id.frame_container,mFirstSlideFragment,mFirstSlideFragment::class.java.simpleName)
            .commit()
    }
}