package com.korilin.concentration_detection.fragment.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

sealed class HomeTabLayoutFragment : Fragment() {

    open val homeTabLayoutFragmentTag = "HomeTabLayoutFragment"

    private fun logI(message:String) = Log.i(homeTabLayoutFragmentTag, message)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logI("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logI("onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logI("onViewCreated")
        return null
    }

    override fun onStart() {
        super.onStart()
        logI("onStart")
    }

    override fun onResume() {
        super.onResume()
        logI("onResume")
    }

    override fun onPause() {
        super.onPause()
        logI("onPause")
    }

    override fun onStop() {
        super.onStop()
        logI("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        logI("onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        logI("onDetach")
    }
}