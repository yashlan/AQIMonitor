package com.c22_ce02.awmonitorapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AirQualityForecastByHour(
    val hour: String,
    val iconAQISrc: Int,
    val aqi: Int,
    val pm10: Int,
    val pm25: Int,
    val o3: Int,
    val so2: Int,
    val no2: Int,
    val co: Int
) : Parcelable