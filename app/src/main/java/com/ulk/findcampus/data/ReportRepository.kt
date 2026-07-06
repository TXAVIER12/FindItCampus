package com.ulk.findcampus.data

import androidx.lifecycle.LiveData

class ReportRepository(private val dao: ItemReportDao) {

    val allReports: LiveData<List<ItemReport>> = dao.getAllReports()
    val lostCount: LiveData<Int> = dao.getLostCount()
    val foundCount: LiveData<Int> = dao.getFoundCount()

    suspend fun insert(report: ItemReport): Long = dao.insert(report)

    suspend fun update(report: ItemReport) = dao.update(report)

    suspend fun delete(report: ItemReport) = dao.delete(report)

    suspend fun getReportById(id: Int): ItemReport? = dao.getReportById(id)

    fun search(keyword: String, reportType: String, category: String = "All"): LiveData<List<ItemReport>> =
        dao.searchReports(keyword, reportType, category)
}
