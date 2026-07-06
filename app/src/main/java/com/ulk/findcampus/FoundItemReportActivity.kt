package com.ulk.findcampus

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ulk.findcampus.data.AppDatabase
import com.ulk.findcampus.data.ItemReport
import com.ulk.findcampus.data.ReportRepository
import com.ulk.findcampus.databinding.ActivityFoundItemReportBinding
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class FoundItemReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoundItemReportBinding
    private lateinit var repository: ReportRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoundItemReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = AppDatabase.getDatabase(applicationContext).itemReportDao()
        repository = ReportRepository(dao)

        setupForm()
        setupToggles()
        setupErrorClearing()
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
        binding.btnSwitchLost.setOnClickListener {
            startActivity(Intent(this, LostItemReportActivity::class.java))
            finish()
        }
    }

    private fun setupErrorClearing() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilItemName.error = null
                binding.tilDescription.error = null
                binding.tilLocation.error = null
                binding.tilDate.error = null
                binding.tilHandInLocation.error = null
                binding.tilContactInfo.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etItemName.addTextChangedListener(watcher)
        binding.etDescription.addTextChangedListener(watcher)
        binding.etLocation.addTextChangedListener(watcher)
        binding.etDate.addTextChangedListener(watcher)
        binding.etHandInLocation.addTextChangedListener(watcher)
        binding.etContactInfo.addTextChangedListener(watcher)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.etDate.setText(formattedDate)
        }, year, month, day)
        
        dpd.datePicker.maxDate = System.currentTimeMillis()
        dpd.show()
    }

    private fun saveReport() {
        val itemName = binding.etItemName.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem?.toString().orEmpty()
        val description = binding.etDescription.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val handInLocation = binding.etHandInLocation.text.toString().trim()
        val contactInfo = binding.etContactInfo.text.toString().trim()

        var isValid = true

        if (itemName.length < 3) {
            binding.tilItemName.error = "Item name must be at least 3 characters"
            isValid = false
        }

        if (description.length < 5) {
            binding.tilDescription.error = "Please provide more detail"
            isValid = false
        }

        if (location.isEmpty()) {
            binding.tilLocation.error = "Found location is required"
            isValid = false
        }

        if (date.isEmpty()) {
            binding.tilDate.error = "Please select the date"
            isValid = false
        }

        if (handInLocation.isEmpty()) {
            binding.tilHandInLocation.error = "Storage/Hand-in location is required"
            isValid = false
        }

        if (contactInfo.isEmpty()) {
            binding.tilContactInfo.error = "Contact or office ref is required"
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(this, "Please check all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val report = ItemReport(
            itemName = itemName,
            category = category,
            reportType = ItemReport.TYPE_FOUND,
            description = description,
            location = location,
            date = date,
            contactName = "",
            contactInfo = contactInfo,
            handInLocation = handInLocation
        )

        lifecycleScope.launch {
            repository.insert(report)
            Toast.makeText(this@FoundItemReportActivity, "Found item report saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
