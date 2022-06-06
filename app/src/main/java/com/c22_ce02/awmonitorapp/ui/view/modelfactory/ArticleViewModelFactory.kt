package com.c22_ce02.awmonitorapp.ui.view.modelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c22_ce02.awmonitorapp.di.Injection
import com.c22_ce02.awmonitorapp.ui.view.model.ArticleViewModel
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class ArticleViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ArticleViewModel::class.java))
            ArticleViewModel(Injection.provideArticleRepository()) as T
        else
            throw IllegalArgumentException("ViewModel of ${modelClass.simpleName} Not Found")
    }
}