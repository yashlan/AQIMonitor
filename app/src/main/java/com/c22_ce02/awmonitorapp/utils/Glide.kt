package com.c22_ce02.awmonitorapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

fun AppCompatActivity.loadImageViaGlide(uri: Uri?, target: ImageView) {
    Glide
        .with(this)
        .load(uri)
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(target)
}

@SuppressLint("CheckResult")
fun Context.loadImageViaGlide(uri: Uri?, target: ImageView) {
    Glide
        .with(this)
        .load(uri)
        .centerCrop()
        .override(200, 200)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(target)

}

fun AppCompatActivity.loadImageViaGlide(drawable: Int, target: ImageView) {
    Glide
        .with(this)
        .load(ContextCompat.getDrawable(this, drawable))
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(target)
}