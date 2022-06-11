package com.c22_ce02.awmonitorapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherHistoryAndForecastByHour(
    val windSpeed: Int,
    val humidity: Int,
    val temperature: Int
) : Parcelable