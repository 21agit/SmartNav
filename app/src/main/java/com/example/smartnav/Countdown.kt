package com.example.smartnav

import android.os.CountDownTimer
import android.widget.TextView

class Countdown {

    private var countDownTimer: CountDownTimer? = null
    var isCountingDown = false

    fun startCountdown(countdownText: TextView, onCountdownFinish: () -> Unit) {
        countDownTimer = object : CountDownTimer(5000, 1000) { // Countdown in Millisekunden
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60)
                val seconds = (millisUntilFinished % (1000 * 60)) / 1000
                countdownText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }

            override fun onFinish() {
                countdownText.text = "00:00:00"
                isCountingDown = false
                onCountdownFinish()
            }
        }.start()
        isCountingDown = true
    }
}