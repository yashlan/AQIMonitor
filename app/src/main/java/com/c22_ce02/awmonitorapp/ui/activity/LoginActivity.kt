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

class LoginActivity : AppCompatActivity(R.layout.activity_login) {

    private val binding by viewBinding(ActivityLoginBinding::bind)
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory()
    }

    private var email: String? = null
    private var password: String? = null
    private val checkButtonHandler = Handler(Looper.getMainLooper())


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
                showLoadingDialog()
                it.startAnimation(AlphaAnimation(1f, .5f))
                email?.let { email ->
                    password?.let { pw ->
                        loginViewModel.login(
                            email,
                            pw,
                            onSuccess = { data ->
                                hideLoadingDialog()
                                if (data?.status == 200) {
                                    val userPref = UserPreference(this@LoginActivity)
                                    userPref.saveSession(
                                        data.data.name,
                                        data.data.email,
                                        onSave = {
                                            startActivity(
                                                Intent(
                                                    this@LoginActivity,
                                                    HomeActivity::class.java
                                                )
                                            )
                                            finish()
                                        }
                                    )
                                }
                            },
                            onError = { errorMsg ->
                                hideLoadingDialog()
                                if (errorMsg != null) {
                                    showToast(errorMsg)
                                }
                            })
                    }
                }
            }

            tvRegister.setOnClickListener {
                it.startAnimation(AlphaAnimation(1f, .5f))
                startActivity(
                    Intent(
                        this@LoginActivity,
                        RegisterActivity::class.java
                    )
                )
                finish()
            }

            editTextEmail.doOnTextChanged { it, _, _, _ ->
                email = it.toString()
                checkAllFieldCorrect()
            }

            editTextPassword.doOnTextChanged { it, _, _, _ ->
                password = it.toString()
                checkAllFieldCorrect()
            }
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


    companion object {
        private const val DELAY_CHECK_FIELD: Long = 200
    }
}