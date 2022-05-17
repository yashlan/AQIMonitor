package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.model.FiveDaysOfDailyForecastResponse
import com.c22_ce02.awmonitorapp.data.repository.FiveDaysOfDailyForecastRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FiveDaysOfDailyForecastViewModel(private val repository: FiveDaysOfDailyForecastRepository) : ViewModel() {
    fun get5DaysOfDailyForecasts(
        locationKey: String,
        onSuccess: (FiveDaysOfDailyForecastResponse?) -> Unit,
        onFailed: (String?) -> Unit
    ) {
        val call = repository.get5DaysOfDailyForecasts(locationKey)
        call.enqueue(object : Callback<FiveDaysOfDailyForecastResponse> {
            override fun onResponse(
                call: Call<FiveDaysOfDailyForecastResponse>,
                response: Response<FiveDaysOfDailyForecastResponse>
            ) {
                if(response.isSuccessful) {
                    onSuccess(response.body())
                }else{
                    onFailed(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<FiveDaysOfDailyForecastResponse>, t: Throwable) {
                onFailed(t.message.toString())
            }
        })
    }
}