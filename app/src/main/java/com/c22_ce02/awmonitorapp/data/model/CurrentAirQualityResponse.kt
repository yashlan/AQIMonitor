package com.c22_ce02.awmonitorapp.data.model

import com.google.gson.annotations.SerializedName

data class CurrentAirQualityResponse(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("city_name")
    val cityName: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("state_code")
    val stateCode: String,
    @SerializedName("data")
    val data: List<Data>
) {
    data class Data(
        @SerializedName("aqi")
        val aqi: Int,
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
        @SerializedName("pollen_level_tree")
        val pollenLevelTree: Int,
        @SerializedName("pollen_level_grass")
        val pollenLevelGrass: Int,
        @SerializedName("pollen_level_weed")
        val pollenLevelWeed: Int,
        @SerializedName("mold_level")
        val moldLevel: Int,
        @SerializedName("predominant_pollen_type")
        val predominantPollenType: String
    )
}