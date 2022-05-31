package com.c22_ce02.awmonitorapp.data.response

import com.google.gson.annotations.SerializedName

data class FiveDaysOfDailyForecastResponse(
    @SerializedName("Headline")
    val headline: Headline,
    @SerializedName("DailyForecasts")
    val dailyForecasts: List<DailyForecasts>
) {
    data class Headline(
        @SerializedName("EffectiveDate")
        val effectiveDate: String,
        @SerializedName("EffectiveEpochDate")
        val effectiveEpochDate: Int,
        @SerializedName("Severity")
        val severity: Int,
        @SerializedName("Text")
        val text: String,
        @SerializedName("Category")
        val category: String,
        @SerializedName("EndDate")
        val endDate: String,
        @SerializedName("EndEpochDate")
        val endEpochDate: Int,
        @SerializedName("MobileLink")
        val mobileLink: String,
        @SerializedName("Link")
        val link: String
    )

    data class DailyForecasts(
        @SerializedName("Date")
        val date: String,
        @SerializedName("EpochDate")
        val epochDate: Int,
        @SerializedName("Temperature")
        val temperature: Temperature,
    )

    data class Temperature(
        @SerializedName("Minimum")
        val minimum: Minimum,
        @SerializedName("Maximum")
        val maximum: Maximum,
    )

    data class Minimum(
        @SerializedName("Value")
        val value: Double,
        @SerializedName("Unit")
        val unit: String,
        @SerializedName("UnitType")
        val unitType: Int
    )

    data class Maximum(
        @SerializedName("Value")
        val value: Double,
        @SerializedName("Unit")
        val unit: String,
        @SerializedName("UnitType")
        val unitType: Int
    )
}
