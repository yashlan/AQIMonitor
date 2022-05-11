package com.c22_ce02.awmonitorapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.ActivityHomeBinding
import com.c22_ce02.awmonitorapp.utils.loadImageViaGlide
import com.c22_ce02.awmonitorapp.utils.showToast
import com.c22_ce02.awmonitorapp.utils.viewBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityHomeBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }else {
            binding.tvName.text = "nama : " + user.displayName
            binding.tvEmail.text = "email : " + user.email
            loadImageViaGlide(user.photoUrl, binding.imgPhoto)
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
    
    private fun logout() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                else {
                    showToast(task.result.toString())
                }
            }
    }
}