package com.c22_ce02.awmonitorapp.asset

import com.google.gson.annotations.SerializedName

data class DummyResponse(

	@field:SerializedName("DummyResponse")
	val dummyResponse: List<DummyResponseItem>
)

data class DummyResponseItem(

	@field:SerializedName("country")
	var country: String? = null,

	@field:SerializedName("capital")
	var capital: String? = null,

	@field:SerializedName("aqi")
	var aqi: String? = null,

	@field:SerializedName("lng")
	var lng: String? = null,

	@field:SerializedName("city")
	var city: String? = null,

	@field:SerializedName("admin_name")
	var adminName: String? = null,

	@field:SerializedName("population_proper")
	var populationProper: String? = null,

	@field:SerializedName("iso2")
	var iso2: String? = null,

	@field:SerializedName("lat")
	var lat: String? = null,

	@field:SerializedName("population")
	var population: String? = null
)
