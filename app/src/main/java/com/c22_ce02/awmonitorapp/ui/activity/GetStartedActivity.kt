package com.c22_ce02.awmonitorapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c22_ce02.awmonitorapp.databinding.ActivityGetStartedBinding
import com.c22_ce02.awmonitorapp.data.preference.CheckHelper
import com.c22_ce02.awmonitorapp.data.preference.CheckPreference
import com.c22_ce02.awmonitorapp.utils.showToast
import com.c22_ce02.awmonitorapp.utils.viewBinding

class GetStartedActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityGetStartedBinding>()
    private lateinit var checkHelper: CheckHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = "Get Started"
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
        showToast("Selamat Datang")
    }
}