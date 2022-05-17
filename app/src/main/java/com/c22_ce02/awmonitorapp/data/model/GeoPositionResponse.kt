package com.c22_ce02.awmonitorapp.data.model

import com.google.gson.annotations.SerializedName

data class GeoPositionResponse(
    @SerializedName("Key")
    val locationKey: String,
    @SerializedName("LocalizedName")
    val localizedName: String,
    @SerializedName("AdministrativeArea")
    val administrativeArea: AdministrativeArea,
    @SerializedName("Country")
    val country: Country
) {
    data class AdministrativeArea(
        @SerializedName("LocalizedName")
        val localizedName: String
    )
    data class Country(
        @SerializedName("LocalizedName")
        val localizedName: String
    )
}
