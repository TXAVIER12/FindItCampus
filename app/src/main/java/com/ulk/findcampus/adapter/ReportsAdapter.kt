package com.ulk.findcampus.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import android.graphics.drawable.GradientDrawable
import com.ulk.findcampus.R
import com.ulk.findcampus.data.ItemReport
import com.ulk.findcampus.databinding.ItemReportRowBinding
import java.util.Locale

class ReportsAdapter(
    private val onItemClick: (ItemReport) -> Unit
) : ListAdapter<ItemReport, ReportsAdapter.ReportViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReportViewHolder(private val binding: ItemReportRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(report: ItemReport) {
            val context = binding.root.context
            
            binding.tvItemName.text = report.itemName
            binding.tvTicketNumber.text = if (report.reportType == ItemReport.TYPE_LOST) 
                "#L-${String.format(Locale.US, "%04d", report.id)}" 
            else 
                "#F-${String.format(Locale.US, "%04d", report.id)}"
            
            binding.tvLocation.text = report.location
            binding.tvDate.text = report.date
            
            // Initials
            val words = report.itemName.split(" ").filter { it.isNotEmpty() }
            val initials = if (words.size >= 2) {
                "${words[0][0]}${words[1][0]}".uppercase()
            } else if (words.isNotEmpty()) {
                words[0].take(2).uppercase()
            } else {
                "??"
            }
            binding.tvInitials.text = initials
            
            // Pill
            binding.tvReportTypePill.text = report.reportType.uppercase()
            val typeColor = if (report.reportType == ItemReport.TYPE_LOST)
                ContextCompat.getColor(context, R.color.lost_red)
            else
                ContextCompat.getColor(context, R.color.found_teal)
            
            (binding.tvReportTypePill.background as? GradientDrawable)?.apply {
                setStroke(1 * context.resources.displayMetrics.density.toInt(), typeColor)
            }
            binding.tvReportTypePill.setTextColor(typeColor)

            // Circle Color
            (binding.tvInitials.parent as? android.view.View)?.background?.let { bg ->
                if (bg is GradientDrawable) {
                    val circleColor = when (report.category) {
                        "Electronics" -> ContextCompat.getColor(context, R.color.deep_forest_green)
                        "Book" -> ContextCompat.getColor(context, R.color.lost_red)
                        "Bag" -> ColorUtils.darken(typeColor, 0.2f)
                        else -> typeColor
                    }
                    bg.setColor(circleColor)
                }
            }

            binding.root.setOnClickListener { onItemClick(report) }
        }
    }

    private object ColorUtils {
        fun darken(color: Int, factor: Float): Int {
            val a = android.graphics.Color.alpha(color)
            val r = Math.round(android.graphics.Color.red(color) * (1 - factor))
            val g = Math.round(android.graphics.Color.green(color) * (1 - factor))
            val b = Math.round(android.graphics.Color.blue(color) * (1 - factor))
            return android.graphics.Color.argb(a, r, g, b)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemReport>() {
            override fun areItemsTheSame(oldItem: ItemReport, newItem: ItemReport) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ItemReport, newItem: ItemReport) =
                oldItem == newItem
        }
    }
}
