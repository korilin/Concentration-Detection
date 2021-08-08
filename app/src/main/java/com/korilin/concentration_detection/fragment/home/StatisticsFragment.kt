package com.korilin.concentration_detection.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korilin.concentration_detection.R
import com.korilin.concentration_detection.databinding.FragmentStatisticsBinding
import com.korilin.concentration_detection.sqlite.ConcentrationSQLiteHelper
import com.korilin.concentration_detection.sqlite.Record
import com.korilin.concentration_detection.toDoubleDigit

/**
 * A simple [Fragment] subclass.
 * Use the [StatisticsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatisticsFragment : HomeTabLayoutFragment() {

    override val homeTabLayoutFragmentTag = "StatisticsFragment"

    private lateinit var helper: ConcentrationSQLiteHelper

    private lateinit var records: List<Record>

    private lateinit var viewBinding: FragmentStatisticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper = ConcentrationSQLiteHelper(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        viewBinding = FragmentStatisticsBinding.inflate(inflater)
        records = helper.selectRecords()

        viewBinding.recordRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = RecordAdapter(records)
        }

        // Inflate the layout for this fragment
        return viewBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            StatisticsFragment()
    }
}

class RecordAdapter(private val records: List<Record>) :
    RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val duration = view.findViewById(R.id.duration) as TextView
        val unLockCount = view.findViewById(R.id.unLockCount) as TextView
        val completionTime = view.findViewById(R.id.completeTime) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.record_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        records[position].also {
            holder.duration.text = with(it.duration) {
                val h = "${this / 3600}".toDoubleDigit()
                val min = "${this % 3600 / 60}".toDoubleDigit()
                "$h° $min′"
            }
            holder.unLockCount.text = it.unLockCount.toString()
            holder.completionTime.text = it.time
        }
    }

    override fun getItemCount(): Int = records.size
}