package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.model.CurrentAirQualityResponse
import com.c22_ce02.awmonitorapp.data.repository.CurrentAirQualityRepository
import retrofit2.*

class CurrentAirQualityViewModel(private val repository: CurrentAirQualityRepository) :
    ViewModel() {

    val currentAirQuality = MutableLiveData<CurrentAirQualityResponse?>()
    val errorMessage = MutableLiveData<String>()

    fun getCurrentAirQuality(
        lat: Double,
        lon: Double,
        apikey: String
    ) {
        currentAirQuality.postValue(null)
        errorMessage.postValue(null)
        val call = repository.getCurrentAirQuality(lat, lon, apikey)
        call.enqueue(object : Callback<CurrentAirQualityResponse> {
            override fun onResponse(
                call: Call<CurrentAirQualityResponse>,
                response: Response<CurrentAirQualityResponse>
            ) {
                if (response.isSuccessful) {
                    currentAirQuality.postValue(response.body())
                } else {
                    errorMessage.postValue(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<CurrentAirQualityResponse>, t: Throwable) {
                errorMessage.postValue(t.localizedMessage?.toString() ?: t.message.toString())
            }
        })
    }
}