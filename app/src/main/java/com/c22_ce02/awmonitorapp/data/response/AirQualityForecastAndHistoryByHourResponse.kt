package com.c22_ce02.awmonitorapp.data.response

import com.google.gson.annotations.SerializedName

data class AirQualityForecastAndHistoryByHourResponse(
    @SerializedName("data")
    val data: Data
) {
    data class Data(
        @SerializedName("forecast")
        val forecast: List<Forecast>,
        @SerializedName("history")
        val history: List<History>
    )

    data class Forecast(
        @SerializedName("aqi")
        val aqi: Double,
        @SerializedName("co")
        val co: Double,
        @SerializedName("no2")
        val no2: Double,
        @SerializedName("o3")
        val o3: Double,
        @SerializedName("pm10")
        val pm10: Double,
        @SerializedName("pm25")
        val pm25: Double,
        @SerializedName("so2")
        val so2: Double,
    )

    data class History(
        @SerializedName("aqi")
        val aqi: Double,
        @SerializedName("co")
        val co: Double,
        @SerializedName("no2")
        val no2: Double,
        @SerializedName("o3")
        val o3: Double,
        @SerializedName("pm10")
        val pm10: Double,
        @SerializedName("pm25")
        val pm25: Double,
        @SerializedName("so2")
        val so2: Double,
    )
}