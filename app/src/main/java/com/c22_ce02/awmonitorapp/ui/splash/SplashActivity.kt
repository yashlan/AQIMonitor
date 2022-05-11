package com.c22_ce02.awmonitorapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.c22_ce02.awmonitorapp.databinding.ActivitySplashBinding
import com.c22_ce02.awmonitorapp.ui.activity.HomeActivity
import com.c22_ce02.awmonitorapp.ui.activity.LoginActivity
import com.c22_ce02.awmonitorapp.utils.setFullscreen
import com.c22_ce02.awmonitorapp.utils.viewBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivitySplashBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullscreen()
        binding.tvLogo.text = "Logo HERE"
        val user = Firebase.auth.currentUser
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(
                Intent(
                    this,
                    if (user != null)
                        HomeActivity::class.java
                    else
                        LoginActivity::class.java
                )
            )
            finish()
        }, 1000)
    }
}