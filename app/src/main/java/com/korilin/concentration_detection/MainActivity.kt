package com.korilin.concentration_detection

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.korilin.concentration_detection.databinding.ActivityMainBinding
import com.korilin.concentration_detection.fragment.home.ConcentrationFragment
import com.korilin.concentration_detection.fragment.home.StatisticsFragment
import com.korilin.concentration_detection.viewmodel.MainViewModel

val tabName = arrayOf(R.string.concentration_tab_name, R.string.statistics_tab_name)

/**
 * 主页
 */
class MainActivity : FragmentActivity() {

    lateinit var viewBinding: ActivityMainBinding

    private lateinit var homeTabLayout: TabLayout
    private lateinit var homeViewPager: ViewPager2
    private val viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivity", "Thread = ${Thread.currentThread().name}")

        viewBinding = ActivityMainBinding.inflate(layoutInflater).also {
            homeTabLayout = it.homeTableLayout
            homeViewPager = it.homeViewPager
            setContentView(it.root)
        }

        homeViewPager.adapter = HomeViewPager2Adapter(this)

        TabLayoutMediator(homeTabLayout, homeViewPager) { tab, position ->
            tab.text = getString(tabName[position])
        }.attach()
    }

    inner class HomeViewPager2Adapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        override fun getItemCount() = tabName.size

        /**
         * Provide a new Fragment associated with the specified position.
         *
         *
         * The adapter will be responsible for the Fragment lifecycle:
         *
         *  * The Fragment will be used to display an item.
         *  * The Fragment will be destroyed when it gets too far from the viewport, and its state
         * will be saved. When the item is close to the viewport again, a new Fragment will be
         * requested, and a previously saved state will be used to initialize it.
         *
         * @see ViewPager2.setOffscreenPageLimit
         */
        override fun createFragment(position: Int) = when (position) {
            0 -> ConcentrationFragment.newInstance()
            1 -> StatisticsFragment.newInstance()
            else -> throw NoClassDefFoundError("No corresponding Fragment is available ：position=$position")
        }

    }

}