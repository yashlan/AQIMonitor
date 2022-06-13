package com.c22_ce02.awmonitorapp.utils

import android.content.Context
import android.util.Log
import com.c22_ce02.awmonitorapp.BuildConfig
import java.io.*
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
    val time = Calendar.getInstance()
    time.add(Calendar.HOUR, -1)
    val lastModified = Date(file.lastModified())
    if (lastModified.before(time.time)) {
        file.delete()
        if (BuildConfig.DEBUG) {
            context.showToastInThread("file sudah expired, donwload ulang dulu")
        }
        return false
    }
    return file.exists()
}