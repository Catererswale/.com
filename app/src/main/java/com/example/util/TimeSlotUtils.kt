package com.example.util

import com.example.data.models.KitchenSettingsConfig
import com.example.data.models.OrderEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class CancellationTier {
    SAME_DAY_NO_REFUND,
    MORE_THAN_24_HOURS_FULL_REFUND,
    BETWEEN_12_AND_24_HOURS_RESCHEDULE_OR_FORFEIT,
    LESS_THAN_12_HOURS_NO_REFUND
}

data class CancellationPolicyResult(
    val tier: CancellationTier,
    val hoursRemaining: Double,
    val isSameDay: Boolean,
    val customerRefundPercentage: Double,
    val customerRefundAmount: Double,
    val kitchenSettlementAmount: Double,
    val companyAdsFundAmount: Double,
    val canReschedule: Boolean,
    val policyExplanation: String
)

object TimeSlotUtils {

    /**
     * Standard Indian Standard Time (IST - Asia/Kolkata) Zone for all catering operations
     */
    val IST_TIME_ZONE: TimeZone = TimeZone.getTimeZone("Asia/Kolkata")
    val INDIA_LOCALE: Locale = Locale("en", "IN")

    fun getIndianCalendar(): Calendar = Calendar.getInstance(IST_TIME_ZONE, INDIA_LOCALE)

    fun createDateFormat(pattern: String): SimpleDateFormat {
        return SimpleDateFormat(pattern, Locale.ENGLISH).apply {
            timeZone = IST_TIME_ZONE
        }
    }

    /**
     * Parses time string like "11:00 AM", "07:30 PM", "12:00 PM", "3 PM" into total minutes from midnight (0..1439).
     */
    fun parseTimeToMinutes(timeStr: String): Int? {
        val clean = timeStr.trim().uppercase(Locale.ENGLISH)
        val regex = Regex("""(\d{1,2})(?::(\d{2}))?\s*(AM|PM)""")
        val match = regex.find(clean) ?: return null

        var hour = match.groupValues[1].toIntOrNull() ?: return null
        val minute = if (match.groupValues[2].isNotBlank()) match.groupValues[2].toIntOrNull() ?: 0 else 0
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
     * Kitchen Preparation Shifts:
     * - "MORNING" -> 06:00 AM to 03:00 PM (360 to 915 mins)
     * - "EVENING" -> 03:30 PM to 12:00 AM (916 to 1440 mins)
     */
    fun getPrepShift(slotStr: String): String {
        val lower = slotStr.lowercase(Locale.ENGLISH)
        if (lower.contains("dinner") || lower.contains("night") || lower.contains("evening") || lower.contains("raat")) {
            return "EVENING"
        }
        if (lower.contains("lunch") || lower.contains("morning") || lower.contains("subah") || lower.contains("dopahar") || lower.contains("breakfast")) {
            return "MORNING"
        }

        val startMins = getSlotStartMinutes(slotStr)
        if (startMins != null) {
            // 6:00 AM (360 mins) to 3:00 PM (900 mins, tolerance up to 915 mins)
            return if (startMins in 360..915) "MORNING" else "EVENING"
        }

        // Fallback check on AM vs PM
        return if (lower.contains("am") || lower.contains("12:") || lower.contains("01:") || lower.contains("02:")) "MORNING" else "EVENING"
    }

    fun isMorningShift(slotStr: String): Boolean = getPrepShift(slotStr) == "MORNING"
    fun isEveningShift(slotStr: String): Boolean = getPrepShift(slotStr) == "EVENING"

    /**
     * Compares two orders for kitchen preparation sequence:
     * 1. Delivery Date (earlier date on top)
     * 2. Shift order: Morning Shift (6 AM - 3 PM) first, Evening Shift (3:30 PM - 12 AM) second
     * 3. Delivery Start Time (earliest minute of the day on top, e.g. 11:00 AM before 12:30 PM before 01:30 PM)
     * 4. Created Timestamp (earlier booked order on top if time slots match)
     */
    fun compareOrdersForKitchenPrep(o1: OrderEntity, o2: OrderEntity): Int {
        val dateCompare = o1.deliveryDate.compareTo(o2.deliveryDate)
        if (dateCompare != 0) return dateCompare

        val s1 = if (isMorningShift(o1.deliveryTimeSlot)) 0 else 1
        val s2 = if (isMorningShift(o2.deliveryTimeSlot)) 0 else 1
        if (s1 != s2) return s1.compareTo(s2)

        val t1 = getSlotStartMinutes(o1.deliveryTimeSlot) ?: (if (s1 == 0) 720 else 1200)
        val t2 = getSlotStartMinutes(o2.deliveryTimeSlot) ?: (if (s2 == 0) 720 else 1200)
        if (t1 != t2) return t1.compareTo(t2)

        return o1.createdAtTimestamp.compareTo(o2.createdAtTimestamp)
    }

    /**
     * Checks if a date string refers to Today (in Indian Standard Time).
     */
    fun isToday(dateStr: String): Boolean {
        val todayFormatter = createDateFormat("yyyy-MM-dd")
        val todayStr = todayFormatter.format(getIndianCalendar().time)
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

        val now = getIndianCalendar()
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

    /**
     * Calculates the estimated hours remaining until delivery.
     * Positive value = delivery is in the future.
     * Negative value = scheduled slot has already started or passed.
     */
    fun calculateDeliveryHoursRemaining(deliveryDate: String, deliveryTimeSlot: String): Double {
        val now = getIndianCalendar()
        if (isToday(deliveryDate)) {
            val currentMins = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
            val slotStartMins = getSlotStartMinutes(deliveryTimeSlot) ?: (currentMins + 120)
            val diffMins = slotStartMins - currentMins
            return diffMins / 60.0
        }

        // Try parsing various date formats
        val formats = listOf(
            createDateFormat("yyyy-MM-dd"),
            createDateFormat("dd MMM yyyy"),
            createDateFormat("dd-MM-yyyy"),
            createDateFormat("yyyy/MM/dd")
        )

        var parsedDate: Date? = null
        for (format in formats) {
            try {
                parsedDate = format.parse(deliveryDate.trim())
                if (parsedDate != null) break
            } catch (_: Exception) {}
        }

        val targetCal = getIndianCalendar()
        if (parsedDate != null) {
            targetCal.time = parsedDate
            val slotStartMins = getSlotStartMinutes(deliveryTimeSlot) ?: (12 * 60)
            targetCal.set(Calendar.HOUR_OF_DAY, slotStartMins / 60)
            targetCal.set(Calendar.MINUTE, slotStartMins % 60)
            targetCal.set(Calendar.SECOND, 0)
            targetCal.set(Calendar.MILLISECOND, 0)
        } else {
            // Default to future 24h if string not parsed
            val futureCal = getIndianCalendar()
            futureCal.add(Calendar.DAY_OF_YEAR, 2)
            targetCal.time = futureCal.time
        }

        val diffMillis = targetCal.timeInMillis - now.timeInMillis
        return diffMillis / (1000.0 * 60.0 * 60.0)
    }

    /**
     * Rule: Kitchen preparation button is LOCKED until 10 hours before delivery.
     * Same-day deliveries can be prepared immediately upon acceptance.
     */
    fun isKitchenPrepAllowed(deliveryDate: String, deliveryTimeSlot: String): Boolean {
        if (isToday(deliveryDate)) return true
        val hoursRemaining = calculateDeliveryHoursRemaining(deliveryDate, deliveryTimeSlot)
        return hoursRemaining <= 10.0
    }

    /**
     * User-friendly label and countdown for Kitchen Prep button lock.
     */
    fun getKitchenPrepCountdownLabel(deliveryDate: String, deliveryTimeSlot: String): String {
        if (isToday(deliveryDate)) return "Start Cooking 🍳"
        val hoursRemaining = calculateDeliveryHoursRemaining(deliveryDate, deliveryTimeSlot)
        if (hoursRemaining <= 10.0) {
            return "Start Cooking 🍳"
        }
        val unlockWaitHours = hoursRemaining - 10.0
        return if (unlockWaitHours >= 1.0) {
            "🔒 Prep Opens in ${unlockWaitHours.toInt()}h"
        } else {
            "🔒 Prep Opens in ${(unlockWaitHours * 60).toInt()}m"
        }
    }

    /**
     * Evaluates cancellation policy for any order based on delivery date, time slot, and advance paid.
     * Rules:
     * 1. Same-Day Delivery: 0% customer refund. Advance minus 10% platform fee settled to Kitchen account.
     * 2. > 24 Hours: 100% full refund to customer. Kitchen alerted with loud siren alarm to cancel cooking.
     * 3. 12 to 24 Hours: 0% cash return. Customer can reschedule within 7 days by paying remaining 50% (locked non-cancellable),
     *    OR cancel with advance transferred to Company Ads & Customer Offers Fund.
     * 4. < 12 Hours: 0% refund to customer. Advance minus 10% commission settled to Kitchen account for raw material compensation.
     */
    fun evaluateCancellationPolicy(order: OrderEntity): CancellationPolicyResult {
        val isSameDay = isToday(order.deliveryDate)
        val advance = order.advancePaidAmount
        val commission = advance * 0.10
        val kitchenSettlement = advance * 0.90

        if (isSameDay) {
            return CancellationPolicyResult(
                tier = CancellationTier.SAME_DAY_NO_REFUND,
                hoursRemaining = calculateDeliveryHoursRemaining(order.deliveryDate, order.deliveryTimeSlot),
                isSameDay = true,
                customerRefundPercentage = 0.0,
                customerRefundAmount = 0.0,
                kitchenSettlementAmount = kitchenSettlement,
                companyAdsFundAmount = commission,
                canReschedule = false,
                policyExplanation = "Same-day delivery: 0% customer refund. ₹${kitchenSettlement.toInt()} settled to kitchen minus 10% commission."
            )
        }

        val hoursRemaining = calculateDeliveryHoursRemaining(order.deliveryDate, order.deliveryTimeSlot)

        return when {
            hoursRemaining > 24.0 -> {
                CancellationPolicyResult(
                    tier = CancellationTier.MORE_THAN_24_HOURS_FULL_REFUND,
                    hoursRemaining = hoursRemaining,
                    isSameDay = false,
                    customerRefundPercentage = 100.0,
                    customerRefundAmount = advance,
                    kitchenSettlementAmount = 0.0,
                    companyAdsFundAmount = 0.0,
                    canReschedule = true,
                    policyExplanation = ">24 hours before delivery: 100% full refund (₹${advance.toInt()}). Kitchen alerted via siren to cancel."
                )
            }
            hoursRemaining >= 12.0 -> {
                CancellationPolicyResult(
                    tier = CancellationTier.BETWEEN_12_AND_24_HOURS_RESCHEDULE_OR_FORFEIT,
                    hoursRemaining = hoursRemaining,
                    isSameDay = false,
                    customerRefundPercentage = 0.0,
                    customerRefundAmount = 0.0,
                    kitchenSettlementAmount = 0.0,
                    companyAdsFundAmount = advance,
                    canReschedule = true,
                    policyExplanation = "12-24 hours before delivery: No cash refund. Reschedule within 7 days with 100% payment lock, OR forfeit advance to Company Ads & Offers Fund."
                )
            }
            else -> {
                CancellationPolicyResult(
                    tier = CancellationTier.LESS_THAN_12_HOURS_NO_REFUND,
                    hoursRemaining = hoursRemaining,
                    isSameDay = false,
                    customerRefundPercentage = 0.0,
                    customerRefundAmount = 0.0,
                    kitchenSettlementAmount = kitchenSettlement,
                    companyAdsFundAmount = commission,
                    canReschedule = false,
                    policyExplanation = "<12 hours before delivery: 0% refund to customer. ₹${kitchenSettlement.toInt()} settled to kitchen minus 10% commission for ingredients."
                )
            }
        }
    }

    /**
     * Generates date options for the next 7 days for the 7-day reschedule window.
     * Returns List of Pair(rawDateStr "yyyy-MM-dd", displayLabel "Tomorrow (Wed, 9 Sep)")
     */
    fun getNext7DaysForReschedule(): List<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()
        val rawFormatter = createDateFormat("yyyy-MM-dd")
        val displayFormatter = createDateFormat("EEE, dd MMM")
        val cal = getIndianCalendar()

        for (i in 1..7) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
            val raw = rawFormatter.format(cal.time)
            val display = when (i) {
                1 -> "Tomorrow (${displayFormatter.format(cal.time)})"
                else -> displayFormatter.format(cal.time)
            }
            list.add(Pair(raw, display))
        }
        return list
    }
}
