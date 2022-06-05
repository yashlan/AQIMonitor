package com.c22_ce02.awmonitorapp.ui.activity

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.c22_ce02.awmonitorapp.data.model.Article

class DetailArticleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myWebView = WebView(applicationContext)
        setContentView(myWebView)
        val article = intent.getParcelableExtra<Article>(EXTRA_DATA) as Article
        myWebView.loadUrl(article.url.toString())

        supportActionBar?.title = null
    }
    companion object {
        var EXTRA_DATA = "extra_data"
    }
}