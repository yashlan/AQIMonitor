package com.c22_ce02.awmonitorapp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.ActivityLoginBinding
import com.c22_ce02.awmonitorapp.utils.setFullscreen
import com.c22_ce02.awmonitorapp.utils.showToast
import com.c22_ce02.awmonitorapp.utils.viewBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityLoginBinding>()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        onSignInResult(result)
    }

    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setFullscreen()
        setupSign()
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    private fun setupSign() {
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(providers)
            .setTheme(R.style.LoginTheme)
            .setTosAndPrivacyPolicyUrls(
                "https://example.com/terms.html",
                "https://example.com/privacy.html")
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        } else {
            if(response?.error?.errorCode == null) {
                finishAffinity()
                return
            }
            showToast("Error ${response?.error?.errorCode} : " + response?.error?.message.toString())
        }
    }
}