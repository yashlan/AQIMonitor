package com.c22_ce02.awmonitorapp.data.repository

import com.c22_ce02.awmonitorapp.api.ApiService

class LoginRepository(private val apiService: ApiService) {
    fun login(
        email: String,
        password: String
    ) = apiService.login(email, password)
}