package com.example.skullheadradio.tools

import android.content.Context
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class SongProgressbarHandler(){

    private lateinit var progressBar: SeekBar
    private lateinit var startTime: String
    private lateinit var endTime: String
    private var timer: Timer? = null



    fun generateProgressView(bar: SeekBar,remainTimeView: TextView, startTimeView: TextView, startTime: String, endTime: String, context: Context) {
        this.startTime = startTime
        this.endTime = endTime
        progressBar = bar
        startUpdatingProgress(remainTimeView, startTimeView)
    }

    private fun startUpdatingProgress(remainTimeView: TextView, startTimeView: TextView) {
        val handler = Handler()
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                handler.post {
                    updateProgress(remainTimeView, startTimeView)
                }
            }
        }, 0, 1000) // Update alle 1 Sekunde
    }

    private fun updateProgress(remainTimeView: TextView, startTimeView: TextView) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.getDefault())
        val currentTime = System.currentTimeMillis()

        val startDate = dateFormat.parse(startTime)?.time ?: return
        val endDate = dateFormat.parse(endTime)?.time ?: return

        val totalTime = endDate - startDate
        val elapsedTime = currentTime - startDate

        val totalTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(totalTime)
        val totalTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(totalTime) % 60

        val elapsedTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime)
        val elapsedTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60

        var remainingTimeMinutes = totalTimeMinutes - elapsedTimeMinutes
        var remainingTimeSeconds = totalTimeSeconds - elapsedTimeSeconds

        if(remainingTimeSeconds < 0){
            remainingTimeMinutes -= 1
            remainingTimeSeconds = 60 - remainingTimeSeconds
        }

        val remainingTimeFormatted = String.format("%02d:%02d", remainingTimeMinutes, remainingTimeSeconds)
        val elapsedTimeFormatted = String.format("%02d:%02d", elapsedTimeMinutes, elapsedTimeSeconds)

        remainTimeView.text = remainingTimeFormatted
        startTimeView.text = elapsedTimeFormatted


        if (elapsedTime >= 0 && elapsedTime <= totalTime) {
            val progressPercentage = (elapsedTime.toFloat() / totalTime.toFloat() * 100).toInt()
            progressBar.progress = progressPercentage
        } else {
            // song ends or hasn't already started
            progressBar.progress = 0
            timer?.cancel()
        }
    }
}