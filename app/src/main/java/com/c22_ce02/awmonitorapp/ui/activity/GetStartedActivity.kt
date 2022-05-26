package com.c22_ce02.awmonitorapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.ActivityGetStartedBinding
import com.c22_ce02.awmonitorapp.data.preference.CheckHelper
import com.c22_ce02.awmonitorapp.data.preference.CheckPreference
import com.c22_ce02.awmonitorapp.utils.setFullscreen
import com.c22_ce02.awmonitorapp.utils.showToast

class GetStartedActivity : AppCompatActivity(R.layout.activity_get_started) {

    private val binding by viewBinding(ActivityGetStartedBinding::bind)
    private lateinit var checkHelper: CheckHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        setFullscreen()
        super.onCreate(savedInstanceState)
        this.title = getString(R.string.get_started)
        checkHelper = CheckHelper()

        binding.button.setOnClickListener {
            savePref()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun savePref() {
        val checkPreference = CheckPreference(this)
        checkHelper.isLogin = true
        checkPreference.setCheck(checkHelper)
        if (BuildConfig.DEBUG) {
            showToast("Selamat Datang")
        }
    }
}