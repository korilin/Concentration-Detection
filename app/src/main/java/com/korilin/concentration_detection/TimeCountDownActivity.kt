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

        connectivityManager = getSystemService(ConnectivityManager::class.java).apply {
            registerDefaultNetworkCallback(networkCallback)
        }

        registerReceiver(airplaneReceiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))

        setContentView(viewBinding.root)
        countDownTimer.start()
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // I don't about this
            // https://stackoverflow.com/questions/62577645/android-view-view-systemuivisibility-deprecated-what-is-the-replacement
            window.setDecorFitsSystemWindows(false)
        } else {
            //  older sdk version settings
            // https://developer.android.com/training/system-ui/immersive?hl=zh-cn
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
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