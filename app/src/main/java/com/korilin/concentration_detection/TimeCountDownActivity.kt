package com.korilin.concentration_detection

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import com.korilin.concentration_detection.databinding.ActivityTimeCountDownBinding

const val PARAM = "time"

class TimeCountDownActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityTimeCountDownBinding
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val time = intent.getIntExtra(PARAM, 0)
        viewBinding = ActivityTimeCountDownBinding.inflate(layoutInflater)

        val airplaneModeStatus =
            Settings.Global.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0)


        viewBinding.flyModeStatus.apply {
            if (airplaneModeStatus == 0) {
                text = getString(R.string.closed)
                setTextColor(getColor(R.color.error))
            } else {
                text = getString(R.string.opened)
                setTextColor(getColor(R.color.success))
            }
        }

        countDownTimer = object : CountDownTimer(time * 1000L, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                var content = ""
                (millisUntilFinished / 1000).also {
                    content += "${it / 3600} : ${it % 3600 / 60} : ${it % 3600 % 60}"
                }
                viewBinding.timeCountDownText.text = content
            }

            override fun onFinish() {
            }
        }
        setContentView(viewBinding.root)
    }

    override fun onResume() {
        countDownTimer.start()
        super.onResume()
    }

    companion object {
        fun actionStart(context: Context, time: Int) {
            context.startActivity(Intent(context, TimeCountDownActivity::class.java).apply {
                putExtra(PARAM, time)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
        }
    }
}