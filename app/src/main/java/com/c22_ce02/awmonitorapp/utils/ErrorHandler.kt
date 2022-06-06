package com.c22_ce02.awmonitorapp.utils

import com.c22_ce02.awmonitorapp.data.response.ErrorResponse
import com.google.gson.Gson
import retrofit2.Response

fun <T> Response<T>.translateError(): String {
    return Gson().fromJson(
        errorBody()?.string(),
        ErrorResponse::class.java
    ).message
}