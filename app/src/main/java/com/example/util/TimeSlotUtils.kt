package com.example.util

import com.example.data.models.KitchenSettingsConfig
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object TimeSlotUtils {

    /**
     * Parses time string like "11:00 AM", "07:30 PM", "12:00 PM" into total minutes from midnight (0..1439).
     */
    fun parseTimeToMinutes(timeStr: String): Int? {
        val clean = timeStr.trim().uppercase(Locale.ENGLISH)
        val regex = Regex("""(\d{1,2}):(\d{2})\s*(AM|PM)""")
        val match = regex.find(clean) ?: return null

        var hour = match.groupValues[1].toIntOrNull() ?: return null
        val minute = match.groupValues[2].toIntOrNull() ?: return null
        val amPm = match.groupValues[3]

        if (amPm == "PM" && hour < 12) hour += 12
        if (amPm == "AM" && hour == 12) hour = 0

        return hour * 60 + minute
    }

    /**
     * Extracts the start time of a slot string and converts it to minutes from midnight.
     * Handles formats like "11:00 AM - 02:00 PM (Lunch)" or "11:00 AM - 11:30 AM".
     */
    fun getSlotStartMinutes(slotStr: String): Int? {
        val parts = slotStr.split("-")
        if (parts.isNotEmpty()) {
            return parseTimeToMinutes(parts[0])
        }
        return null
    }

    /**
     * Checks if a date string refers to Today.
     */
    fun isToday(dateStr: String): Boolean {
        val todayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val todayStr = todayFormatter.format(Calendar.getInstance().time)
        if (dateStr.startsWith(todayStr)) return true

        val lower = dateStr.lowercase(Locale.ENGLISH)
        if (lower.contains("today") || lower.contains("आज")) return true

        return false
    }

    /**
     * Filters time slots for Same Day vs Next Day / Future Date.
     * - Future Day: returns all configured slots.
     * - Same Day:
     *   - If same day booking is disabled in settings -> returns empty list.
     *   - Filters slots whose start time is AFTER (current_time + prep_lead_time)
     *     and within sameDayDeliveryStartTime and sameDayDeliveryEndTime.
     */
    fun getAvailableTimeSlots(
        selectedDateStr: String,
        allSlots: List<String>,
        config: KitchenSettingsConfig
    ): List<String> {
        val isSameDay = isToday(selectedDateStr)
        if (!isSameDay) {
            return allSlots
        }

        if (!config.allowSameDayBooking) {
            return emptyList()
        }

        val now = Calendar.getInstance()
        val currentMins = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val prepLeadMins = (config.sameDayPrepLeadTimeHours * 60).toInt()
        val cutoffMins = currentMins + prepLeadMins

        val deliveryStartMins = parseTimeToMinutes(config.sameDayDeliveryStartTime) ?: 0
        val deliveryEndMins = parseTimeToMinutes(config.sameDayDeliveryEndTime) ?: 1440

        return allSlots.filter { slot ->
            val slotStartMins = getSlotStartMinutes(slot)
            if (slotStartMins == null) {
                true // Fallback to include if unparseable
            } else {
                slotStartMins >= cutoffMins && slotStartMins >= deliveryStartMins && slotStartMins <= deliveryEndMins
            }
        }
    }
}
