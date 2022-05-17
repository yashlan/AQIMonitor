package com.c22_ce02.awmonitorapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c22_ce02.awmonitorapp.databinding.ActivityGetStartedBinding

class GetStartedActivity : AppCompatActivity() {

    // binding init
    private lateinit var binding : ActivityGetStartedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.title = "Get Started"
    }
}