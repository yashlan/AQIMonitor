package com.c22_ce02.awmonitorapp.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import net.time4j.PrettyTime
import net.time4j.android.ApplicationStarter
import net.time4j.format.expert.Iso8601Format
import java.text.SimpleDateFormat
import java.util.*

fun Fragment.initializeTime4A() {
    ApplicationStarter.initialize(requireActivity(), true)
}

fun convertToTimeAgo(date: String): String {
    val moment = Iso8601Format.EXTENDED_DATE_TIME_OFFSET.parse(date)
    return PrettyTime.of(Locale("id")).printRelativeInStdTimezone(moment)
}

fun getCurrentTimeOf(date: String): Long {
    val dateFormat = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.getDefault()
    )
    val pasTime = dateFormat.parse(date)
    val nowTime = Date()
    return nowTime.time - pasTime?.time!!
}