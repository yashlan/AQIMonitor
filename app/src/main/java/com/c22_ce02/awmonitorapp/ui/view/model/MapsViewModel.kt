package com.c22_ce02.awmonitorapp.ui.view.model



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.api.ApiConfig
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.data.response.CurrentItem
import com.c22_ce02.awmonitorapp.data.response.MapsCurrentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapsViewModel: ViewModel() {


    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading : LiveData<Boolean> = _showLoading

    private val _currentData = MutableLiveData<List<CurrentItem>>()
    val currentData : LiveData<List<CurrentItem>> = _currentData



    fun getCurrentData(){
        _showLoading.value = true
        val client = ApiConfig.getApiService(BuildConfig.BASE_URL_HEROKU).getCurrent(BuildConfig.API_KEY_ML_DEPLOYMENT)
        client.enqueue(object: Callback<MapsCurrentResponse> {
            override fun onResponse(
                call: Call<MapsCurrentResponse>,
                response: Response<MapsCurrentResponse>
            ) {
                if (response.isSuccessful){
                    _showLoading.value = false
                    _currentData.value = response.body()?.data?.current
                }else {
                    Log.d("MapsFragment", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MapsCurrentResponse>, t: Throwable) {
                _showLoading.value = false
                Log.d("onFailure", "Error: ${t.message}")
            }

        })
    }

}