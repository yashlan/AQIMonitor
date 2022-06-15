package com.c22_ce02.awmonitorapp.utils

fun convertPM10ToISPU(pm10: Double): Int {
    val result: Int
    val k: Double
    val t: Double
    val r: Double
    when {
        pm10 > 420.0 -> {
            k = pm10 - 420
            t = (500 - 420).toDouble()
            r = (400 - 300).toDouble()
            result = ((r * k / t) + 300).toInt()
        }
        pm10 in 350.0..419.0 -> {
            k = pm10 - 350
            t = (420 - 350).toDouble()
            r = (300 - 200).toDouble()
            result = ((r * k / t) + 200).toInt()
        }
        pm10 in 150.0..349.0 -> {
            k = pm10 - 150
            t = (350 - 150).toDouble()
            r = (200 - 100).toDouble()
            result = ((r * k / t) + 100).toInt()
        }
        else -> {
            k = pm10 - 50
            t = (150 - 50).toDouble()
            r = (100 - 50).toDouble()
            result = ((r * k / t) + 50).toInt()
        }
    }
    return result
}

fun convertPM25ToISPU(pm25: Double): Int {
    val result: Int
    val k: Double
    val t: Double
    val r: Double
    when {
        pm25 > 250.4 -> {
            k = pm25 - 250.4
            t = 500 - 250.4
            r = (400 - 300).toDouble()
            result = ((r * k / t) + 300).toInt()
        }
        pm25 in 150.4..249.4 -> {
            k = pm25 - 150.4
            t = 250.4 - 150.4
            r = (300 - 200).toDouble()
            result = ((r * k / t) + 200).toInt()
        }
        pm25 in 55.4..149.4 -> {
            k = pm25 - 55.4
            t = 150.4 - 55.4
            r = (200 - 100).toDouble()
            result = ((r * k / t) + 100).toInt()
        }
        else -> {
            k = pm25 - 15.5
            t = 55.4 - 15.5
            r = (100 - 50).toDouble()
            result = ((r * k / t) + 50).toInt()
        }
    }
    return result
}