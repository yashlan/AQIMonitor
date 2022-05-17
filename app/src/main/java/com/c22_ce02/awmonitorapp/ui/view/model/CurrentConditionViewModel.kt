package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.model.CurrentConditionResponse
import com.c22_ce02.awmonitorapp.data.repository.CurrentConditionRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrentConditionViewModel(private val repository: CurrentConditionRepository) : ViewModel() {
    fun getCurrentCondition(
        locationKey: String,
        onSuccess: (List<CurrentConditionResponse>?) -> Unit,
        onFailed: (String?) -> Unit
    ) {
        val call = repository.getCurrentCondition(locationKey)
        call.enqueue(object : Callback<List<CurrentConditionResponse>> {
            override fun onResponse(
                call: Call<List<CurrentConditionResponse>>,
                response: Response<List<CurrentConditionResponse>>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body())
                } else {
                    onFailed(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<List<CurrentConditionResponse>>, t: Throwable) {
                onFailed(t.message.toString())
            }
        })
    }
}