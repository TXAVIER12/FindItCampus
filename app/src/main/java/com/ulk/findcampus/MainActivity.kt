package com.ulk.findcampus

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ulk.findcampus.adapter.ReportsAdapter
import com.ulk.findcampus.data.AppDatabase
import com.ulk.findcampus.data.ReportRepository
import com.ulk.findcampus.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: ReportRepository
    private lateinit var adapter: ReportsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = AppDatabase.getDatabase(applicationContext).itemReportDao()
        repository = ReportRepository(dao)

        setupSummary()
        setupQuickActions()
        setupDashboardList()
        setupBottomNavigation()
    }

    private fun setupSummary() {
        repository.lostCount.observe(this) { count ->
            binding.tvLostCount.text = (count ?: 0).toString()
        }
        repository.foundCount.observe(this) { count ->
            binding.tvFoundCount.text = (count ?: 0).toString()
        }
    }

    private fun setupQuickActions() {
        binding.btnReportLost.setOnClickListener {
            startActivity(Intent(this, LostItemReportActivity::class.java))
        }
        binding.btnReportFound.setOnClickListener {
            startActivity(Intent(this, FoundItemReportActivity::class.java))
        }
    }

    private fun setupDashboardList() {
        adapter = ReportsAdapter { report ->
            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra(ItemDetailActivity.EXTRA_REPORT_ID, report.id)
            startActivity(intent)
        }
        binding.recyclerDashboard.layoutManager = LinearLayoutManager(this)
        binding.recyclerDashboard.adapter = adapter

        repository.allReports.observe(this) { reports ->
            adapter.submitList(reports.take(5)) // Show only top 5 on dashboard
            binding.tvOpenCountLabel.text = "${reports.size} open"
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_report -> {
                    startActivity(Intent(this, LostItemReportActivity::class.java))
                    false
                }
                R.id.nav_reports -> {
                    startActivity(Intent(this, ReportsListActivity::class.java))
                    false
                }
                R.id.nav_info -> {
                    startActivity(Intent(this, OfficeInfoActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }
}
