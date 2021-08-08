package com.korilin.concentration_detection.fragment.home

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.korilin.concentration_detection.R
import com.korilin.concentration_detection.TimeCountDownActivity
import com.korilin.concentration_detection.components.ConcentrationTimeSelector
import com.korilin.concentration_detection.databinding.FragmentConcentrationBinding
import com.korilin.concentration_detection.viewmodel.MainViewModel

/**
 * A home ViewPager2 [Fragment].
 * Use the [ConcentrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 * ==== description by Kori ====
 *
 * 在这个 Fragment 里面可以选择专注时间：
 * - 专注时间选择按钮为单选按钮，基于 RecycleView 实现的一个单独的 Fragment
 * - 使用 ViewModel 来共享子 Fragment 选择的时间
 * - 通过开始按钮启动专注 Activity，并将时间传递给 Activity
 *
 * @see ConcentrationTimeSelector
 */
class ConcentrationFragment : HomeTabLayoutFragment() {

    override val homeTabLayoutFragmentTag = "ConcentrationFragment"

    private lateinit var viewBinding: FragmentConcentrationBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        viewBinding = FragmentConcentrationBinding.inflate(inflater)

        val panelLauncher = registerForActivityResult(PanelResultContract()) {
            TimeCountDownActivity.actionStart(requireActivity(), viewModel.time)
        }

        viewBinding.startButton.apply {
            setOnClickListener {
                if (viewModel.time == 0)
                    Toast.makeText(
                        activity,
                        getString(R.string.select_concentration_time_message),
                        Toast.LENGTH_LONG
                    ).show()
                else {
                    try {
                        panelLauncher.launch(Unit)
                    } catch (ane: ActivityNotFoundException) {
                        Toast.makeText(
                            activity,
                            getString(R.string.be_sure_concentration_settings),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        // Inflate the layout for this fragment
        return viewBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ConcentrationFragment()
    }



    inner class PanelResultContract : ActivityResultContract<Unit, Unit>() {
        /** Create an intent that can be used for [Activity.startActivityForResult]  */
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createIntent(context: Context, input: Unit?) =
            Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)

        /** Convert result obtained from [Activity.onActivityResult] to O  */
        override fun parseResult(resultCode: Int, intent: Intent?) = run { }

    }
}