package com.musicplayerapplication.helper

object Utils {

    fun getProgressPercentage(currentDuration: Long?, totalDuration: Long?): Int {
        var percentage: Double?
        if (currentDuration == null || totalDuration == null)
            return 0
        val currentSeconds = (currentDuration / 1000).toInt().toLong()
        val totalSeconds = (totalDuration / 1000).toInt().toLong()
        percentage = currentSeconds.toDouble() / totalSeconds * 100
        return percentage.toInt()
    }

    fun progressToTimer(progress: Int, totalDuration: Int): Int {
        var totalDuration = totalDuration
        var currentDuration = 0
        totalDuration = totalDuration / 1000
        currentDuration = (progress.toDouble() / 100 * totalDuration).toInt()
        return currentDuration * 1000
    }
}