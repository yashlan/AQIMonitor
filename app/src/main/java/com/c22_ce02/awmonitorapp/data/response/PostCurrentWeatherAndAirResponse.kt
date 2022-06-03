package com.c22_ce02.awmonitorapp.data.response

import com.google.gson.annotations.SerializedName

data class PostCurrentWeatherAndAirResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    var data: Data
) {
    data class Data(
        @SerializedName("id")
        val id: Int,
        @SerializedName("location")
        val location: String,
        @SerializedName("date")
        val date: String,
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
        @SerializedName("temperature")
        val temperature: Double,
        @SerializedName("humidity")
        val humidity: Double,
        @SerializedName("wind_speed")
        val windSpeed: Double,
        @SerializedName("updatedAt")
        val updatedAt: String,
        @SerializedName("createdAt")
        val createdAt: String
    )
}