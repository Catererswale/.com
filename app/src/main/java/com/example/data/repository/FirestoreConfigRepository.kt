package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.CaterersApplication
import com.example.data.models.KitchenSettingsConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirestoreConfigRepository(private val context: Context? = null) {

    private val tag = "FirestoreConfigRepo"
    private val collectionName = "kitchen_config"
    private val docName = "settings"

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
            Log.w(tag, "FirebaseFirestore unavailable: ${e.message}")
            null
        }
    }

    /**
     * Real-time flow listening for changes in Firestore kitchen configuration.
     */
    fun listenToKitchenSettings(): Flow<KitchenSettingsConfig?> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        var registration: ListenerRegistration? = null
        try {
            registration = db.collection(collectionName).document(docName)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(tag, "Firestore snapshot listener error: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val config = parseSnapshotToConfig(snapshot.data)
                        trySend(config)
                    }
                }
        } catch (e: Exception) {
            Log.e(tag, "Error setting up listener: ${e.message}")
            trySend(null)
        }

        awaitClose {
            registration?.remove()
        }
    }

    /**
     * Saves or updates kitchen settings configuration in Firebase Firestore.
     */
    fun saveKitchenSettings(config: KitchenSettingsConfig, onResult: (Boolean, String?) -> Unit) {
        val db = firestore
        if (db == null) {
            onResult(false, "Firebase Firestore is not initialized.")
            return
        }

        val dataMap = hashMapOf<String, Any?>(
            "isKitchenOpen" to config.isKitchenOpen,
            "autoSendWhatsappInvoice" to config.autoSendWhatsappInvoice,
            "minimumOrderValue" to config.minimumOrderValue,
            "defaultAdvancePercentage" to config.defaultAdvancePercentage,
            "gstTaxPercentage" to config.gstTaxPercentage,
            "deliveryRadiusKm" to config.deliveryRadiusKm,
            "deliveryChargePerKm" to config.deliveryChargePerKm,
            "openingTime" to config.openingTime,
            "closingTime" to config.closingTime,
            "kitchenAddress" to config.kitchenAddress,
            "kitchenPhone" to config.kitchenPhone,
            "deliveryTimeSlots" to config.deliveryTimeSlots,
            "allowSameDayBooking" to config.allowSameDayBooking,
            "sameDayPrepLeadTimeHours" to config.sameDayPrepLeadTimeHours,
            "sameDayDeliveryStartTime" to config.sameDayDeliveryStartTime,
            "sameDayDeliveryEndTime" to config.sameDayDeliveryEndTime,
            // Discounts & Loyalty
            "catererOrderDiscountType" to config.catererOrderDiscountType.name,
            "catererOrderDiscountValue" to config.catererOrderDiscountValue,
            "catererMinOrderForDiscount" to config.catererMinOrderForDiscount,
            "adminOrderDiscountType" to config.adminOrderDiscountType.name,
            "adminOrderDiscountValue" to config.adminOrderDiscountValue,
            "adminPromoCode" to config.adminPromoCode,
            "adminMinOrderForDiscount" to config.adminMinOrderForDiscount,
            "isLoyaltyEnabled" to config.isLoyaltyEnabled,
            "loyaltyEarnPointsPer100Rs" to config.loyaltyEarnPointsPer100Rs,
            "loyaltyPointRupeeValue" to config.loyaltyPointRupeeValue,
            "maxLoyaltyRedeemPercent" to config.maxLoyaltyRedeemPercent,
            "offersAddonServices" to config.offersAddonServices,
            "lastUpdatedTimestamp" to System.currentTimeMillis()
        )

        db.collection(collectionName).document(docName)
            .set(dataMap)
            .addOnSuccessListener {
                Log.d(tag, "Successfully saved settings to Firestore")
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Failed to save settings to Firestore: ${e.message}")
                onResult(false, e.message)
            }
    }

    private fun parseSnapshotToConfig(data: Map<String, Any>?): KitchenSettingsConfig? {
        if (data == null) return null
        return try {
            KitchenSettingsConfig(
                isKitchenOpen = data["isKitchenOpen"] as? Boolean ?: true,
                autoSendWhatsappInvoice = data["autoSendWhatsappInvoice"] as? Boolean ?: true,
                minimumOrderValue = (data["minimumOrderValue"] as? Number)?.toDouble() ?: 500.0,
                defaultAdvancePercentage = (data["defaultAdvancePercentage"] as? Number)?.toInt() ?: 30,
                gstTaxPercentage = (data["gstTaxPercentage"] as? Number)?.toDouble() ?: 5.0,
                deliveryRadiusKm = (data["deliveryRadiusKm"] as? Number)?.toInt() ?: 25,
                deliveryChargePerKm = (data["deliveryChargePerKm"] as? Number)?.toDouble() ?: 15.0,
                openingTime = data["openingTime"] as? String ?: "07:00 AM",
                closingTime = data["closingTime"] as? String ?: "11:00 PM",
                kitchenAddress = data["kitchenAddress"] as? String ?: "Okhla Phase 3, Jamia Nagar, New Delhi - 110025",
                kitchenPhone = data["kitchenPhone"] as? String ?: "+91 98765 43210",
                deliveryTimeSlots = (data["deliveryTimeSlots"] as? List<*>)?.filterIsInstance<String>() ?: listOf(
                    "11:00 AM - 02:00 PM (Lunch)",
                    "02:00 PM - 05:00 PM (Snacks/High Tea)",
                    "07:00 PM - 10:30 PM (Dinner)"
                ),
                allowSameDayBooking = data["allowSameDayBooking"] as? Boolean ?: true,
                sameDayPrepLeadTimeHours = (data["sameDayPrepLeadTimeHours"] as? Number)?.toDouble() ?: 2.5,
                sameDayDeliveryStartTime = data["sameDayDeliveryStartTime"] as? String ?: "08:00 AM",
                sameDayDeliveryEndTime = data["sameDayDeliveryEndTime"] as? String ?: "10:00 PM",
                catererOrderDiscountType = try {
                    com.example.data.models.DiscountType.valueOf(data["catererOrderDiscountType"] as? String ?: "PERCENTAGE")
                } catch (e: Exception) { com.example.data.models.DiscountType.PERCENTAGE },
                catererOrderDiscountValue = (data["catererOrderDiscountValue"] as? Number)?.toDouble() ?: 5.0,
                catererMinOrderForDiscount = (data["catererMinOrderForDiscount"] as? Number)?.toDouble() ?: 1500.0,
                adminOrderDiscountType = try {
                    com.example.data.models.DiscountType.valueOf(data["adminOrderDiscountType"] as? String ?: "FLAT")
                } catch (e: Exception) { com.example.data.models.DiscountType.FLAT },
                adminOrderDiscountValue = (data["adminOrderDiscountValue"] as? Number)?.toDouble() ?: 100.0,
                adminPromoCode = data["adminPromoCode"] as? String ?: "SUPER100",
                adminMinOrderForDiscount = (data["adminMinOrderForDiscount"] as? Number)?.toDouble() ?: 1000.0,
                isLoyaltyEnabled = data["isLoyaltyEnabled"] as? Boolean ?: true,
                loyaltyEarnPointsPer100Rs = (data["loyaltyEarnPointsPer100Rs"] as? Number)?.toInt() ?: 2,
                loyaltyPointRupeeValue = (data["loyaltyPointRupeeValue"] as? Number)?.toDouble() ?: 1.0,
                maxLoyaltyRedeemPercent = (data["maxLoyaltyRedeemPercent"] as? Number)?.toInt() ?: 20,
                offersAddonServices = data["offersAddonServices"] as? Boolean ?: true
            )
        } catch (e: Exception) {
            Log.e(tag, "Error parsing Firestore document: ${e.message}")
            null
        }
    }
}
