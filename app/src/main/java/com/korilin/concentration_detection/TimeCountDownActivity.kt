package com.korilin.concentration_detection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.korilin.concentration_detection.databinding.ActivityTimeCountDownBinding

const val PARAM = "time"

const val NETWORK_LOST = 10
const val NETWORK_CONNECT = 11
const val AIRPLANE_CLOSED = 20
const val AIRPLANE_OPENED = 21

class TimeCountDownActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityTimeCountDownBinding
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var connectivityManager: ConnectivityManager

    // 用于修改 UI
    private val uiHandel = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                NETWORK_CONNECT -> viewBinding.networkStatus.apply {
                    text = getString(R.string.network_connected)
                    setTextColor(getColor(R.color.error))
                }
                NETWORK_LOST -> viewBinding.networkStatus.apply {
                    text = getString(R.string.network_closed)
                    setTextColor(getColor(R.color.success))
                }
                AIRPLANE_OPENED -> viewBinding.flyModeStatus.apply {
                    text = getString(R.string.airplane_mode_opened)
                    setTextColor(getColor(R.color.error))
                }
                AIRPLANE_CLOSED -> viewBinding.flyModeStatus.apply {
                    text = getString(R.string.network_closed)
                    setTextColor(getColor(R.color.success))
                }
            }
        }
    }

    // network listener
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            uiHandel.sendMessage(Message().apply { what = NETWORK_CONNECT })
        }

        override fun onLost(network: Network) {
            uiHandel.sendMessage(Message().apply { what = NETWORK_LOST })
        }
    }

    // A Airplane Receiver
    private val airplaneReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                if (intent.getBooleanExtra(intent.action, false)) {
                    uiHandel.sendMessage(Message().apply { what = AIRPLANE_OPENED })
                } else {
                    uiHandel.sendMessage(Message().apply { what = AIRPLANE_CLOSED })
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val time = intent.getIntExtra(PARAM, 0)
        viewBinding = ActivityTimeCountDownBinding.inflate(layoutInflater)

        countDownTimer = object : CountDownTimer(time * 1000L, 1000) {

            fun String.toDoubleDigit() = if (length < 2) "0$this" else this

            override fun onTick(millisUntilFinished: Long) {
                viewBinding.timeCountDownText.text = (millisUntilFinished / 1000).let {
                    val h = "${it / 3600}".toDoubleDigit()
                    val m = "${it % 3600 / 60}".toDoubleDigit()
                    val s = "${it % 3600 % 60}".toDoubleDigit()
                    "$h : $m : $s"
                }
            }

            override fun onFinish() {

            }
        }

        connectivityManager = getSystemService(ConnectivityManager::class.java).apply {
            registerDefaultNetworkCallback(networkCallback)
        }

        registerReceiver(airplaneReceiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))

        // 关掉 Action Bar
        actionBar?.hide()
        setContentView(viewBinding.root)
        countDownTimer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
        unregisterReceiver(airplaneReceiver)
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