package com.korilin.concentration_detection.components

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.korilin.concentration_detection.R
import com.korilin.concentration_detection.viewmodel.ConcentrationViewModel

data class SelectorItem(
    var value: Int,
    var content: String,
    val click: (Button.(Button?, List<SelectorItem>, Int) -> Button?)? = null
)

fun Button.buttonSetNoSelected() {
    setTextColor(ContextCompat.getColor(context, R.color.f1))
    setBackgroundResource(R.drawable.concentration_time_no_selected_button)
}

fun Button.buttonSetSelected() {
    setTextColor(Color.WHITE)
    setBackgroundResource(R.drawable.concentration_time_selected_button)
}

class SelectorAdapter(
    private val selectorList: List<SelectorItem>,
    private var viewModel: ConcentrationViewModel
) : RecyclerView.Adapter<SelectorAdapter.ViewHolder>() {

    private var selected: Button? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val selectorButton = view.findViewById(R.id.selectorButton) as Button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.selector_button, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.selectorButton.apply {
            val customClick: (Button.(Button?, List<SelectorItem>, Int) -> Button?)?
            var time: Int
            selectorList[position].apply {
                text = content
                customClick = click
                time = value
            }
            setOnClickListener {
                selected = when {
                    customClick != null -> customClick(this, selected, selectorList, position)
                    this == selected -> buttonSetNoSelected().run { null }
                    else -> {
                        selected?.buttonSetNoSelected()
                        buttonSetSelected()
                        this
                    }
                }
                viewModel.time = if (selected != null) time else 0
            }
        }
    }

    override fun getItemCount(): Int = selectorList.size
}