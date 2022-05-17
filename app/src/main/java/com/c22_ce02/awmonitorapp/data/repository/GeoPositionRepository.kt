package com.c22_ce02.awmonitorapp.data.repository

import com.c22_ce02.awmonitorapp.api.ApiService

class GeoPositionRepository(private val apiService: ApiService) {
    fun getInformationUserByGeoPosition(latLong: String) =
        apiService.getInformationByGeoPosition(latLong)
}