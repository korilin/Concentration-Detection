package com.korilin.concentration_detection.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.korilin.concentration_detection.R
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

    lateinit var viewBinding: FragmentConcentrationBinding
    lateinit var viewModel: ConcentrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConcentrationViewModel::class.java)
    }

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
                else{
                    Toast.makeText(
                        activity, "${viewModel.time}", Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Inflate the layout for this fragment
        return viewBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ConcentrationFragment.
         */
        @JvmStatic
        fun newInstance() =
            ConcentrationFragment()
    }
}