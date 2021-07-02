package com.korilin.concentration_detection.fragment.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.korilin.concentration_detection.R
import com.korilin.concentration_detection.TimeCountDownActivity
import com.korilin.concentration_detection.components.ConcentrationTimeSelector
import com.korilin.concentration_detection.databinding.FragmentConcentrationBinding
import com.korilin.concentration_detection.viewmodel.ConcentrationViewModel

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

    private lateinit var viewBinding: FragmentConcentrationBinding
    private lateinit var viewModel: ConcentrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConcentrationViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentConcentrationBinding.inflate(inflater)
        viewBinding.startButton.apply {
            setOnClickListener {
                if (viewModel.time == 0)
                    Toast.makeText(
                        activity, "Please select concentration time!", Toast.LENGTH_LONG
                    ).show()
                else {
                    try {
                        startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
                    } catch (ane: ActivityNotFoundException) {
                        Toast.makeText(
                            activity,
                            "Be sure to adjust your internet, volume, and other settings!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    TimeCountDownActivity.actionStart(requireActivity(), viewModel.time)
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
}