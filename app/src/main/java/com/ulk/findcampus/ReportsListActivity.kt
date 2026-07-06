package com.ulk.findcampus

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.ulk.findcampus.adapter.ReportsAdapter
import com.ulk.findcampus.data.AppDatabase
import com.ulk.findcampus.data.ItemReport
import com.ulk.findcampus.data.ReportRepository
import com.ulk.findcampus.databinding.ActivityReportsListBinding

class ReportsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportsListBinding
    private lateinit var repository: ReportRepository
    private lateinit var adapter: ReportsAdapter

    private var currentKeyword: String = ""
    private var currentType: String = "All"
    private var currentCategory: String = "All"
    private var currentLiveData: LiveData<List<ItemReport>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = AppDatabase.getDatabase(applicationContext).itemReportDao()
        repository = ReportRepository(dao)

        adapter = ReportsAdapter { report ->
            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra(ItemDetailActivity.EXTRA_REPORT_ID, report.id)
            startActivity(intent)
        }
        binding.recyclerReports.layoutManager = LinearLayoutManager(this)
        binding.recyclerReports.adapter = adapter

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupFilters()
        refreshList()
    }

    private fun setupFilters() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentKeyword = query.orEmpty()
                refreshList()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentKeyword = newText.orEmpty()
                refreshList()
                return true
            }
        })

        // Type Filters
        binding.chipAll.setOnClickListener { currentType = "All"; refreshList() }
        binding.chipLost.setOnClickListener { currentType = ItemReport.TYPE_LOST; refreshList() }
        binding.chipFound.setOnClickListener { currentType = ItemReport.TYPE_FOUND; refreshList() }

        // Category Filters
        ItemReport.CATEGORIES.forEach { categoryName ->
            val chip = Chip(this, null, com.google.android.material.R.attr.chipStyle)
            chip.text = categoryName
            chip.isCheckable = true
            chip.setOnClickListener {
                currentCategory = if (chip.isChecked) categoryName else "All"
                refreshList()
            }
            binding.chipGroupCategory.addView(chip)
        }
        binding.catAll.setOnClickListener {
            currentCategory = "All"
            refreshList()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        currentLiveData?.removeObservers(this)
        val liveData = repository.search(currentKeyword, currentType, currentCategory)
        currentLiveData = liveData
        liveData.observe(this) { reports ->
            adapter.submitList(reports)
            binding.emptyState.visibility = if (reports.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }
    }
}
