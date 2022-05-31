package com.c22_ce02.awmonitorapp.data.response

import com.google.gson.annotations.SerializedName

data class WeatherForecastByHourResponse(
    @SerializedName("data")
    val data: List<Data>
) {
    data class Data(
        @SerializedName("temp")
        val temperature: Double,
        @SerializedName("wind_spd")
        val windSpeed: Double,
        @SerializedName("rh")
        val humidity: Double,
        @SerializedName("timestamp_local")
        val timestamp_local: String,
    )
}