package com.korilin.concentration_detection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.*
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
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
                    setTextColor(getColor(R.color.success))
                }
                AIRPLANE_CLOSED -> viewBinding.flyModeStatus.apply {
                    text = getString(R.string.network_closed)
                    setTextColor(getColor(R.color.error))
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

    //
    /**
     * A [Intent.ACTION_AIRPLANE_MODE_CHANGED] Receiver
     *
     * Account to my test, this receiver will receive a message when it was just registered.
     * So there use contentResolver to get the current state of the Airplane in [onCreate] fun.
     */
    private val airplaneReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            println(intent.action)
            if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                if (intent.getBooleanExtra("state", false)) {
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

        val finishDialog = AlertDialog.Builder(this).apply {
            setTitle(R.string.finish_dialog_title)
            setPositiveButton(getString(R.string.dialog_confirm), null)
        }.create()

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

            /**
             * when time count down finish
             *
             * 1. stats the current data and adds it locally
             * 2. use dialog to tell user the concentration is complete
             *
             * optional:
             * - finish music
             * - vibration
             */
            override fun onFinish() {
                finishDialog.show()
            }
        }

        /**
         * Because [airplaneReceiver] could not receive the airplane state at the beginning of the registration,
         * So here use contentResolver to get the current state of the Airplane.
         */
        val airplaneModeStatus =
            Settings.Global.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0)

        viewBinding.flyModeStatus.apply {
            if (airplaneModeStatus == 0) {
                text = getString(R.string.airplane_mode_opened)
                setTextColor(getColor(R.color.error))
            } else {
                text = getString(R.string.airplane_mode_closed)
                setTextColor(getColor(R.color.success))
            }
        }

        // 在注册网络变化前，先设置为关闭，以解决网络在关闭的情况下启动 Activity 不会收到网络 callback 的问题
        viewBinding.networkStatus.apply {
            text = getString(R.string.network_closed)
            setTextColor(getColor(R.color.success))
        }

        connectivityManager = getSystemService(ConnectivityManager::class.java).apply {
            registerDefaultNetworkCallback(networkCallback)
        }

        registerReceiver(airplaneReceiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))

        setContentView(viewBinding.root)
        countDownTimer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
        unregisterReceiver(airplaneReceiver)
    }

    companion object {
        /**
         * Use this function to start [TimeCountDownActivity] to ensure that it has the correct params.
         */
        fun actionStart(context: Context, time: Int) {
            context.startActivity(Intent(context, TimeCountDownActivity::class.java).apply {
                putExtra(PARAM, time)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
        }
    }
}