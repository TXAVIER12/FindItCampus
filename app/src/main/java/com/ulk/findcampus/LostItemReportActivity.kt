package com.ulk.findcampus

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ulk.findcampus.data.AppDatabase
import com.ulk.findcampus.data.ItemReport
import com.ulk.findcampus.data.ReportRepository
import com.ulk.findcampus.databinding.ActivityLostItemReportBinding
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class LostItemReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLostItemReportBinding
    private lateinit var repository: ReportRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostItemReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = AppDatabase.getDatabase(applicationContext).itemReportDao()
        repository = ReportRepository(dao)

        setupForm()
        setupToggles()
    }

    private fun setupForm() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            ItemReport.CATEGORIES
        )
        binding.spinnerCategory.adapter = adapter

        binding.etDate.setOnClickListener { showDatePicker() }
        binding.btnSave.setOnClickListener { saveReport() }
    }

    private fun setupToggles() {
        binding.btnSwitchFound.setOnClickListener {
            startActivity(Intent(this, FoundItemReportActivity::class.java))
            finish()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.etDate.setText(formattedDate)
        }, year, month, day).show()
    }

    private fun saveReport() {
        val itemName = binding.etItemName.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem?.toString().orEmpty()
        val description = binding.etDescription.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val contactInfo = binding.etContactInfo.text.toString().trim()

        if (itemName.isEmpty() || location.isEmpty() || contactInfo.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val report = ItemReport(
            itemName = itemName,
            category = category,
            reportType = ItemReport.TYPE_LOST,
            description = description,
            location = location,
            date = date.ifEmpty { "Not specified" },
            contactName = "",
            contactInfo = contactInfo
        )

        lifecycleScope.launch {
            repository.insert(report)
            Toast.makeText(this@LostItemReportActivity, "Saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
