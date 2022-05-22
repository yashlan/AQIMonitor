package com.c22_ce02.awmonitorapp.data.model

import com.google.gson.annotations.SerializedName

data class CurrentAirQualityResponse(
    @SerializedName("data")
    val data: List<Data>
) {
    data class Data(
        @SerializedName("aqi")
        val aqi: Double,
        @SerializedName("o3")
        val o3: Double,
        @SerializedName("so2")
        val so2: Double,
        @SerializedName("no2")
        val no2: Double,
        @SerializedName("co")
        val co: Double,
        @SerializedName("pm10")
        val pm10: Double,
        @SerializedName("pm25")
        val pm25: Double,
    )
}