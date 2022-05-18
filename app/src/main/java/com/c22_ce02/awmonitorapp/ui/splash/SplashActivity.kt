package com.c22_ce02.awmonitorapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.c22_ce02.awmonitorapp.databinding.ActivitySplashBinding
import com.c22_ce02.awmonitorapp.data.preference.CheckHelper
import com.c22_ce02.awmonitorapp.data.preference.CheckPreference
import com.c22_ce02.awmonitorapp.ui.activity.GetStartedActivity
import com.c22_ce02.awmonitorapp.ui.activity.HomeActivity
import com.c22_ce02.awmonitorapp.utils.setFullscreen

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding

    private lateinit var mCheckPreferences: CheckPreference
    private lateinit var checkHelper: CheckHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullscreen()

        mCheckPreferences = CheckPreference(this)
        checkHelper = mCheckPreferences.getCheck()

        Handler(Looper.getMainLooper()).postDelayed({
            if (checkHelper.isLogin){
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this,GetStartedActivity::class.java))
                finish()
            }
        },SPLASH_TIME_OUT)
    }

    companion object {
        private const val SPLASH_TIME_OUT : Long = 3000
    }
}