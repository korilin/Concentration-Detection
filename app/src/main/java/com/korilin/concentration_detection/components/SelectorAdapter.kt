package com.korilin.concentration_detection.components

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.korilin.concentration_detection.R

data class SelectorItem(val value: Int, val content: String, val click: (() -> Unit)? = null)

class SelectorAdapter(
    private val selectorList: List<SelectorItem>
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
            text = selectorList[position].content
            setOnClickListener {
                selected = if (this == selected) {
                    setTextColor(ContextCompat.getColor(context, R.color.f1))
                    setBackgroundResource(R.drawable.concentration_time_no_selected_button)
                    null
                } else {
                    selected?.let {
                        it.setTextColor(ContextCompat.getColor(context, R.color.f1))
                        it.setBackgroundResource(R.drawable.concentration_time_no_selected_button)
                    }
                    setTextColor(Color.WHITE)
                    setBackgroundResource(R.drawable.concentration_time_selected_button)
                    this
                }
            }
        }
    }

    override fun getItemCount(): Int = selectorList.size
}