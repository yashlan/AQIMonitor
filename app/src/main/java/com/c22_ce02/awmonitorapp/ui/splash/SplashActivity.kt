package com.c22_ce02.awmonitorapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.c22_ce02.awmonitorapp.databinding.ActivitySplashBinding
import com.c22_ce02.awmonitorapp.ui.activity.HomeActivity
import com.c22_ce02.awmonitorapp.utils.setFullscreen
import com.c22_ce02.awmonitorapp.utils.viewBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivitySplashBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setFullscreen()
        super.onCreate(savedInstanceState)
        binding.tvLogo.text = "Logo HERE"
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, 1000)
    }
}