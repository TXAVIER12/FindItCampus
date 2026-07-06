package com.ulk.findcampus.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single Lost or Found report stored locally in the app database.
 */
@Entity(tableName = "item_reports")
data class ItemReport(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val itemName: String,
    val category: String,
    val reportType: String,      // "Lost" or "Found"
    val description: String,
    val location: String,        // last-seen location (Lost) or found location (Found)
    val date: String,            // date lost / date found
    val contactName: String,
    val contactInfo: String,     // phone or email
    val handInLocation: String = "", // only used for Found reports
    var status: String = STATUS_OPEN,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val TYPE_LOST = "Lost"
        const val TYPE_FOUND = "Found"

        const val STATUS_OPEN = "Open"
        const val STATUS_CLAIMED = "Claimed"
        const val STATUS_RETURNED = "Returned"
        const val STATUS_RESOLVED = "Resolved"

        val CATEGORIES = arrayOf(
            "ID Card", "Electronics", "Book", "Bag", "Key", "Clothing", "Other"
        )
    }
}
