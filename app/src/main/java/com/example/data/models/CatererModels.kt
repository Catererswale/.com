package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    CUSTOMER,
    KITCHEN,
    DELIVERY_BOY,
    SUPER_ADMIN
}

enum class UnitType {
    KG,
    DOZEN,
    LITRE,
    PORTION
}

enum class FoodType {
    VEG,
    NON_VEG
}

enum class OrderStatus {
    NEW,
    ACCEPTED,
    CONFIRMED,
    PREPARING,
    READY,
    ASSIGNED_DELIVERY,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}

enum class PaymentMethod {
    UPI,
    CARD,
    NET_BANKING,
    WALLET,
    DYNAMIC_QR,
    CASH_ON_DELIVERY
}

enum class PaymentStatus {
    ADVANCE_PAID_30,
    ADVANCE_PAID_50,
    FULL_PAID,
    BALANCE_PENDING,
    FULLY_SETTLED,
    REFUNDED
}

data class CateringAddOn(
    val id: String,
    val name: String,
    val hindiName: String,
    val price: Double,
    val unit: String,
    val icon: String,
    val description: String,
    val servesText: String,
    val isAvailable: Boolean = true
)

enum class KycStatus {
    PENDING,
    APPROVED,
    REJECTED
}

enum class Language(val displayName: String) {
    ENGLISH("English"),
    HINDI("हिंदी (Hindi)"),
    HINGLISH("Hinglish (Mix)")
}

enum class DiscountType {
    NONE,
    FLAT,       // Fixed amount e.g. ₹50 off
    PERCENTAGE  // Percentage e.g. 10% off
}

@Entity(tableName = "caterers")
data class CatererEntity(
    @PrimaryKey val id: String,
    val name: String,
    val kitchenName: String,
    val logoUrl: String,
    val bannerUrl: String,
    val rating: Float,
    val reviewCount: Int,
    val deliveryTimeMinutes: Int,
    val minOrderAmount: Double,
    val deliveryCharge: Double,
    val distanceKm: Double,
    val fssaiLicense: String,
    val isFssaiVerified: Boolean,
    val isOpenForBooking: Boolean,
    val address: String,
    val city: String,
    val ownerMobile: String,
    val kycStatus: KycStatus = KycStatus.APPROVED,
    val aadhaarNumber: String = "",
    val panNumber: String = "",
    val bankAccount: String = "",
    val bankIfsc: String = "",
    val fssaiDocUrl: String = "fssai_cert_112233.pdf",
    val aadhaarDocUrl: String = "aadhaar_998877.jpg",
    val panDocUrl: String = "pan_abcde1234.jpg",
    val bankChequeDocUrl: String = "cancelled_cheque_9182.jpg",
    val kitchenPhotoUrl: String = "kitchen_sanitation_photo.jpg",
    val kycNotes: String = "Verified by Super Admin",
    val onlineCommissionPercentage: Double = 10.0,
    val offlineCommissionPercentage: Double = 5.0,
    val isOfflineBookingEnabled: Boolean = true,
    // Kitchen Specific Serving Capacity (1 Kg me kitne log khaenge)
    val biryaniPersonsPerKg: Double = 6.67, // e.g. 1 Kg serves ~6.7 persons (10 persons = 1.5 Kg)
    val sweetPersonsPerKg: Double = 12.5,   // e.g. 1 Kg serves ~12.5 persons (10 persons = 0.8 Kg)
    val gravyPersonsPerKg: Double = 8.33,   // e.g. 1 Kg serves ~8.3 persons (10 persons = 1.2 Kg)
    val rotiPersonsPerUnit: Double = 0.5,   // 2 rotis per person
    // Caterer choice for Add-on services: अगर सिर्फ खाना बेचना है तो false
    val offersAddonServices: Boolean = true
)

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey val id: String,
    val catererId: String,
    val catererName: String,
    val category: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val foodType: FoodType,
    val unitType: UnitType,
    val pricePerUnit: Double,
    val discountPercent: Double = 0.0,
    val minQuantity: Double = 1.0,
    val maxQuantity: Double = 50.0,
    val stepQuantity: Double = 0.5,
    val prepTimeMinutes: Int = 45,
    val isAvailable: Boolean = true,
    val isRecommended: Boolean = false,
    val isPopular: Boolean = false,
    val isApprovedByAdmin: Boolean = true,
    // Per Product Discount Settings (Caterer & Admin)
    val catererDiscountType: DiscountType = DiscountType.PERCENTAGE,
    val catererDiscountValue: Double = 0.0,
    val adminDiscountType: DiscountType = DiscountType.NONE,
    val adminDiscountValue: Double = 0.0,
    // Dish specific serving capacity (0.0 means default to caterer profile standard)
    val servesPersonsPerUnit: Double = 0.0
) {
    fun getEffectivePrice(): Double {
        var price = pricePerUnit
        // Caterer Product Level Discount
        if (catererDiscountType == DiscountType.PERCENTAGE && catererDiscountValue > 0) {
            price -= (pricePerUnit * (catererDiscountValue / 100.0))
        } else if (catererDiscountType == DiscountType.FLAT && catererDiscountValue > 0) {
            price -= catererDiscountValue
        } else if (discountPercent > 0) {
            price -= (pricePerUnit * (discountPercent / 100.0))
        }

        // Admin Product Level Discount
        if (adminDiscountType == DiscountType.PERCENTAGE && adminDiscountValue > 0) {
            price -= (pricePerUnit * (adminDiscountValue / 100.0))
        } else if (adminDiscountType == DiscountType.FLAT && adminDiscountValue > 0) {
            price -= adminDiscountValue
        }
        return kotlin.math.max(0.0, price)
    }

    fun hasDiscount(): Boolean {
        return getEffectivePrice() < pricePerUnit
    }
}

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val menuItemId: String,
    val catererId: String,
    val catererName: String,
    val name: String,
    val imageUrl: String,
    val foodType: FoodType,
    val unitType: UnitType,
    val pricePerUnit: Double,
    val quantity: Double,
    val totalPrice: Double,
    val originalPricePerUnit: Double = pricePerUnit,
    val catererDiscountType: DiscountType = DiscountType.NONE,
    val catererDiscountValue: Double = 0.0,
    val adminDiscountType: DiscountType = DiscountType.NONE,
    val adminDiscountValue: Double = 0.0
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val customerName: String,
    val customerMobile: String,
    val deliveryAddress: String,
    val catererId: String,
    val catererName: String,
    val itemsSummary: String, // JSON or formatted text
    val totalAmount: Double,
    val advancePaidAmount: Double,
    val balanceAmount: Double,
    val paymentMethod: PaymentMethod,
    val paymentStatus: PaymentStatus,
    val orderStatus: OrderStatus,
    val deliveryDate: String,
    val deliveryTimeSlot: String,
    val deliveryOtp: String = "4829",
    val deliveryBoyId: String? = null,
    val deliveryBoyName: String? = null,
    val deliveryBoyMobile: String? = null,
    val createdAtTimestamp: Long = System.currentTimeMillis(),
    val isBartanPending: Boolean = true,
    val bartanDescription: String = "Handi & Serving Trays",
    val isBartanReturned: Boolean = false,
    val userRating: Float = 0f,
    val userReview: String = "",
    // Discount & Loyalty Breakdown
    val catererOrderDiscountAmount: Double = 0.0,
    val adminOrderDiscountAmount: Double = 0.0,
    val loyaltyDiscountAmount: Double = 0.0,
    val redeemedLoyaltyPoints: Int = 0,
    val earnedLoyaltyPoints: Int = 0,
    // Offline Booking & Cash Collection Tracking
    val isOfflineBooking: Boolean = false,
    val cashCollectedByDeliveryBoy: Double = 0.0,
    val isCashSubmittedToKitchen: Boolean = false
)

@Entity(tableName = "delivery_boys")
data class DeliveryBoyEntity(
    @PrimaryKey val id: String,
    val kitchenId: String,
    val name: String,
    val mobile: String,
    val aadhaarNumber: String,
    val drivingLicence: String = "",
    val isAadhaarVerified: Boolean = true,
    val isOnline: Boolean = true,
    val isBusy: Boolean = false,
    val todayCompletedDeliveries: Int = 0,
    val cashToSubmit: Double = 0.0,
    val pendingBartanCount: Int = 0
)

@Entity(tableName = "bartan_records")
data class BartanRecordEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val customerName: String,
    val customerMobile: String,
    val customerAddress: String = "",
    val catererId: String,
    val catererName: String = "",
    val itemsDescription: String,
    val deliveryDate: String,
    val deliveryBoyId: String = "",
    val deliveryBoyName: String = "",
    val deliveryBoyMobile: String = "",
    val isCollected: Boolean = false,
    val collectedDate: String = "",
    val returnStatus: String = "PENDING", // PENDING, PICKUP_SCHEDULED, COLLECTED, RETURNED_TO_KITCHEN
    val lastMorningAlertDate: String = "",
    val morningAlertSent: Boolean = false
)

@Entity(tableName = "app_notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val targetRole: UserRole,
    val isRead: Boolean = false
)

@Entity(tableName = "user_favorites", primaryKeys = ["userId", "catererId"])
data class FavoriteKitchenEntity(
    val userId: String,
    val catererId: String,
    val catererName: String = "",
    val kitchenName: String = "",
    val rating: Float = 0f,
    val address: String = "",
    val favoritedAt: Long = System.currentTimeMillis()
)

data class UserProfile(
    val id: String = "usr_${System.currentTimeMillis()}",
    val name: String = "",
    val mobile: String = "",
    val email: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val businessName: String = "",
    val city: String = "New Delhi",
    val fssaiOrLicense: String = "",
    val isVerified: Boolean = true,
    val loyaltyPoints: Int = 150,
    val favoriteKitchenIds: List<String> = emptyList()
)

data class KitchenSettingsConfig(
    val deliveryRadiusKm: Int = 25,
    val deliveryTimeSlots: List<String> = listOf(
        "11:00 AM - 02:00 PM (Lunch)",
        "02:00 PM - 05:00 PM (Snacks/High Tea)",
        "07:00 PM - 10:30 PM (Dinner)"
    ),
    val minimumOrderValue: Double = 2000.0,
    val defaultAdvancePercentage: Int = 30,
    val gstTaxPercentage: Double = 5.0,
    val deliveryChargePerKm: Double = 15.0,
    val isKitchenOpen: Boolean = true,
    val autoSendWhatsappInvoice: Boolean = true,
    val openingTime: String = "07:00 AM",
    val closingTime: String = "11:00 PM",
    val kitchenAddress: String = "Okhla Phase 3, Jamia Nagar, New Delhi - 110025",
    val kitchenPhone: String = "+91 98765 43210",
    val allowSameDayBooking: Boolean = true,
    val sameDayPrepLeadTimeHours: Double = 2.5,
    val sameDayDeliveryStartTime: String = "08:00 AM",
    val sameDayDeliveryEndTime: String = "10:00 PM",

    // Caterer Per-Order Discount Settings (किचन डिस्काउंट)
    val catererOrderDiscountType: DiscountType = DiscountType.PERCENTAGE,
    val catererOrderDiscountValue: Double = 5.0, // e.g. 5% OFF
    val catererMinOrderForDiscount: Double = 1500.0,

    // Super Admin Per-Order Discount & Promo Settings (एडमिन प्रोमो)
    val adminOrderDiscountType: DiscountType = DiscountType.FLAT,
    val adminOrderDiscountValue: Double = 100.0, // e.g. ₹100 FLAT
    val adminPromoCode: String = "SUPER100",
    val adminMinOrderForDiscount: Double = 1000.0,

    // Super Admin Loyalty Points Settings (एडमिन लॉयल्टी पॉइंट्स)
    val isLoyaltyEnabled: Boolean = true,
    val loyaltyEarnPointsPer100Rs: Int = 2, // Earn 2 points per ₹100 spent
    val loyaltyPointRupeeValue: Double = 1.0, // 1 Point = ₹1.00
    val maxLoyaltyRedeemPercent: Int = 20, // Max 20% of order value redeemable

    // Kitchen Food Serving Capacity Settings (1 Kg me kitne log khaenge)
    val biryaniPersonsPerKg: Double = 6.67, // 1 Kg serves ~6.7 persons
    val sweetPersonsPerKg: Double = 12.5,   // 1 Kg serves ~12.5 persons
    val gravyPersonsPerKg: Double = 8.33,   // 1 Kg serves ~8.3 persons
    val rotiPersonsPerUnit: Double = 0.5,   // 2 rotis per person

    // Caterer choice: Offer event add-on services or pure food only
    val offersAddonServices: Boolean = true
)

@Entity(tableName = "partner_reviews")
data class PartnerReviewEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val catererId: String,
    val catererName: String,
    val customerName: String,
    val customerMobile: String,
    val overallRating: Float, // 1.0 to 5.0
    val tasteRating: Float = 5.0f,
    val portionRating: Float = 5.0f,
    val packagingRating: Float = 5.0f,
    val deliveryRating: Float = 5.0f,
    val bartanRating: Float = 5.0f,
    val tagsCsv: String = "", // Comma-separated tags e.g. "Authentic Dum,Generous Serving"
    val eventType: String = "Family Gathering", // e.g. Wedding, Birthday, Corporate, House Party, Dawat
    val comment: String = "",
    val dishRatingsJson: String = "", // e.g. "Chicken Biryani: 5★, Kheer: 5★"
    val isRecommended: Boolean = true,
    val tipAmount: Double = 0.0,
    val isAnonymous: Boolean = false,
    val photosCount: Int = 0,
    val createdAtTimestamp: Long = System.currentTimeMillis(),
    val kitchenResponse: String = "",
    val kitchenResponseDate: String = ""
) {
    val tagList: List<String>
        get() = if (tagsCsv.isBlank()) emptyList() else tagsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}

