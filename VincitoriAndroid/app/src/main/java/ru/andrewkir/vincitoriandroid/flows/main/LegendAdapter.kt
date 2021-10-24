package ru.andrewkir.vincitoriandroid.flows.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ru.andrewkir.vincitoriandroid.R
import ru.andrewkir.vincitoriandroid.common.round
import ru.andrewkir.vincitoriandroid.web.model.LegendX
import java.text.DecimalFormat
import java.util.*

class LegendAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isLoadingState = false
    var loadingOffset = 0

    companion object {
        const val COURSE_VIEW = 0
        const val LOADING_VIEW = 1
    }

    var data: List<LegendX> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LegendViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.row_legend, viewGroup, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoadingState) {
            if (position == data.size) LOADING_VIEW else COURSE_VIEW
        } else {
            COURSE_VIEW
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val hexColor = java.lang.String.format(
            "#%06X",
            0xFFFFFF and data[position].color!!
        )
        (viewHolder as LegendViewHolder).colorView.foreground =
            ColorDrawable(Color.parseColor("#66${hexColor.slice(1 until hexColor.length)}"))

        viewHolder.colorText.text = "${data[position].minValue!!.round(2)} - ${data[position].maxValue!!.round(2)}"
    }


    inner class LegendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var colorView: ImageView = view.findViewById(R.id.colorView)
        var colorText: TextView = view.findViewById(R.id.colorText)
    }

    override fun getItemCount() = data.size + loadingOffset
}