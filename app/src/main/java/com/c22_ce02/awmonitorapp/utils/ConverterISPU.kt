package com.c22_ce02.awmonitorapp.utils

private fun convertPM10ToISPU(pm10: Double): Int {
    val result: Int
    val k: Double
    val t: Double
    val r: Double
    when {
        pm10.toInt() > 420 -> {
            k = pm10 - 420
            t = (500 - 420).toDouble()
            r = (400 - 300).toDouble()
            result = ((r * k / t) + 300).toInt()
        }
        pm10.toInt() in 351..420 -> {
            k = pm10 - 350
            t = (420 - 350).toDouble()
            r = (300 - 200).toDouble()
            result = ((r * k / t) + 200).toInt()
        }
        pm10.toInt() in 150..350 -> {
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

private fun convertPM25ToISPU(pm25: Double): Int {
    val result: Int
    val k: Double
    val t: Double
    val r: Double
    when {
        pm25.toInt() > 250 -> {
            k = pm25 - 250.4
            t = 500 - 250.4
            r = (400 - 300).toDouble()
            result = ((r * k / t) + 300).toInt()
        }
        pm25.toInt() in 150..250 -> {
            k = pm25 - 150.4
            t = 250.4 - 150.4
            r = (300 - 200).toDouble()
            result = ((r * k / t) + 200).toInt()
        }
        pm25.toInt() in 55..149 -> {
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

private fun convertO3ToISPU(o3: Double): Int {
    val result: Int
    val k: Double
    val t: Double
    val r: Double
    when {
        o3.toInt() > 800 -> {
            k = (o3 - 800)
            t = ((1000 - 800).toDouble())
            r = ((400 - 300).toDouble())
            result = ((r * k / t) + 300).toInt()
        }
        o3.toInt() in 401..800 -> {
            k = (o3 - 400)
            t = ((800 - 400).toDouble())
            r = ((300 - 200).toDouble())
            result = ((r * k / t) + 200).toInt()
        }
        o3.toInt() in 236..400 -> {
            k = (o3 - 235)
            t = ((400 - 235).toDouble())
            r = ((200 - 100).toDouble())
            result = ((r * k / t) + 100).toInt()
        }
        else -> {
            k = (o3 - 120)
            t = ((235 - 120).toDouble())
            r = ((100 - 50).toDouble())
            result = ((r * k / t) + 50).toInt()
        }
    }
    return result
}

private fun convertSO2toISPU(so2: Double): Int {
    val result: Int
    val k: Double
    val t: Double
    val r: Double
    when {
        so2.toInt() > 800 -> {
            k = so2 - 800
            t = ((1200 - 800).toDouble())
            r = ((400 - 300).toDouble())
            result = ((r * k / t) + 300).toInt()
        }
        so2.toInt() in 401..800 -> {
            k = (so2 - 400)
            t = ((800 - 400).toDouble())
            r = ((300 - 200).toDouble())
            result = ((r * k / t) + 200).toInt()
        }
        so2.toInt() in 180..400 -> {
            k = (so2 - 180)
            t = ((400 - 180).toDouble())
            r = ((200 - 100).toDouble())
            result = ((r * k / t) + 100).toInt()
        }
        else -> {
            k = (so2 - 52)
            t = ((180 - 52).toDouble())
            r = ((100 - 50).toDouble())
            result = ((r * k / t) + 50).toInt()
        }
    }
    return result
}

private fun convertNO2toISPU(no2: Double): Int {
    val result: Int
    val k: Double
    val t: Double
    val r: Double
    when {
        no2.toInt() > 2260 -> {
            k = no2 - 2260
            t = ((3000 - 2260).toDouble())
            r = ((400 - 300).toDouble())
            result = ((r * k / t) + 300).toInt()
        }
        no2.toInt() in 1131..2260 -> {
            k = (no2 - 1130)
            t = ((2260 - 1130).toDouble())
            r = ((300 - 200).toDouble())
            result = ((r * k / t) + 200).toInt()
        }
        no2.toInt() in 200..1130 -> {
            k = (no2 - 200)
            t = ((1130 - 200).toDouble())
            r = ((200 - 100).toDouble())
            result = ((r * k / t) + 100).toInt()
        }
        else -> {
            k = (no2 - 80)
            t = ((235 - 80).toDouble())
            r = ((100 - 50).toDouble())
            result = ((r * k / t) + 50).toInt()
        }
    }
    return result
}

private fun convertCOtoISPU(co: Double): Int {
    val result: Int
    val k: Double
    val t: Double
    val r: Double
    when {
        co.toInt() > 30000 -> {
            k = (co - 30000)
            t = ((45000 - 30000).toDouble())
            r = ((400 - 300).toDouble())
            result = ((r * k / t) + 300).toInt()
        }
        co.toInt() in 15001..30000 -> {
            k = (co - 15000)
            t = ((30000 - 15000).toDouble())
            r = ((300 - 200).toDouble())
            result = ((r * k / t) + 200).toInt()
        }
        co.toInt() in 8000..15000 -> {
            k = (co - 8000)
            t = ((15000 - 8000).toDouble())
            r = ((200 - 100).toDouble())
            result = ((r * k / t) + 100).toInt()
        }
        else -> {
            k = (co - 4000)
            t = ((8000 - 4000).toDouble())
            r = ((100 - 50).toDouble())
            result = ((r * k / t) + 50).toInt()
        }
    }
    return result
}

fun getCurrentAQIISPU(
    pm10: Double,
    pm25: Double,
    o3: Double,
    so2: Double,
    no2: Double,
    co: Double,
): Int {
    var list: List<Int> = listOf()
    try {
        list = listOf(
            convertPM10ToISPU(pm10),
            convertPM25ToISPU(pm25),
            convertO3ToISPU(o3),
            convertSO2toISPU(so2),
            convertNO2toISPU(no2),
            convertCOtoISPU(co)
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return list.maxByOrNull { it } ?: 0
}