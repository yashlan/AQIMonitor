package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.repository.RegisterRepository
import com.c22_ce02.awmonitorapp.data.response.RegisterResponse
import com.c22_ce02.awmonitorapp.utils.translateError
import retrofit2.*

class RegisterViewModel(private val repository: RegisterRepository) : ViewModel() {

    fun register(
        name: String,
        email: String,
        password: String,
        onSuccess: (RegisterResponse?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val call = repository.register(name, email, password)
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body())
                } else {
                    onError(response.translateError())
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                onError(t.message)
            }
        })
    }
}