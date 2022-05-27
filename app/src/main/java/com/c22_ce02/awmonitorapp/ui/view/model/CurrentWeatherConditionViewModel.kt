package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.response.CurrentWeatherConditionResponse
import com.c22_ce02.awmonitorapp.data.repository.CurrentWeatherConditionRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrentWeatherConditionViewModel(private val repository: CurrentWeatherConditionRepository) :
    ViewModel() {

    val currentWeather = MutableLiveData<List<CurrentWeatherConditionResponse.Data>?>()
    val errorMessage = MutableLiveData<String?>()

    fun getCurrentWeatherCondition(
        lat: Double,
        lon: Double,
        apiKey: String
    ) {
        currentWeather.postValue(null)
        errorMessage.postValue(null)
        val call = repository.getCurrentWeatherCondition(lat, lon, apiKey)
        call.enqueue(object : Callback<CurrentWeatherConditionResponse> {
            override fun onResponse(
                call: Call<CurrentWeatherConditionResponse>,
                response: Response<CurrentWeatherConditionResponse>
            ) {
                if (response.isSuccessful) {
                    currentWeather.postValue(response.body()?.data)
                } else {
                    errorMessage.postValue(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<CurrentWeatherConditionResponse>, t: Throwable) {
                errorMessage.postValue(t.localizedMessage?.toString() ?: t.message.toString())
            }
        })
    }
}