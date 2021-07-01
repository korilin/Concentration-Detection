package com.korilin.concentration_detection.components

import android.app.TimePickerDialog
import android.graphics.Rect
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korilin.concentration_detection.databinding.ConcentrationTimeSelectorBinding
import com.korilin.concentration_detection.fragment.home.ConcentrationFragment
import com.korilin.concentration_detection.viewmodel.ConcentrationViewModel

/**
 * 这是一个时间选择器
 *
 * - 基于 RecycleView 实现
 * - 使用父 Fragment 的 ViewModel 来共享选择的时间值
 *
 * @see ConcentrationFragment
 */
class ConcentrationTimeSelector : Fragment() {

    private var columnCount: Int = 2
    private val selectorItems = listOf(
        SelectorItem(30 * 60, "30 min"),
        SelectorItem(60 * 60, "60 min"),
        SelectorItem(90 * 60, "90 min"),
        SelectorItem(0 * 60, "custom") { selected, selectorList, position ->
            selected?.buttonSetNoSelected()
            selectorList[position].apply {
                content = "custom"
                value = 0
            }
            TimePickerDialog(
                activity,
                { _, hourOfDay, minute ->
                    val min = hourOfDay * 60 + minute
                    selectorList[position].apply {
                        content = "$min min"
                        value = min * 60
                    }.also {
                        text = it.content
                        viewModel.time = it.value
                    }
                    buttonSetSelected()
                }, 0, 0, true
            ).apply {
                setTitle("Set Custom Concentration Time")
            }.show()
            this
        }
    )

    private lateinit var selectorViewBinding: ConcentrationTimeSelectorBinding

    private lateinit var selectorRecyclerView: RecyclerView

    /**
     * get parent Fragment ViewModel, 原理未知
     * this solution comes from stackOverflow:
     *
     * [https://stackoverflow.com/questions/59952673/how-to-get-an-instance-of-viewmodel-in-activity-in-2020-21]
     */
    private val viewModel: ConcentrationViewModel by viewModels(ownerProducer = { requireParentFragment() })

    /**
     * A  [E/RecyclerView: No adapter attached; skipping layout] note
     *
     * 如果使用 ViewBinding 会生成一个 Fragment 布局
     * 而在 onCreateView 中如果使用 [inflater] 再次 inflate 的话
     * 那么实际加载的是后面的 Fragment 布局，会导致 RecyclerView 渲染不正常
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        selectorViewBinding = ConcentrationTimeSelectorBinding.inflate(inflater)

        selectorRecyclerView = selectorViewBinding.selectorRecyclerView.apply {
            // activity [getActivity] or context [this.getContext] ? How to choice
            // I see someone using getActivity() as content
            layoutManager = object : GridLayoutManager(activity, columnCount) {
                override fun canScrollVertically() = false
            }.apply {
                offsetChildrenHorizontal(100)
            }
            adapter = SelectorAdapter(selectorItems, viewModel)

            // set items padding, base with [columnCount] is 2
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect, view: View,
                    parent: RecyclerView, state: RecyclerView.State
                ) {
                    val span = 30
                    outRect.apply {
                        bottom = span
                        right = span * 2
                        left = span * 2
                        top = span
                    }
                }
            })
        }

        // Inflate the layout for this fragment
        return selectorViewBinding.root
    }
}

