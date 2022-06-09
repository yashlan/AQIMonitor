package com.c22_ce02.awmonitorapp.ui.splash

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.data.preference.CheckHelper
import com.c22_ce02.awmonitorapp.data.preference.CheckPreference
import com.c22_ce02.awmonitorapp.data.preference.UserPreference
import com.c22_ce02.awmonitorapp.databinding.ActivitySplashBinding
import com.c22_ce02.awmonitorapp.ui.activity.HomeActivity
import com.c22_ce02.awmonitorapp.ui.activity.LoginActivity
import com.c22_ce02.awmonitorapp.ui.activity.OnBoardingActivity
import com.c22_ce02.awmonitorapp.utils.forcePortraitScreenOrientation
import com.c22_ce02.awmonitorapp.utils.setFullscreen


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(R.layout.activity_splash) {

    private val binding by viewBinding(ActivitySplashBinding::bind)

    private lateinit var mCheckPreferences: CheckPreference
    private lateinit var checkHelper: CheckHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        forcePortraitScreenOrientation()
        setFullscreen()
        super.onCreate(savedInstanceState)
        "Version ${BuildConfig.VERSION_NAME}".also { binding.tvVersionApp.text = it }
        mCheckPreferences = CheckPreference(this)
        checkHelper = mCheckPreferences.getCheckBoarding()
        val user = UserPreference(this)
        playAnimation()
        Handler(Looper.getMainLooper()).postDelayed({
            when {
                user.isSessionEmpty() && checkHelper.isUserFinishBoarding -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                user.isSessionEmpty() && !checkHelper.isUserFinishBoarding -> {
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

    private fun playAnimation() {
        ObjectAnimator.ofFloat(
            binding.imgLogoLeaf,
            View.ALPHA,
            1f,
            .8f
        ).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }.start()

        ObjectAnimator.ofFloat(
            binding.imgLogoCircle,
            View.ROTATION,
            0f,
            3600f
        ).apply {
            duration = 8000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }.start()

        ObjectAnimator.ofFloat(
            binding.tvAppName,
            View.ALPHA,
            0f,
            1f
        ).apply {
            duration = 1000
        }.start()

        ObjectAnimator.ofFloat(
            binding.tvVersionApp,
            View.ALPHA,
            0f,
            1f
        ).apply {
            duration = 1000
        }.start()
    }

    companion object {
        private const val SPLASH_TIME_OUT: Long = 5000
    }
}