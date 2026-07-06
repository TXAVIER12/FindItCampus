package com.ulk.findcampus

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import android.graphics.drawable.GradientDrawable
import com.ulk.findcampus.data.AppDatabase
import com.ulk.findcampus.data.ItemReport
import com.ulk.findcampus.data.ReportRepository
import com.ulk.findcampus.databinding.ActivityItemDetailBinding
import kotlinx.coroutines.launch

class ItemDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemDetailBinding
    private lateinit var repository: ReportRepository
    private var currentReport: ItemReport? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = AppDatabase.getDatabase(applicationContext).itemReportDao()
        repository = ReportRepository(dao)

        binding.toolbar.setNavigationOnClickListener { finish() }

        val reportId = intent.getIntExtra(EXTRA_REPORT_ID, -1)
        if (reportId == -1) {
            Toast.makeText(this, "Report not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadReport(reportId)

        binding.btnMarkClaimed.setOnClickListener { updateStatus(ItemReport.STATUS_CLAIMED) }
        binding.btnMarkReturned.setOnClickListener { updateStatus(ItemReport.STATUS_RETURNED) }
        binding.btnMarkResolved.setOnClickListener { updateStatus(ItemReport.STATUS_RESOLVED) }
        binding.btnDelete.setOnClickListener { confirmDelete() }
    }

    private fun loadReport(id: Int) {
        lifecycleScope.launch {
            val report = repository.getReportById(id)
            if (report == null) {
                Toast.makeText(this@ItemDetailActivity, "Report not found", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            currentReport = report
            renderReport(report)
        }
    }

    private fun renderReport(report: ItemReport) {
        binding.tvDetailType.text = report.reportType.uppercase()

        val typeColor = if (report.reportType == ItemReport.TYPE_LOST)
            ContextCompat.getColor(this, R.color.lost_red)
        else
            ContextCompat.getColor(this, R.color.found_teal)
        
        (binding.tvDetailType.background as? GradientDrawable)?.setColor(typeColor)

        binding.tvDetailStatus.text = "Status: ${report.status}"
        val statusColor = when (report.status) {
            ItemReport.STATUS_OPEN -> ContextCompat.getColor(this, R.color.status_open)
            ItemReport.STATUS_CLAIMED -> ContextCompat.getColor(this, R.color.lost_red)
            ItemReport.STATUS_RETURNED -> ContextCompat.getColor(this, R.color.found_teal)
            else -> ContextCompat.getColor(this, R.color.primary_dark)
        }
        binding.tvDetailStatus.setTextColor(statusColor)

        binding.tvDetailItemName.text = report.itemName
        binding.tvDetailCategory.text = "Category: ${report.category}"
        binding.tvDetailDescription.text =
            report.description.ifEmpty { "No description provided" }
        binding.tvDetailLocation.text = report.location
        binding.tvDetailDate.text = report.date
        binding.tvDetailContact.text = buildString {
            if (report.contactName.isNotEmpty()) append(report.contactName).append(" · ")
            append(report.contactInfo.ifEmpty { "Not provided" })
        }

        if (report.reportType == ItemReport.TYPE_FOUND) {
            binding.tvHandInLabel.visibility = View.VISIBLE
            binding.tvDetailHandIn.visibility = View.VISIBLE
            binding.tvDetailHandIn.text = report.handInLocation.ifEmpty { "Not specified" }
        } else {
            binding.tvHandInLabel.visibility = View.GONE
            binding.tvDetailHandIn.visibility = View.GONE
        }
    }

    private fun updateStatus(newStatus: String) {
        val report = currentReport ?: return
        report.status = newStatus
        lifecycleScope.launch {
            repository.update(report)
            renderReport(report)
            Toast.makeText(
                this@ItemDetailActivity,
                "Marked as $newStatus",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Delete report")
            .setMessage("This will permanently remove this report. Continue?")
            .setPositiveButton("Delete") { _, _ -> deleteReport() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteReport() {
        val report = currentReport ?: return
        lifecycleScope.launch {
            repository.delete(report)
            Toast.makeText(this@ItemDetailActivity, "Report deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object {
        const val EXTRA_REPORT_ID = "extra_report_id"
    }
}
