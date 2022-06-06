package com.c22_ce02.awmonitorapp.data.repository

import com.c22_ce02.awmonitorapp.api.ApiService

class RegisterRepository(private val apiService: ApiService) {
    fun register(
        name: String,
        email: String,
        password: String
    ) = apiService.register(name, email, password)
}