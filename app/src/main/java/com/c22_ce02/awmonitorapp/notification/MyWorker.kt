package com.c22_ce02.awmonitorapp.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.os.StrictMode
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.utils.showToast
import com.google.android.gms.location.LocationServices
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.SyncHttpClient
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class MyWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams), LocationListener {

    private lateinit var locationManager: LocationManager

    //private var resultStatus: Result? = null
    private var isLocationChanged = false
    private var newLat = 0.0
    private var newLon = 0.0

    override fun doWork(): Result {
        return getCurrentAirQuality()
    }

    private fun getLocation(onGetLocation: (Double, Double) -> Unit) {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)
        if (ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        onGetLocation(location.latitude, location.longitude)
                    } else {
                        requestLocation()
                        val refreshLocationTimer = Timer()
                        refreshLocationTimer.scheduleAtFixedRate(object : TimerTask() {
                            override fun run() {
                                if (isLocationChanged) {
                                    onGetLocation(newLat, newLon)
                                    cancel()
                                }
                            }
                        }, 0, PERIOD)
                    }
                }
        }
    }

    private fun requestLocation() {
        locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            } else {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000L,
                    1000f,
                    this
                )
            }
        }
    }

    override fun onLocationChanged(l: Location) {
        newLat = l.latitude
        newLon = l.longitude
        isLocationChanged = true
        locationManager.removeUpdates(this)
    }

    private fun setPolicy() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    private fun getCurrentAirQuality(): Result {
        var resultStatus: Result = Result.retry()
        getLocation(onGetLocation = { lat, lon ->
            if (Looper.myLooper() == null) {
                Looper.prepare()
            }
            setPolicy()
            val client = SyncHttpClient()
            val url =
                "${BuildConfig.BASE_URL_WEATHERBIT}current/airquality?lat=$lat&lon=$lon&key=${BuildConfig.API_KEY_WEATHERBIT}"
            client.post(url, object : AsyncHttpResponseHandler() {
                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<Header?>?,
                    responseBody: ByteArray
                ) {
                    val result = String(responseBody)
                    resultStatus = try {
                        val responseObject = JSONObject(result)
                        val data = responseObject.getJSONArray("data")
                        val aqi = data.getJSONObject(0).getString("aqi")
                        val title = "Kualitas Udara Saat Ini : $aqi"
                        val message = "kualitas udara di tempatmu saat ini sedang tidak sehat, " +
                                "jangan lupa pakai masker ketika keluar rumah"
                        showNotification(title, message)
                        Result.success()
                    } catch (e: Exception) {
                        Result.failure()
                    }
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header?>?,
                    responseBody: ByteArray?,
                    error: Throwable
                ) {
                    resultStatus = Result.failure()
                }
            })
        })
        return resultStatus
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notification.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "monitor channel"
        private const val PERIOD: Long = 500
    }
}