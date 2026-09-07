package com.example.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ContainerOverdueHelper {
    /**
     * Calculate how many days a container is overdue.
     * Food delivery containers (Degs/Handis) are expected to be returned the next day (within 24 hours of event).
     */
    fun calculateDaysOverdue(dateStr: String): Int {
        if (dateStr.isBlank()) return 1
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val eventDate = sdf.parse(dateStr)
            if (eventDate != null) {
                val now = Date()
                val diffMillis = now.time - eventDate.time
                val diffDays = (diffMillis / (1000L * 60 * 60 * 24)).toInt()
                if (diffDays > 0) diffDays else 0
            } else 1
        } catch (e: Exception) {
            1
        }
    }

    /**
     * Determines whether a pending container is overdue.
     * Returns true if the container has not been collected and its delivery date is in the past.
     */
    fun isOverdue(deliveryDate: String, isCollected: Boolean): Boolean {
        if (isCollected) return false
        val days = calculateDaysOverdue(deliveryDate)
        if (days >= 1) return true
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return deliveryDate.isNotBlank() && deliveryDate < todayStr
    }
}
