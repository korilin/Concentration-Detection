package com.korilin.concentration_detection.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.korilin.concentration_detection.R
import com.korilin.concentration_detection.databinding.FragmentConcentrationBinding
import com.korilin.concentration_detection.viewmodel.ConcentrationViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [ConcentrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConcentrationFragment : HomeTabLayoutFragment() {

    lateinit var viewBinding: FragmentConcentrationBinding
    lateinit var viewModel: ViewModel

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