package com.c22_ce02.awmonitorapp.data.response

import com.google.gson.annotations.SerializedName

data class WeatherHistoryAndForecastByHourResponse(
    @SerializedName("data")
    val data: Data
) {
    data class Data(
        @SerializedName("history")
        val history: List<History>,
        @SerializedName("forecast")
        val forecast: List<Forecast>
    )

    data class History(
        @SerializedName("rh")
        val humidity: Double,
        @SerializedName("temp")
        val temperature: Double,
        @SerializedName("wind_spd")
        val windSpeed: Double
    )

    data class Forecast(
        @SerializedName("rh")
        val humidity: Double,
        @SerializedName("temp")
        val temperature: Double,
        @SerializedName("wind_spd")
        val windSpeed: Double
    )
}