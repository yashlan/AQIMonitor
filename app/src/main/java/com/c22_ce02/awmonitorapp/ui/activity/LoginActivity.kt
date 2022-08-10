package com.c22_ce02.awmonitorapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.data.preference.UserPreference
import com.c22_ce02.awmonitorapp.databinding.ActivityLoginBinding
import com.c22_ce02.awmonitorapp.ui.view.model.LoginViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.LoginViewModelFactory
import com.c22_ce02.awmonitorapp.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread


class LoginActivity : AppCompatActivity(R.layout.activity_login) {

    private val binding by viewBinding(ActivityLoginBinding::bind)
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory()
    }

    private var email: String? = null
    private var password: String? = null
    private val checkButtonHandler = Handler(Looper.getMainLooper())
    private val nameDummy = "Dummy Name"
    private val emailDummy = "dummyUser@gmail.com"
    private val passwordDummy = "12345678"

    override fun onBackPressed() {
        /*agar tombol back tidak berfungsi*/
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        forcePortraitScreenOrientation()
        setFullscreen()
        super.onCreate(savedInstanceState)

        setupComponent()

    }

    private fun setupComponent() {
        with(binding) {
            btnLogin.setOnClickListener {
                editTextPassword.hideSoftKeyboard()
                editTextEmail.hideSoftKeyboard()
                it.startAnimation(AlphaAnimation(1f, .5f))
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    login()
                }, DELAY_LOGIN)
            }

            val d = Handler(Looper.getMainLooper())
            d.postDelayed({
                showLoadingDialog()
            }, 2000)

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                login()
            }, 4000)


            tvRegister.setOnClickListener {
                it.startAnimation(AlphaAnimation(1f, .5f))
/*                startActivity(
                    Intent(
                        this@LoginActivity,
                        RegisterActivity::class.java
                    )
                )
                finish()*/
            }

            editTextEmail.isEnabled = false
            editTextPassword.isEnabled = false


            editTextEmail.setText(emailDummy)
            editTextPassword.setText(passwordDummy)

/*            editTextEmail.doOnTextChanged { it, _, _, _ ->
                email = it.toString()
                checkAllFieldCorrect()
            }

            editTextPassword.doOnTextChanged { it, _, _, _ ->
                password = it.toString()
                checkAllFieldCorrect()
            }*/
        }
    }

    private fun checkAllFieldCorrect() {
        checkButtonHandler.removeCallbacksAndMessages(null)
        checkButtonHandler.postDelayed({
            binding.btnLogin.isEnabled = setupEnabled()
        }, DELAY_CHECK_FIELD)
    }

    private fun setupEnabled(): Boolean =
        with(binding) {
            editTextEmail.isNotError() && !email.isNullOrEmpty() &&
                    editTextPassword.isNotError() && !password.isNullOrEmpty()
        }

    private fun login() {
        hideLoadingDialog()
        val userPref = UserPreference(this@LoginActivity)
        userPref.saveSession(
            name = nameDummy,
            email = emailDummy,
            onSave = { savedName, savedEmail ->
                if (savedName != null && savedEmail != null) {
                    startActivity(
                        Intent(
                            this@LoginActivity,
                            HomeActivity::class.java
                        )
                    )
                    finish()
                }
            }
        )
        /*email?.let { email ->
            password?.let { pw ->
                loginViewModel.login(
                    email,
                    pw,
                    onSuccess = { data ->
                        if (data != null) {

                        }
                    },
                    onError = { errorMsg ->
                        hideLoadingDialog()
                        if (errorMsg != null) {
                            showToast(errorMsg)
                        }
                    })
            }
        }*/
    }

    companion object {
        private const val DELAY_CHECK_FIELD: Long = 200
        private const val DELAY_LOGIN: Long = 2000
    }
}