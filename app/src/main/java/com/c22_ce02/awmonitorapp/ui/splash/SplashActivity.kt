package com.c22_ce02.awmonitorapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.ActivitySplashBinding
import com.c22_ce02.awmonitorapp.data.preference.CheckHelper
import com.c22_ce02.awmonitorapp.data.preference.CheckPreference
import com.c22_ce02.awmonitorapp.ui.activity.HomeActivity
import com.c22_ce02.awmonitorapp.ui.activity.LoginActivity
import com.c22_ce02.awmonitorapp.ui.activity.OnBoardingActivity
import com.c22_ce02.awmonitorapp.utils.forcePortraitScreenOrientation
import com.c22_ce02.awmonitorapp.utils.loadImageViaGlide
import com.c22_ce02.awmonitorapp.utils.setFullscreen
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(R.layout.activity_splash) {

    private val binding by viewBinding(ActivitySplashBinding::bind)

    private lateinit var mCheckPreferences: CheckPreference
    private lateinit var checkHelper: CheckHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        forcePortraitScreenOrientation()
        setFullscreen()
        super.onCreate(savedInstanceState)
        loadImageViaGlide(R.drawable.logo_no_background, binding.imageView)
        mCheckPreferences = CheckPreference(this)
        checkHelper = mCheckPreferences.getCheckBoarding()
        val user = FirebaseAuth.getInstance().currentUser
        Handler(Looper.getMainLooper()).postDelayed({
            when {
                user == null && checkHelper.isUserFinishBoarding -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                user == null && !checkHelper.isUserFinishBoarding -> {
                    startActivity(Intent(this, OnBoardingActivity::class.java))
                    finish()
                }
                else -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
            }
        }, SPLASH_TIME_OUT)
    }

    companion object {
        private const val SPLASH_TIME_OUT: Long = 3000
    }
}