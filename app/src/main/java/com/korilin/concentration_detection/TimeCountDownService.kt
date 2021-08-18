package com.korilin.concentration_detection

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat

class TimeCountDownService : Service() {

    private lateinit var countDownTimer: CountDownTimer

    inner class TCDBinder : Binder() {
        fun start(time: Int, handle: Handler) {
            countDownTimer = object : CountDownTimer(time * 1000L, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    Log.d("CountDownTimer", "Thread = ${Thread.currentThread().name}")

                    handle.sendMessage(Message().apply {
                        what = TIME_CHANGE
                        arg1 = (millisUntilFinished / 1000).toInt()
                    })
                }

                override fun onFinish() {
                    handle.sendMessage(Message().apply {
                        what = TIME_FINISH
                    })
                }
            }.start()
        }
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("TimeCountDownService", "Thread = ${Thread.currentThread().name}")

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            TimeCountDownService::class.java.name,
            getText(R.string.app_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)

        val pendingIntent = Intent(this, TimeCountDownActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)
        }
        val notification = NotificationCompat.Builder(this, TimeCountDownService::class.java.name)
            .setContentTitle(getText(R.string.app_name))
            .setContentText("Time Remaining:")
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
    }

    override fun onBind(intent: Intent): IBinder {
        return TCDBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        countDownTimer.cancel()
        return super.onUnbind(intent)
    }
}