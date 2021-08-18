package com.korilin.concentration_detection

import android.content.*
import android.net.ConnectivityManager
import android.net.Network
import android.os.*
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.korilin.concentration_detection.databinding.ActivityTimeCountDownBinding
import com.korilin.concentration_detection.sqlite.ConcentrationSQLiteHelper
import com.korilin.concentration_detection.viewmodel.MainViewModel

const val INTENT_PARAM = "time"

const val BUNDLE_PARAM = "unLockCount"
const val BUNDLE_TIME_UNTIL = "timeUntil"

const val NETWORK_LOST = 10
const val NETWORK_CONNECT = 11
const val AIRPLANE_CLOSED = 20
const val AIRPLANE_OPENED = 21
const val SCREEN_UNLOCK = 30

const val TIME_CHANGE = 40
const val TIME_FINISH = 41

class TimeCountDownActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityTimeCountDownBinding
    private lateinit var connectivityManager: ConnectivityManager
    private val viewModel: MainViewModel by viewModels()

    private val dbHelper = ConcentrationSQLiteHelper(this)
    private var time = 0

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimeCountDownService.TCDBinder
            binder.start(viewModel.timeUntil, uiHandler)
            Log.d("onServiceConnected", "Thread = ${Thread.currentThread().name}")
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    // 用于修改 UI
    private val uiHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            Log.d("uiHandler", "Thread = ${Thread.currentThread().name}")
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
                SCREEN_UNLOCK -> viewBinding.unLockCount.text = viewModel.unLockCount.toString()
                TIME_CHANGE -> {
                    viewModel.timeUntil = msg.arg1
                    viewBinding.timeCountDownText.text = msg.arg1.let {
                        val h = "${it / 3600}".toDoubleDigit()
                        val m = "${it % 3600 / 60}".toDoubleDigit()
                        val s = "${it % 3600 % 60}".toDoubleDigit()
                        "$h : $m : $s"
                    }
                }
                TIME_FINISH -> if (viewModel.timeUntil == 0) {
                    dbHelper.insertRecord(time, viewModel.unLockCount)
                    AlertDialog.Builder(this@TimeCountDownActivity).apply {
                        setTitle(R.string.finish_dialog_title)
                        setMessage(
                            """
                                ${getString(R.string.dialog_message1)} ${getTime(time)}
                                
                                ${getString(R.string.dialog_message2)} : ${viewModel.unLockCount}
                            """.trimIndent()
                        )
                        setPositiveButton(
                            getString(R.string.dialog_confirm)
                        ) { dialog, _ ->
                            dialog.dismiss()
                            finish()
                        }
                    }.create().show()
                }
            }
        }
    }

    // network listener
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            uiHandler.sendMessage(Message().apply { what = NETWORK_CONNECT })
        }

        override fun onLost(network: Network) {
            uiHandler.sendMessage(Message().apply { what = NETWORK_LOST })
        }
    }

    /**
     * A [Intent.ACTION_AIRPLANE_MODE_CHANGED] Receiver
     *
     * Account to my test, this receiver will receive a message when it was just registered.
     * So there use contentResolver to get the current state of the Airplane in [onCreate] fun.
     */
    private val airplaneReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                if (intent.getBooleanExtra("state", false)) {
                    uiHandler.sendMessage(Message().apply { what = AIRPLANE_OPENED })
                } else {
                    uiHandler.sendMessage(Message().apply { what = AIRPLANE_CLOSED })
                }
            }
        }
    }

    /**
     * when unlock the screen:
     * - unlock count + 1
     */
    private val userPresentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_USER_PRESENT) {
                viewModel.unLockCount += 1
                Toast.makeText(
                    context,
                    getString(R.string.unlock_notify_toast) + viewModel.unLockCount.toString(),
                    Toast.LENGTH_LONG
                ).show()
                uiHandler.sendMessage(Message().apply { what = SCREEN_UNLOCK })
            }
        }
    }

    private fun getTime(time: Int): String {
        var content = ""
        val h = time / 3600
        val m = time % 3600 / 60
        if (h != 0) content += h.toString() + getString(R.string.time_hour)
        if (m != 0) content += m.toString() + getString(R.string.time_min)
        return content
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TimeCountDownActivity", "Thread = ${Thread.currentThread().name}")

        viewBinding = ActivityTimeCountDownBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        time = intent.getIntExtra(INTENT_PARAM, 0)
        if (viewModel.timeUntil == 0) viewModel.timeUntil = time

        viewBinding.unLockCount.text = viewModel.unLockCount.toString()

        /**
         * Because [airplaneReceiver] could not receive the airplane state at the beginning of the registration,
         * So here use contentResolver to get the current state of the Airplane.
         */
        val airplaneModeStatus =
            Settings.Global.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0)

        viewBinding.flyModeStatus.apply {
            if (airplaneModeStatus == 0) {
                text = getString(R.string.airplane_mode_closed)
                setTextColor(getColor(R.color.error))
            } else {
                text = getString(R.string.airplane_mode_opened)
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

        // 广播注册
        registerReceiver(airplaneReceiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))
        registerReceiver(userPresentReceiver, IntentFilter(Intent.ACTION_USER_PRESENT))

        val serviceIntent = Intent(this, TimeCountDownService::class.java)
        startService(serviceIntent)
        bindService(
            serviceIntent,
            connection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(BUNDLE_PARAM, viewModel.unLockCount)
        outState.putInt(BUNDLE_TIME_UNTIL, viewModel.timeUntil)
    }

    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage(R.string.dialog_message3)
            .setPositiveButton(R.string.dialog_confirm) { _, _ -> super.onBackPressed() }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .create()
        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
        unregisterReceiver(airplaneReceiver)
        unregisterReceiver(userPresentReceiver)
        unbindService(connection)
    }

    companion object {
        /**
         * Use this function to start [TimeCountDownActivity] to ensure that it has the correct params.
         */
        fun actionStart(context: Context, time: Int) {
            context.startActivity(Intent(context, TimeCountDownActivity::class.java).apply {
                putExtra(INTENT_PARAM, time)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
        }
    }
}