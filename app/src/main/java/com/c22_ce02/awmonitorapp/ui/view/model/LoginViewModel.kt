package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.repository.LoginRepository
import com.c22_ce02.awmonitorapp.data.response.LoginResponse
import com.c22_ce02.awmonitorapp.utils.translateError
import retrofit2.*

class LoginViewModel(private val repository: LoginRepository) : ViewModel() {

    fun login(
        email: String,
        password: String,
        onSuccess: (LoginResponse?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val call = repository.login(email, password)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    onSuccess(response.body())
                } else {
                    onError(response.translateError())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onError(t.message)
            }
        })
    }
}