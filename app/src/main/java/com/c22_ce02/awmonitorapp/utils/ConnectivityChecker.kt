@file:Suppress("DEPRECATION")

package com.c22_ce02.awmonitorapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.c22_ce02.awmonitorapp.R

fun isNetworkAvailable(context: Context, showNotAvailableInfo: Boolean): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork
        val activeNetwork = connectivityManager.getNetworkCapabilities(network)
        return when {
            activeNetwork?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> true
            activeNetwork?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> true
            else -> {
                if (showNotAvailableInfo) context.showToast(R.string.msg_no_internet)
                false
            }
        }
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return if (networkInfo.isConnected) {
            true
        } else {
            if (showNotAvailableInfo) context.showToast(R.string.msg_no_internet)
            false
        }
    }
}