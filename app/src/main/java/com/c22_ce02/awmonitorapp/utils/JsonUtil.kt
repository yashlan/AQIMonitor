package com.c22_ce02.awmonitorapp.utils

import android.content.Context
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.data.preference.MapsPreference
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

private const val fileName = "maps_response.json"

fun readResponse(context: Context): String? {
    return try {
        val path = context.getExternalFilesDir(null)?.absolutePath
        val file = File(path, fileName)
        val fis = FileInputStream(file)
        val isr = InputStreamReader(fis)
        val bufferedReader = BufferedReader(isr)
        val sb = StringBuilder()
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            sb.append(line)
        }
        bufferedReader.close()
        sb.toString()
    } catch (fileNotFound: FileNotFoundException) {
        null
    } catch (ioException: IOException) {
        null
    }
}

fun saveResponse(context: Context, jsonString: String?): Boolean {
    return try {
        val path = context.getExternalFilesDir(null)?.absolutePath
        val file = File(path, fileName)
        val output = BufferedWriter(FileWriter(file))
        output.write(jsonString)
        output.close()
        true
    } catch (fileNotFound: FileNotFoundException) {
        false
    } catch (ioException: IOException) {
        false
    }
}

fun isFilePresent(context: Context): Boolean {
    val path = context.getExternalFilesDir(null)?.absolutePath
    val file = File(path, fileName)

    val mapsPref = MapsPreference(context)
    val diff = Date().time - mapsPref.getLastUpdateMaps()
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    val canUpdateMaps = hours >= 1

    if (canUpdateMaps) {
        file.delete()
        return false
    }
    return file.exists()
}