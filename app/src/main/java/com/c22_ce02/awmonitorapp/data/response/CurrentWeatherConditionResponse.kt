package com.c22_ce02.awmonitorapp.data.response

import com.google.gson.annotations.SerializedName

data class CurrentWeatherConditionResponse(
    @SerializedName("data")
    val data: List<Data>,
) {
    data class Data(
        @SerializedName("aqi")
        val aqi: Double,
        @SerializedName("rh")
        val humidity: Double,
        @SerializedName("wind_spd")
        val windSpeed: Double,
        @SerializedName("temp")
        val temperature: Double,
        @SerializedName("ob_time")
        val obTime: String,
    )
}