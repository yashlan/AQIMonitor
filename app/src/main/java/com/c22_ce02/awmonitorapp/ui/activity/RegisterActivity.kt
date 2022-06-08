package com.c22_ce02.awmonitorapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.databinding.ActivityRegisterBinding
import com.c22_ce02.awmonitorapp.ui.view.model.RegisterViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.RegisterViewModelFactory
import com.c22_ce02.awmonitorapp.utils.*

class RegisterActivity : AppCompatActivity(R.layout.activity_register) {

    private val binding by viewBinding(ActivityRegisterBinding::bind)
    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory()
    }
    private var name: String? = null
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

    private fun checkAllFieldCorrect() {
        checkButtonHandler.removeCallbacksAndMessages(null)
        checkButtonHandler.postDelayed({
            binding.btnRegister.isEnabled = setupEnabled()
        }, DELAY_CHECK_FIELD)
    }

    private fun setupEnabled(): Boolean =
        with(binding) {
            editTextName.isNotError() && !name.isNullOrEmpty() &&
                    editTextEmail.isNotError() && !email.isNullOrEmpty() &&
                    editTextPassword.isNotError() && !password.isNullOrEmpty()
        }

    private fun setupComponent() {
        with(binding) {
            btnRegister.setOnClickListener {
                editTextName.hideSoftKeyboard()
                editTextEmail.hideSoftKeyboard()
                editTextPassword.hideSoftKeyboard()
                showLoadingDialog()
                it.startAnimation(AlphaAnimation(1f, .5f))
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    register()
                }, DELAY_REGISTER)

                tvLogin.setOnClickListener {
                    it.startAnimation(AlphaAnimation(1f, .5f))
                    startActivity(
                        Intent(
                            this@RegisterActivity,
                            LoginActivity::class.java
                        )
                    )
                    finish()
                }

                editTextName.doOnTextChanged { it, _, _, _ ->
                    name = it.toString()
                    checkAllFieldCorrect()
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
    }

    private fun register() {
        name?.let { name ->
            email?.let { email ->
                password?.let { password ->
                    registerViewModel.register(
                        name,
                        email,
                        password,
                        onSuccess = { data ->
                            hideLoadingDialog()
                            if (data != null) {
                                showToast("Akun berhasil dibuat!")
                                startActivity(
                                    Intent(
                                        this@RegisterActivity,
                                        LoginActivity::class.java
                                    )
                                )
                                finish()
                            }
                        },
                        onError = { errorMsg ->
                            hideLoadingDialog()
                            if (errorMsg != null) {
                                showToast(errorMsg)
                            }
                        }
                    )
                }
            }
        }
    }

    companion object {
        private const val DELAY_CHECK_FIELD: Long = 200
        private const val DELAY_REGISTER: Long = 2000
    }
}