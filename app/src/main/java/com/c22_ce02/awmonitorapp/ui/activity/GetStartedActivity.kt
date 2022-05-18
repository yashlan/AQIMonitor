package com.c22_ce02.awmonitorapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.c22_ce02.awmonitorapp.databinding.ActivityGetStartedBinding
import com.c22_ce02.awmonitorapp.data.preference.CheckHelper
import com.c22_ce02.awmonitorapp.data.preference.CheckPreference

class GetStartedActivity : AppCompatActivity() {

    // binding init
    private lateinit var binding : ActivityGetStartedBinding
    private lateinit var checkHelper: CheckHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.title = "Get Started"
        checkHelper = CheckHelper()

        binding.button.setOnClickListener {
            savePref(true)
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun savePref(isLogin: Boolean) {
        val checkPreference = CheckPreference(this)
        checkHelper.isLogin = isLogin
        checkPreference.setCheck(checkHelper)
        Toast.makeText(this,"Selamat Datang",Toast.LENGTH_SHORT).show()
    }
}