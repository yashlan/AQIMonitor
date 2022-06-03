package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.data.response.ArticleResponse
import com.c22_ce02.awmonitorapp.data.repository.ArticleRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleViewModel(private val repository: ArticleRepository): ViewModel() {

    val listArticle = MutableLiveData<List<ArticleResponse.ItemsItem>?>()
    val errorMessage = MutableLiveData<String?>()

    fun getArticle(Key: String){
        listArticle.postValue(null)
        errorMessage.postValue(null)
        val call = repository.getArticle(Key)
        call.enqueue(object : Callback<ArticleResponse>{
            override fun onResponse(
                call: Call<ArticleResponse>,
                response: Response<ArticleResponse>
            ) {
                if (response.isSuccessful)
                    listArticle.postValue(response.body()?.items)
                else
                    errorMessage.postValue(response.errorBody().toString())
            }

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                errorMessage.postValue(t.localizedMessage?.toString()?:t.message.toString())
            }
        })
    }
}