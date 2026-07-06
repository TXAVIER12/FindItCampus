package com.ulk.findcampus.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ItemReportDao {

    @Insert
    suspend fun insert(report: ItemReport): Long

    @Update
    suspend fun update(report: ItemReport)

    @Delete
    suspend fun delete(report: ItemReport)

    @Query("SELECT * FROM item_reports WHERE id = :id LIMIT 1")
    suspend fun getReportById(id: Int): ItemReport?

    @Query("SELECT * FROM item_reports ORDER BY createdAt DESC")
    fun getAllReports(): LiveData<List<ItemReport>>

    /**
     * Combined search + filter query.
     * reportType: "All", "Lost" or "Found"
     * keyword: matched (case-insensitive, partial) against name, category, description, location
     */
    @Query(
        """
        SELECT * FROM item_reports
        WHERE (:reportType = 'All' OR reportType = :reportType)
        AND (:category = 'All' OR category = :category)
        AND (
            itemName LIKE '%' || :keyword || '%'
            OR description LIKE '%' || :keyword || '%'
            OR location LIKE '%' || :keyword || '%'
        )
        ORDER BY createdAt DESC
        """
    )
    fun searchReports(keyword: String, reportType: String, category: String): LiveData<List<ItemReport>>

    @Query("SELECT COUNT(*) FROM item_reports WHERE reportType = 'Lost'")
    fun getLostCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM item_reports WHERE reportType = 'Found'")
    fun getFoundCount(): LiveData<Int>
}
