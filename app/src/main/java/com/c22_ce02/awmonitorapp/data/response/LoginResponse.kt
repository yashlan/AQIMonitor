package com.c22_ce02.awmonitorapp.data.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    val data: Data
) {
    data class Data(
        @SerializedName("name")
        val name: String,
        @SerializedName("email")
        val email: String
    )
}