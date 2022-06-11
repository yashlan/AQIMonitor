package com.c22_ce02.awmonitorapp.utils

import android.content.Context
import java.io.IOException
import java.io.InputStream

fun Context.loadJSONFromAsset(fileName: String): String? {
    val json = try {
        val inputStream: InputStream = assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer)
    } catch (ex: IOException) {
        ex.printStackTrace()
        return null
    }
    return json
}