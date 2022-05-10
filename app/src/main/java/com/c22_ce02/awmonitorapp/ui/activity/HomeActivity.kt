package com.c22_ce02.awmonitorapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.ActivityHomeBinding
import com.c22_ce02.awmonitorapp.utils.viewBinding

class HomeActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityHomeBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.tvHello.text = getString(R.string.app_name)

    }
}