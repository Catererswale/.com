package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.CaterersApplication
import com.example.data.models.SettlementStatus
import com.example.data.models.WeeklySettlementSummary
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SettlementRepository(private val context: Context? = null) {

    private val tag = "SettlementRepository"
    private val collectionName = "weekly_settlements"

    private val firestore: FirebaseFirestore? by lazy {
        try {
            context?.let { CaterersApplication.initFirebase(it) }
            val apps = if (context != null) FirebaseApp.getApps(context) else emptyList()
            if (apps.isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else {
                try {
                    FirebaseFirestore.getInstance()
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            Log.w(tag, "Firestore initialization error: ${e.message}")
            null
        }
    }

    /**
     * Real-time stream of all weekly settlements stored in Firestore.
     */
    fun listenToWeeklySettlements(): Flow<List<WeeklySettlementSummary>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        var registration: ListenerRegistration? = null
        try {
            registration = db.collection(collectionName)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(tag, "Settlement listener error: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { doc ->
                            parseDocumentToSettlement(doc.data)
                        }
                        trySend(list)
                    }
                }
        } catch (e: Exception) {
            Log.e(tag, "Failed to register listener: ${e.message}")
            trySend(emptyList())
        }

        awaitClose { registration?.remove() }
    }

    /**
     * Save or update a weekly settlement record (e.g. after payout with UTR number).
     */
    fun saveSettlement(settlement: WeeklySettlementSummary, onResult: (Boolean, String?) -> Unit) {
        val db = firestore
        if (db == null) {
            onResult(false, "Firestore unavailable")
            return
        }

        val map = hashMapOf<String, Any?>(
            "settlementId" to settlement.settlementId,
            "catererId" to settlement.catererId,
            "catererName" to settlement.catererName,
            "weekStartDate" to settlement.weekStartDate,
            "weekEndDate" to settlement.weekEndDate,
            "weekLabel" to settlement.weekLabel,
            "totalOrdersCount" to settlement.totalOrdersCount,
            "grossSalesAmount" to settlement.grossSalesAmount,
            "adminCommissionPercentage" to settlement.adminCommissionPercentage,
            "adminCommissionAmount" to settlement.adminCommissionAmount,
            "totalGstAmount" to settlement.totalGstAmount,
            "deliveryChargesCollected" to settlement.deliveryChargesCollected,
            "advancePaidByCustomers" to settlement.advancePaidByCustomers,
            "balanceCollectedAtDelivery" to settlement.balanceCollectedAtDelivery,
            "netPayableToKitchen" to settlement.netPayableToKitchen,
            "settlementStatus" to settlement.settlementStatus.name,
            "payoutBankAcc" to settlement.payoutBankAcc,
            "payoutIfsc" to settlement.payoutIfsc,
            "utrTransactionNumber" to settlement.utrTransactionNumber,
            "settledAtTimestamp" to settlement.settledAtTimestamp,
            "createdAtTimestamp" to settlement.createdAtTimestamp
        )

        db.collection(collectionName).document(settlement.settlementId)
            .set(map)
            .addOnSuccessListener {
                Log.d(tag, "Settlement saved successfully: ${settlement.settlementId}")
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Error saving settlement: ${e.message}")
                onResult(false, e.message)
            }
    }

    private fun parseDocumentToSettlement(data: Map<String, Any>?): WeeklySettlementSummary? {
        if (data == null) return null
        return try {
            WeeklySettlementSummary(
                settlementId = data["settlementId"] as? String ?: "",
                catererId = data["catererId"] as? String ?: "",
                catererName = data["catererName"] as? String ?: "",
                weekStartDate = data["weekStartDate"] as? String ?: "",
                weekEndDate = data["weekEndDate"] as? String ?: "",
                weekLabel = data["weekLabel"] as? String ?: "",
                totalOrdersCount = (data["totalOrdersCount"] as? Number)?.toInt() ?: 0,
                grossSalesAmount = (data["grossSalesAmount"] as? Number)?.toDouble() ?: 0.0,
                adminCommissionPercentage = (data["adminCommissionPercentage"] as? Number)?.toDouble() ?: 10.0,
                adminCommissionAmount = (data["adminCommissionAmount"] as? Number)?.toDouble() ?: 0.0,
                totalGstAmount = (data["totalGstAmount"] as? Number)?.toDouble() ?: 0.0,
                deliveryChargesCollected = (data["deliveryChargesCollected"] as? Number)?.toDouble() ?: 0.0,
                advancePaidByCustomers = (data["advancePaidByCustomers"] as? Number)?.toDouble() ?: 0.0,
                balanceCollectedAtDelivery = (data["balanceCollectedAtDelivery"] as? Number)?.toDouble() ?: 0.0,
                netPayableToKitchen = (data["netPayableToKitchen"] as? Number)?.toDouble() ?: 0.0,
                settlementStatus = try {
                    SettlementStatus.valueOf(data["settlementStatus"] as? String ?: "PENDING")
                } catch (e: Exception) { SettlementStatus.PENDING },
                payoutBankAcc = data["payoutBankAcc"] as? String ?: "",
                payoutIfsc = data["payoutIfsc"] as? String ?: "",
                utrTransactionNumber = data["utrTransactionNumber"] as? String,
                settledAtTimestamp = (data["settledAtTimestamp"] as? Number)?.toLong(),
                createdAtTimestamp = (data["createdAtTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Log.e(tag, "Failed to parse settlement document: ${e.message}")
            null
        }
    }
}
