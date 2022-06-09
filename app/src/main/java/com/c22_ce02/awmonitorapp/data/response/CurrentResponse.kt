package com.c22_ce02.awmonitorapp.data.response

import com.google.gson.annotations.SerializedName

data class CurrentResponse(

	@field:SerializedName("data")
	val data: Data
)

data class Data(

	@field:SerializedName("current")
	val current: List<CurrentItem>
)

data class CurrentItem(

	@field:SerializedName("no2")
	val no2: Double,

	@field:SerializedName("o3")
	val o3: Double,

	@field:SerializedName("pm25")
	val pm25: Double,

	@field:SerializedName("city")
	val city: String,

	@field:SerializedName("so2")
	val so2: Double,

	@field:SerializedName("aqi")
	val aqi: Int,

	@field:SerializedName("pm10")
	val pm10: Double,

	@field:SerializedName("lon")
	val lon: Double,

	@field:SerializedName("co")
	val co: Double,

	@field:SerializedName("lat")
	val lat: Double
)
