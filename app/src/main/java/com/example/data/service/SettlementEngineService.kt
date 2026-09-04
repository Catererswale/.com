package com.example.data.service

import com.example.data.models.CatererEntity
import com.example.data.models.OrderEntity
import com.example.data.models.SettlementStatus
import com.example.data.models.WeeklySettlementSummary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object SettlementEngineService {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private val displayFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

    /**
     * Calculates Monday (00:00:00) and Sunday (23:59:59) boundaries for any given target calendar date.
     */
    fun getWeekBoundaries(referenceDate: Calendar = Calendar.getInstance()): Pair<Calendar, Calendar> {
        val startCal = referenceDate.clone() as Calendar
        startCal.firstDayOfWeek = Calendar.MONDAY
        startCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        startCal.set(Calendar.HOUR_OF_DAY, 0)
        startCal.set(Calendar.MINUTE, 0)
        startCal.set(Calendar.SECOND, 0)
        startCal.set(Calendar.MILLISECOND, 0)

        val endCal = startCal.clone() as Calendar
        endCal.add(Calendar.DAY_OF_WEEK, 6)
        endCal.set(Calendar.HOUR_OF_DAY, 23)
        endCal.set(Calendar.MINUTE, 59)
        endCal.set(Calendar.SECOND, 59)
        endCal.set(Calendar.MILLISECOND, 999)

        return Pair(startCal, endCal)
    }

    /**
     * Calculates weekly settlement summary for a specific partner kitchen.
     * Aggregates Mon-Sun orders, applies admin commission percentage, and calculates net payable.
     */
    fun calculateKitchenWeeklySettlement(
        caterer: CatererEntity,
        orders: List<OrderEntity>,
        adminCommissionPercent: Double = 10.0,
        referenceDate: Calendar = Calendar.getInstance(),
        existingSettlementMap: Map<String, WeeklySettlementSummary> = emptyMap()
    ): WeeklySettlementSummary {
        val (startCal, endCal) = getWeekBoundaries(referenceDate)

        val weekStartDateStr = dateFormatter.format(startCal.time)
        val weekEndDateStr = dateFormatter.format(endCal.time)
        val weekLabelStr = "${displayFormatter.format(startCal.time)} - ${displayFormatter.format(endCal.time)}"

        val settlementId = "SETTLE_${caterer.id}_${weekStartDateStr}"

        // Check if settlement was already saved/settled in Firestore or Room
        val existing = existingSettlementMap[settlementId]

        // Filter orders for this kitchen within the Monday-Sunday timeframe
        val kitchenOrders = orders.filter { order ->
            if (order.catererId != caterer.id) return@filter false
            
            // Try parsing deliveryDate first, or fall back to createdAtTimestamp
            val orderTimeMilis = try {
                val parsedDate = dateFormatter.parse(order.deliveryDate)
                parsedDate?.time ?: order.createdAtTimestamp
            } catch (e: Exception) {
                order.createdAtTimestamp
            }

            orderTimeMilis in startCal.timeInMillis..endCal.timeInMillis
        }

        val totalOrdersCount = kitchenOrders.size
        val onlineOrders = kitchenOrders.filter { !it.isOfflineBooking }
        val offlineOrders = kitchenOrders.filter { it.isOfflineBooking }

        val onlineOrdersCount = onlineOrders.size
        val offlineOrdersCount = offlineOrders.size

        val onlineGrossSales = onlineOrders.sumOf { it.totalAmount }
        val offlineGrossSales = offlineOrders.sumOf { it.totalAmount }
        val grossSalesAmount = onlineGrossSales + offlineGrossSales

        val catererOnlineRate = if (caterer.onlineCommissionPercentage > 0) caterer.onlineCommissionPercentage else adminCommissionPercent
        val catererOfflineRate = if (caterer.offlineCommissionPercentage > 0) caterer.offlineCommissionPercentage else 5.0

        val onlineCommissionAmount = onlineGrossSales * (catererOnlineRate / 100.0)
        val offlineCommissionAmount = offlineGrossSales * (catererOfflineRate / 100.0)
        val adminCommissionAmount = onlineCommissionAmount + offlineCommissionAmount

        // Online Advance held by Platform
        val advancePaidByCustomers = onlineOrders.sumOf { it.advancePaidAmount }
        // 70% / Balance Cash collected at Delivery by Delivery Boy and handed over to Kitchen
        val balanceCollectedAtDelivery = onlineOrders.sumOf { it.balanceAmount }
        // Direct cash received by kitchen from offline bookings
        val offlineCashReceived = offlineGrossSales
        // Total cash already in Kitchen's hand
        val totalCashInKitchenHand = balanceCollectedAtDelivery + offlineCashReceived

        val deliveryChargesCollected = kitchenOrders.sumOf { caterer.deliveryCharge }
        val gstAmount = grossSalesAmount * 0.05 // 5% GST

        // Net Payout from Platform to Kitchen Bank Account:
        // Kitchen total entitlement = Gross Sales - Admin Commission
        // Minus Cash already in kitchen's possession (handed by Delivery Boy or direct offline cash)
        val netPayableToKitchen = (grossSalesAmount - adminCommissionAmount) - totalCashInKitchenHand

        val blendedCommissionRate = if (grossSalesAmount > 0) {
            (adminCommissionAmount / grossSalesAmount) * 100.0
        } else {
            catererOnlineRate
        }

        return WeeklySettlementSummary(
            settlementId = settlementId,
            catererId = caterer.id,
            catererName = caterer.kitchenName.ifBlank { caterer.name },
            weekStartDate = weekStartDateStr,
            weekEndDate = weekEndDateStr,
            weekLabel = weekLabelStr,
            totalOrdersCount = totalOrdersCount,
            grossSalesAmount = grossSalesAmount,
            onlineOrdersCount = onlineOrdersCount,
            onlineGrossSales = onlineGrossSales,
            onlineCommissionPercentage = catererOnlineRate,
            onlineCommissionAmount = onlineCommissionAmount,
            offlineOrdersCount = offlineOrdersCount,
            offlineGrossSales = offlineGrossSales,
            offlineCommissionPercentage = catererOfflineRate,
            offlineCommissionAmount = offlineCommissionAmount,
            adminCommissionPercentage = blendedCommissionRate,
            adminCommissionAmount = adminCommissionAmount,
            totalGstAmount = gstAmount,
            deliveryChargesCollected = deliveryChargesCollected,
            advancePaidByCustomers = advancePaidByCustomers,
            balanceCollectedAtDelivery = balanceCollectedAtDelivery,
            cashReceivedByKitchen = totalCashInKitchenHand,
            netPayableToKitchen = netPayableToKitchen,
            settlementStatus = existing?.settlementStatus ?: SettlementStatus.PENDING,
            payoutBankAcc = caterer.bankAccount,
            payoutIfsc = caterer.bankIfsc,
            utrTransactionNumber = existing?.utrTransactionNumber,
            settledAtTimestamp = existing?.settledAtTimestamp,
            createdAtTimestamp = existing?.createdAtTimestamp ?: System.currentTimeMillis()
        )
    }

    /**
     * Aggregates weekly settlements for all partner kitchens registered in the system.
     */
    fun aggregateAllKitchenSettlements(
        caterers: List<CatererEntity>,
        orders: List<OrderEntity>,
        adminCommissionPercent: Double = 10.0,
        referenceDate: Calendar = Calendar.getInstance(),
        existingSettlements: List<WeeklySettlementSummary> = emptyList()
    ): List<WeeklySettlementSummary> {
        val existingMap = existingSettlements.associateBy { it.settlementId }
        return caterers.map { caterer ->
            calculateKitchenWeeklySettlement(
                caterer = caterer,
                orders = orders,
                adminCommissionPercent = adminCommissionPercent,
                referenceDate = referenceDate,
                existingSettlementMap = existingMap
            )
        }
    }
}
