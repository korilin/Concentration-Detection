package com.korilin.concentration_detection.components

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korilin.concentration_detection.databinding.ConcentrationTimeSelectorBinding

class ConcentrationTimeSelector : Fragment() {

    private var columnCount: Int = 2
    private val selectorItems = listOf(
        SelectorItem(30, "30 min"),
        SelectorItem(60, "60 min"),
        SelectorItem(90, "90 min"),
    )

    private lateinit var selectorViewBinding: ConcentrationTimeSelectorBinding

    private lateinit var selectorRecyclerView: RecyclerView

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
            layoutManager = GridLayoutManager(activity, columnCount).apply {
                offsetChildrenHorizontal(100)
            }
            adapter = SelectorAdapter(selectorItems)

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
                        left = span *2
                        top = span
                    }
                }
            })
        }

        // Inflate the layout for this fragment
        return selectorViewBinding.root
    }
}

