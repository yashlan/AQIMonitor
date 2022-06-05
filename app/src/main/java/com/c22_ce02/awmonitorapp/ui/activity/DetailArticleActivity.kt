package com.c22_ce02.awmonitorapp.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.c22_ce02.awmonitorapp.ui.fragment.ArticleFragment
import com.c22_ce02.awmonitorapp.utils.forcePortraitScreenOrientation
import com.c22_ce02.awmonitorapp.utils.setFullscreen


class DetailArticleActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        forcePortraitScreenOrientation()
        setFullscreen()
        super.onCreate(savedInstanceState)

        val myWebView = WebView(applicationContext)
        myWebView.settings.javaScriptEnabled = true
        setContentView(myWebView)
        val url = intent?.getStringExtra(ArticleFragment.URL_EXTRA)
        myWebView.loadUrl(url.toString())
    }
}