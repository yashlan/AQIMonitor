package com.c22_ce02.awmonitorapp.notification

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Build
import androidx.core.app.NotificationCompat
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.ui.splash.SplashActivity
import com.c22_ce02.awmonitorapp.utils.isNetworkAvailable
import java.util.*
import kotlin.random.Random


class AirQualityNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(c: Context, i: Intent?) {
        showAirQualityNotification(c)
    }

    private fun getCategoryName(aqi: Int): String? {
        return when (aqi) {
            in 101..150 -> "tidak sehat"
            in 151..300 -> "sangat tidak sehat"
            in 300..Int.MAX_VALUE -> "berbahaya"
            else -> null
        }
    }

    private fun getCurrentAirQuality(onSuccess: (String, String, Int) -> Unit) {
        val aqi = Random.nextInt(1, 350);

        val title = "Indeks Kualitas Udara saat ini sebesar $aqi"
        val message =
            "Kualitas udara di daerahmu saat ini berada di kategori ${
                getCategoryName(aqi)
            }, " + "jadi jangan lupa pakai masker saat keluar rumah ya!"
        onSuccess(title, message, aqi)
    }

    private fun showAirQualityNotification(context: Context) {

        if (!isBackgroundRunning(context)) {
            return
        }

        if (!isNetworkAvailable(context, showNotAvailableInfo = false)) {
            return
        }

        getCurrentAirQuality(onSuccess = { title, message, aqi ->

            if (aqi <= 100)
                return@getCurrentAirQuality

            val notifyIntent = Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                else
                    0
            )
            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(notifyPendingIntent)
                .setSmallIcon(R.drawable.logo_no_background_leaf)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.logo_no_background_leaf
                    )
                )
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title)
                )
                .setAutoCancel(true)

            val notification = mBuilder.build()
            mNotificationManager.notify(ID_REPEATING, notification)
        })
    }

    fun createChannel(context: Context) {
        if (!isAlarmAlreadySet(context) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.enableLights(true)
            channel.enableVibration(true)
            channel.lockscreenVisibility = Context.MODE_PRIVATE

            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }
    }

    fun setRepeatingNotification(context: Context) {

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val intent = Intent(context, AirQualityNotificationReceiver::class.java)

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                ID_REPEATING,
                intent,
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> PendingIntent.FLAG_IMMUTABLE
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> PendingIntent.FLAG_MUTABLE
                    else -> 0
                }
            )

        alarmManager?.cancel(pendingIntent)

        val calendar = Calendar.getInstance()
        alarmManager?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis + REPEAT_TIME,
            REPEAT_TIME,
            pendingIntent
        )
    }

    private fun isAlarmAlreadySet(context: Context): Boolean {
        val intent = Intent(context, AirQualityNotificationReceiver::class.java)
        val requestCode = ID_REPEATING

        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> PendingIntent.FLAG_IMMUTABLE
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> PendingIntent.FLAG_MUTABLE
                else -> 0
            }
        ) != null
    }

    private fun isBackgroundRunning(context: Context): Boolean {
        val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = am.runningAppProcesses
        for (processInfo in runningProcesses) {
            if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == context.packageName) {
                        return false
                    }
                }
            }
        }
        return true
    }

    companion object {
        private const val ID_REPEATING = 101
        private const val CHANNEL_ID = "Channel_1"
        private const val CHANNEL_NAME = "Cek Kualitas Udara"
        private const val PERIOD: Long = 500
        private const val REPEAT_TIME: Long = 60 * (60 * 1000)
    }
}