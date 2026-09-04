package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SettlementStatus {
    PENDING,
    PROCESSING,
    SETTLED,
    FAILED
}

/**
 * Data class representing the weekly settlement statement for a partner kitchen.
 * Cycle: Monday to Sunday (Mon 00:00:00 to Sun 23:59:59).
 */
@Entity(tableName = "weekly_settlements")
data class WeeklySettlementSummary(
    @PrimaryKey val settlementId: String,
    val catererId: String,
    val catererName: String,
    val weekStartDate: String, // e.g. "2026-07-20" (Monday)
    val weekEndDate: String,   // e.g. "2026-07-26" (Sunday)
    val weekLabel: String,     // e.g. "20 Jul - 26 Jul 2026"
    val totalOrdersCount: Int,
    val grossSalesAmount: Double,
    // Online Orders Breakdown
    val onlineOrdersCount: Int = 0,
    val onlineGrossSales: Double = 0.0,
    val onlineCommissionPercentage: Double = 10.0,
    val onlineCommissionAmount: Double = 0.0,
    // Offline Orders Breakdown
    val offlineOrdersCount: Int = 0,
    val offlineGrossSales: Double = 0.0,
    val offlineCommissionPercentage: Double = 5.0,
    val offlineCommissionAmount: Double = 0.0,
    // Overall Platform Commission
    val adminCommissionPercentage: Double = 10.0,
    val adminCommissionAmount: Double = 0.0,
    val totalGstAmount: Double = 0.0,
    val deliveryChargesCollected: Double = 0.0,
    // Payments Breakdown
    val advancePaidByCustomers: Double = 0.0, // Online Advance received by Platform
    val balanceCollectedAtDelivery: Double = 0.0, // 70% COD Balance collected by Delivery Boy -> handed to Kitchen
    val cashReceivedByKitchen: Double = 0.0, // Total Cash directly in Kitchen hand
    val netPayableToKitchen: Double, // Net Platform Bank Transfer to Kitchen (Gross - Commission - Cash In Hand)
    val settlementStatus: SettlementStatus = SettlementStatus.PENDING,
    val payoutBankAcc: String = "",
    val payoutIfsc: String = "",
    val utrTransactionNumber: String? = null,
    val settledAtTimestamp: Long? = null,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

/**
 * Platform settings for Settlement Engine execution.
 */
data class SettlementEngineConfig(
    val defaultAdminCommissionPercent: Double = 10.0,
    val weeklySettlementStartDay: Int = java.util.Calendar.MONDAY,
    val weeklySettlementEndDay: Int = java.util.Calendar.SUNDAY,
    val payoutPayoutDayOfWeek: String = "Tuesday",
    val autoApproveThresholdAmount: Double = 50000.0
)
