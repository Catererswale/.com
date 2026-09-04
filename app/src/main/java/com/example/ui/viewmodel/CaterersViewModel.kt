package com.example.data.repository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CaterersDatabase
import com.example.data.models.BartanRecordEntity
import com.example.data.models.CartItemEntity
import com.example.data.models.CatererEntity
import com.example.data.models.CateringAddOn
import com.example.data.models.DeliveryBoyEntity
import com.example.data.models.FoodType
import com.example.data.models.KycStatus
import com.example.data.models.Language
import com.example.data.models.MenuItemEntity
import com.example.data.models.NotificationEntity
import com.example.data.models.OrderEntity
import com.example.data.models.OrderStatus
import com.example.data.models.PartnerReviewEntity
import com.example.data.models.PaymentMethod
import com.example.data.models.UserProfile
import com.example.data.models.UserRole
import com.example.data.models.KitchenSettingsConfig
import com.example.data.models.SettlementStatus
import com.example.data.models.WeeklySettlementSummary
import com.example.data.repository.CaterersRepository
import com.example.data.repository.FirestoreConfigRepository
import com.example.data.repository.FirestoreFavoritesRepository
import com.example.data.repository.SettlementRepository
import com.example.data.service.SettlementEngineService
import com.example.util.NotificationHelper
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class CaterersViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CaterersDatabase.getInstance(application)
    val repository = CaterersRepository(db.caterersDao())
    private val firestoreConfigRepository = FirestoreConfigRepository(application)
    private val settlementRepository = SettlementRepository(application)
    private val firestoreFavoritesRepository = FirestoreFavoritesRepository(application)

    private val _firestoreSettlements = MutableStateFlow<List<WeeklySettlementSummary>>(emptyList())
    private val _adminCommissionPercent = MutableStateFlow(10.0)
    val adminCommissionPercent: StateFlow<Double> = _adminCommissionPercent.asStateFlow()

    // Favorites State
    private val _favoriteKitchenIds = MutableStateFlow<Set<String>>(setOf("caterer_1"))
    val favoriteKitchenIds: StateFlow<Set<String>> = _favoriteKitchenIds.asStateFlow()

    init {
        viewModelScope.launch {
            firestoreConfigRepository.listenToKitchenSettings().collect { remoteConfig ->
                if (remoteConfig != null) {
                    _kitchenSettings.value = remoteConfig
                }
            }
        }

        viewModelScope.launch {
            settlementRepository.listenToWeeklySettlements().collect { remoteSettlements ->
                _firestoreSettlements.value = remoteSettlements
            }
        }

        // Listen to local Room favorites for default user
        viewModelScope.launch {
            repository.getFavoriteCatererIds("cust_1").collect { ids ->
                if (ids.isNotEmpty()) {
                    _favoriteKitchenIds.value = ids.toSet()
                }
            }
        }

        // Listen to Firestore favorites for default user
        viewModelScope.launch {
            firestoreFavoritesRepository.listenToUserFavorites("cust_1").collect { remoteFavs ->
                if (remoteFavs.isNotEmpty()) {
                    _favoriteKitchenIds.value = remoteFavs.toSet()
                }
            }
        }
    }

    // Active Role State
    private val _currentRole = MutableStateFlow(UserRole.CUSTOMER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Auth & User Profile State
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<UserProfile?>(
        UserProfile("cust_1", "Rohan Verma", "+91 98765 43210", "rohan@example.com", UserRole.CUSTOMER, city = "New Delhi")
    )
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    // Language State (English, Hindi, Hinglish)
    private val _currentLanguage = MutableStateFlow(Language.ENGLISH)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    // Search Query & Category Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Delivery Location
    private val _deliveryLocation = MutableStateFlow("Okhla Phase 3, New Delhi")
    val deliveryLocation: StateFlow<String> = _deliveryLocation.asStateFlow()

    // Selected Caterer for Detail View
    private val _selectedCatererId = MutableStateFlow<String?>("caterer_1")
    val selectedCatererId: StateFlow<String?> = _selectedCatererId.asStateFlow()

    // Partner Reviews StateFlow
    val allPartnerReviews: StateFlow<List<PartnerReviewEntity>> = repository.allReviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Live Tracking Order ID
    private val _activeTrackingOrderId = MutableStateFlow<String?>("CW-89210")
    val activeTrackingOrderId: StateFlow<String?> = _activeTrackingOrderId.asStateFlow()

    // System Toggles (Super Admin / Global)
    private val _isGlobalBookingOn = MutableStateFlow(true)
    val isGlobalBookingOn: StateFlow<Boolean> = _isGlobalBookingOn.asStateFlow()

    private val _isSameDayBookingOn = MutableStateFlow(true)
    val isSameDayBookingOn: StateFlow<Boolean> = _isSameDayBookingOn.asStateFlow()

    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    // Kitchen Configuration & Settings
    private val _kitchenSettings = MutableStateFlow(KitchenSettingsConfig())
    val kitchenSettings: StateFlow<KitchenSettingsConfig> = _kitchenSettings.asStateFlow()

    // Customer Loyalty Points & Applied Promo Code State
    private val _userLoyaltyPoints = MutableStateFlow(250) // Default demo points balance
    val userLoyaltyPoints: StateFlow<Int> = _userLoyaltyPoints.asStateFlow()

    private val _appliedAdminPromoCode = MutableStateFlow("")
    val appliedAdminPromoCode: StateFlow<String> = _appliedAdminPromoCode.asStateFlow()

    fun applyAdminPromoCode(code: String): Boolean {
        val config = kitchenSettings.value
        return if (code.trim().equals(config.adminPromoCode.trim(), ignoreCase = true)) {
            _appliedAdminPromoCode.value = config.adminPromoCode
            showFeedback("Promo Code ${config.adminPromoCode} Applied Successfully! 🎉")
            true
        } else {
            showFeedback("Invalid Promo Code. Try '${config.adminPromoCode}'")
            false
        }
    }

    fun removeAdminPromoCode() {
        _appliedAdminPromoCode.value = ""
        showFeedback("Promo Code Removed")
    }

    fun deductLoyaltyPoints(points: Int) {
        if (points > 0) {
            _userLoyaltyPoints.value = maxOf(0, _userLoyaltyPoints.value - points)
        }
    }

    fun awardLoyaltyPoints(points: Int) {
        if (points > 0) {
            _userLoyaltyPoints.value += points
        }
    }

    // UI Toast / Alert Message
    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    // Database Reactive Flows
    val caterersList: StateFlow<List<CatererEntity>> = repository.allCaterers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val menuItemsList: StateFlow<List<MenuItemEntity>> = repository.allMenuItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItemsList: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ordersList: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deliveryBoysList: StateFlow<List<DeliveryBoyEntity>> = repository.allDeliveryBoys
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bartanRecordsList: StateFlow<List<BartanRecordEntity>> = repository.allBartanRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notificationsList: StateFlow<List<NotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Weekly Settlement StateFlow (Mon-Sun aggregated via SettlementEngineService)
    val weeklySettlements: StateFlow<List<WeeklySettlementSummary>> = combine(
        caterersList,
        ordersList,
        _adminCommissionPercent,
        _firestoreSettlements
    ) { caterers, orders, commission, remoteSettlements ->
        SettlementEngineService.aggregateAllKitchenSettlements(
            caterers = caterers,
            orders = orders,
            adminCommissionPercent = commission,
            existingSettlements = remoteSettlements
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Caterers based on search & category
    val filteredCaterers: StateFlow<List<CatererEntity>> = combine(
        caterersList,
        _searchQuery,
        _selectedCategory,
        _favoriteKitchenIds
    ) { list, query, category, favorites ->
        var result = list
        if (category.equals("Favorites", ignoreCase = true) || category.contains("Favorite", ignoreCase = true)) {
            result = result.filter { favorites.contains(it.id) }
        }
        if (query.isNotBlank()) {
            result = result.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.kitchenName.contains(query, ignoreCase = true) ||
                it.address.contains(query, ignoreCase = true)
            }
        }
        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Favorited Caterers Flow
    val favoriteCaterersList: StateFlow<List<CatererEntity>> = combine(
        caterersList,
        _favoriteKitchenIds
    ) { list, favIds ->
        list.filter { favIds.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart Total Amount
    val cartTotalAmount: StateFlow<Double> = cartItemsList.combine(cartItemsList) { items, _ ->
        items.sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun switchRole(role: UserRole) {
        _currentRole.value = role
        showFeedback("Switched to ${role.name.replace("_", " ")} mode")
    }

    fun switchLanguage(lang: Language) {
        _currentLanguage.value = lang
        showFeedback("Language changed to ${lang.name}")
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setDeliveryLocation(location: String) {
        _deliveryLocation.value = location
    }

    fun selectCaterer(catererId: String) {
        _selectedCatererId.value = catererId
    }

    fun setActiveTrackingOrderId(orderId: String) {
        _activeTrackingOrderId.value = orderId
    }

    fun toggleOfflineMode(enabled: Boolean) {
        _isOfflineMode.value = enabled
        showFeedback(if (enabled) "Offline Mode Enabled (Local cache active)" else "Online Mode Restored")
    }

    fun toggleGlobalBooking(enabled: Boolean) {
        _isGlobalBookingOn.value = enabled
        showFeedback(if (enabled) "Global Booking Enabled" else "Global Booking Suspended")
    }

    fun toggleSameDayBooking(enabled: Boolean) {
        _isSameDayBookingOn.value = enabled
        showFeedback(if (enabled) "Same-Day Booking Enabled" else "Same-Day Booking Disabled")
    }

    fun addToCart(item: MenuItemEntity, quantity: Double) {
        viewModelScope.launch {
            repository.addToCart(item, quantity)
            showFeedback("Added ${quantity} ${item.unitType.name} of ${item.name} to Cart")
        }
    }

    fun addAddOnService(addOn: CateringAddOn, quantity: Double) {
        viewModelScope.launch {
            val firstItem = cartItemsList.value.firstOrNull()
            val catId = firstItem?.catererId ?: selectedCatererId.value ?: "caterer_1"
            val catName = firstItem?.catererName ?: "Royal Catering Partner"
            repository.addCustomAddOnToCart(
                catererId = catId,
                catererName = catName,
                addOnId = addOn.id,
                name = addOn.name,
                price = addOn.price,
                qty = quantity,
                foodType = FoodType.VEG
            )
            showFeedback("Added ${addOn.name} to Cart ✨")
        }
    }

    fun removeAddOnService(addOnId: String) {
        viewModelScope.launch {
            repository.removeCartItem("cart_addon_$addOnId")
            showFeedback("Add-on service removed from Cart")
        }
    }

    fun removeCartItem(id: String) {
        viewModelScope.launch {
            repository.removeCartItem(id)
            showFeedback("Item removed from Cart")
        }
    }

    fun placeOrder(
        customerName: String,
        customerMobile: String,
        address: String,
        catererId: String,
        catererName: String,
        itemsSummary: String,
        totalAmount: Double,
        is30PercentAdvance: Boolean,
        paymentMethod: PaymentMethod,
        deliveryDate: String,
        deliveryTimeSlot: String,
        catererDiscountAmount: Double = 0.0,
        adminDiscountAmount: Double = 0.0,
        loyaltyDiscountAmount: Double = 0.0,
        redeemedLoyaltyPoints: Int = 0,
        earnedLoyaltyPoints: Int = 0,
        isOfflineBooking: Boolean = false,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val orderId = repository.createOrder(
                customerName = customerName,
                customerMobile = customerMobile,
                deliveryAddress = address,
                catererId = catererId,
                catererName = catererName,
                itemsSummary = itemsSummary,
                totalAmount = totalAmount,
                is30PercentAdvance = is30PercentAdvance,
                paymentMethod = paymentMethod,
                deliveryDate = deliveryDate,
                deliveryTimeSlot = deliveryTimeSlot,
                catererDiscountAmount = catererDiscountAmount,
                adminDiscountAmount = adminDiscountAmount,
                loyaltyDiscountAmount = loyaltyDiscountAmount,
                redeemedLoyaltyPoints = redeemedLoyaltyPoints,
                earnedLoyaltyPoints = earnedLoyaltyPoints,
                isOfflineBooking = isOfflineBooking
            )
            _activeTrackingOrderId.value = orderId

            // Deduct redeemed loyalty points and add earned points
            if (redeemedLoyaltyPoints > 0) {
                deductLoyaltyPoints(redeemedLoyaltyPoints)
            }
            if (earnedLoyaltyPoints > 0) {
                awardLoyaltyPoints(earnedLoyaltyPoints)
            }
            removeAdminPromoCode()

            // Trigger system push notifications
            NotificationHelper.showSystemNotification(
                context = getApplication(),
                title = "Order Placed Successfully! 🎉",
                message = "Order $orderId placed with $catererName for $deliveryDate ($deliveryTimeSlot)."
            )
            NotificationHelper.showSystemNotification(
                context = getApplication(),
                title = "New Order Received! 🛎️",
                message = "New order $orderId received for ₹${totalAmount.toInt()} from $customerName."
            )

            showFeedback("Order $orderId placed successfully! 🎉")
            onSuccess(orderId)
        }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status, getApplication())
            showFeedback("Order $orderId status updated to ${status.name}")
        }
    }

    fun assignDeliveryBoy(orderId: String, boy: DeliveryBoyEntity) {
        viewModelScope.launch {
            repository.assignDeliveryBoy(orderId, boy, getApplication())
            showFeedback("Assigned ${boy.name} to Order $orderId")
        }
    }

    fun verifyDeliveryOtp(orderId: String, enteredOtp: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.verifyOtpAndCompleteDelivery(orderId, enteredOtp, getApplication())
            if (success) {
                showFeedback("OTP Verified! Order $orderId Delivered Successfully ✅")
            } else {
                showFeedback("Invalid 4-Digit OTP! Please re-check with Customer.")
            }
            onResult(success)
        }
    }

    fun toggleKitchenBookingStatus(catererId: String, isOpen: Boolean) {
        viewModelScope.launch {
            repository.updateCatererBookingStatus(catererId, isOpen)
            showFeedback("Kitchen status changed to ${if (isOpen) "OPEN" else "CLOSED"}")
        }
    }

    fun addMenuItem(item: MenuItemEntity) {
        viewModelScope.launch {
            repository.addMenuItem(item)
            showFeedback("New Menu Item '${item.name}' added successfully")
        }
    }

    fun updateMenuItem(item: MenuItemEntity) {
        viewModelScope.launch {
            repository.updateMenuItem(item)
            showFeedback("Menu item '${item.name}' updated successfully")
        }
    }

    fun deleteMenuItem(id: String) {
        viewModelScope.launch {
            repository.deleteMenuItem(id)
            showFeedback("Menu item removed")
        }
    }

    fun updateCatererKycStatus(catererId: String, status: KycStatus, notes: String) {
        viewModelScope.launch {
            repository.updateCatererKycStatus(catererId, status, notes)
            showFeedback("KYC Status updated to ${status.name}")
        }
    }

    fun updateCatererDocuments(
        catererId: String,
        fssaiDoc: String,
        aadhaarDoc: String,
        panDoc: String,
        bankChequeDoc: String,
        kitchenPhotoDoc: String
    ) {
        viewModelScope.launch {
            repository.updateCatererDocuments(catererId, fssaiDoc, aadhaarDoc, panDoc, bankChequeDoc, kitchenPhotoDoc)
            showFeedback("Documents uploaded and submitted for Admin verification 📄✅")
        }
    }

    fun updateCatererDetails(caterer: CatererEntity) {
        viewModelScope.launch {
            repository.updateCatererDetails(caterer)
            showFeedback("Kitchen details updated successfully")
        }
    }

    fun addKitchenPartner(caterer: CatererEntity, onSuccess: (CatererEntity) -> Unit = {}) {
        viewModelScope.launch {
            repository.addCaterer(caterer)
            showFeedback("Kitchen Partner '${caterer.name}' registered successfully! 🎉")
            onSuccess(caterer)
        }
    }

    fun addDeliveryBoy(boy: DeliveryBoyEntity) {
        viewModelScope.launch {
            repository.addDeliveryBoy(boy)
            showFeedback("Delivery Boy ${boy.name} registered")
        }
    }

    fun markBartanCollected(id: String, date: String) {
        viewModelScope.launch {
            repository.updateBartanCollected(id, true, date)
            showFeedback("Bartan marked as collected ✅")
        }
    }

    fun submitReview(orderId: String, rating: Float, review: String) {
        viewModelScope.launch {
            repository.submitOrderReview(orderId, rating, review)
            showFeedback("Thank you! Rating & Review submitted ⭐")
        }
    }

    fun getReviewsForCaterer(catererId: String) = repository.getReviewsByCaterer(catererId)

    fun submitDetailedReview(review: PartnerReviewEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.submitDetailedReview(review, getApplication())
            _userLoyaltyPoints.value += 50 // Reward 50 loyalty points for review
            showFeedback("Thank you! Review & Rating submitted for ${review.catererName} (+50 Points Earned) 🎉⭐")
            onComplete()
        }
    }

    fun loginUser(profile: UserProfile) {
        _currentUser.value = profile
        _currentRole.value = profile.role
        _isLoggedIn.value = true
        
        // Sync favorites for logged-in user
        viewModelScope.launch {
            repository.getFavoriteCatererIds(profile.id).collect { ids ->
                if (ids.isNotEmpty()) {
                    _favoriteKitchenIds.value = ids.toSet()
                }
            }
        }
        viewModelScope.launch {
            firestoreFavoritesRepository.listenToUserFavorites(profile.id).collect { remoteFavs ->
                if (remoteFavs.isNotEmpty()) {
                    _favoriteKitchenIds.value = remoteFavs.toSet()
                }
            }
        }

        showFeedback("Welcome back, ${profile.name}! Logged in as ${profile.role.name.replace("_", " ")} 🎉")
    }

    fun signUpUser(profile: UserProfile) {
        _currentUser.value = profile
        _currentRole.value = profile.role
        _isLoggedIn.value = true
        showFeedback("Account created successfully! Welcome ${profile.name} 🚀")
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentUser.value = null
        showFeedback("Logged out successfully 🔒")
    }

    fun quickDemoLogin(role: UserRole) {
        val demoProfile = when (role) {
            UserRole.CUSTOMER -> UserProfile("cust_1", "Rohan Verma", "+91 98765 43210", "rohan@example.com", UserRole.CUSTOMER, city = "New Delhi")
            UserRole.KITCHEN -> UserProfile("kit_1", "A1 Huma Caterers", "+91 98111 22334", "huma@caterers.com", UserRole.KITCHEN, businessName = "A1 Huma Caterers", city = "Okhla Phase 3", fssaiOrLicense = "23319008000123")
            UserRole.DELIVERY_BOY -> UserProfile("del_1", "Amit Kumar", "+91 99887 76655", "amit@delivery.com", UserRole.DELIVERY_BOY, businessName = "Bike Delivery #402", city = "Delhi NCR")
            UserRole.SUPER_ADMIN -> UserProfile("adm_1", "Super Admin", "+91 88000 00000", "admin@catererswale.com", UserRole.SUPER_ADMIN, businessName = "CaterersWale HQ")
        }
        loginUser(demoProfile)
    }

    fun updateAdminCommission(percent: Double) {
        _adminCommissionPercent.value = percent
        showFeedback("✅ Admin Commission set to $percent%")
    }

    fun updateCatererCommissionAndOfflineSettings(
        catererId: String,
        onlineCommission: Double,
        offlineCommission: Double,
        isOnlineEnabled: Boolean,
        isOfflineEnabled: Boolean
    ) {
        viewModelScope.launch {
            repository.updateCatererCommissionAndOfflineSettings(
                catererId = catererId,
                onlineCommission = onlineCommission,
                offlineCommission = offlineCommission,
                isOnlineEnabled = isOnlineEnabled,
                isOfflineEnabled = isOfflineEnabled
            )
            showFeedback("✅ Kitchen settings saved: Online (${if (isOnlineEnabled) "ON" else "OFF"}, $onlineCommission%), Offline (${if (isOfflineEnabled) "ON" else "OFF"}, $offlineCommission%)")
        }
    }

    fun toggleKitchenOfflineBooking(catererId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            val caterer = caterersList.value.find { it.id == catererId } ?: return@launch
            repository.updateCatererCommissionAndOfflineSettings(
                catererId = catererId,
                onlineCommission = caterer.onlineCommissionPercentage,
                offlineCommission = caterer.offlineCommissionPercentage,
                isOnlineEnabled = caterer.isOpenForBooking,
                isOfflineEnabled = isEnabled
            )
            showFeedback(if (isEnabled) "✅ Offline POS Booking Enabled for ${caterer.name}" else "🚫 Offline POS Booking Disabled for ${caterer.name}")
        }
    }

    fun processSettlementPayout(settlement: WeeklySettlementSummary, utrNumber: String) {
        val updatedSettlement = settlement.copy(
            settlementStatus = SettlementStatus.SETTLED,
            utrTransactionNumber = utrNumber,
            settledAtTimestamp = System.currentTimeMillis()
        )

        // Update in-memory state list
        val currentList = _firestoreSettlements.value.toMutableList()
        val index = currentList.indexOfFirst { it.settlementId == updatedSettlement.settlementId }
        if (index >= 0) {
            currentList[index] = updatedSettlement
        } else {
            currentList.add(updatedSettlement)
        }
        _firestoreSettlements.value = currentList

        // Save to Firestore
        settlementRepository.saveSettlement(updatedSettlement) { success, errorMsg ->
            if (success) {
                showFeedback("✅ Payout of ₹${updatedSettlement.netPayableToKitchen.toInt()} settled for ${updatedSettlement.catererName}! (UTR: $utrNumber)")
            } else {
                showFeedback("✅ Payout marked settled locally (${errorMsg ?: "Offline"})")
            }
        }

        // Automated Push Notification to Partner Kitchen App
        viewModelScope.launch {
            val notifTitle = "Payment Settlement Released! 💰"
            val notifMsg = "Weekly settlement payout of ₹${updatedSettlement.netPayableToKitchen.toInt()} for cycle ${updatedSettlement.weekLabel} has been processed and transferred to ${updatedSettlement.catererName}. Bank UTR: $utrNumber."

            repository.insertNotification(
                NotificationEntity(
                    id = "n_${System.currentTimeMillis()}_settlement",
                    title = notifTitle,
                    message = notifMsg,
                    targetRole = UserRole.KITCHEN
                )
            )

            NotificationHelper.showSystemNotification(
                context = getApplication(),
                title = notifTitle,
                message = notifMsg
            )
        }
    }

    fun updateKitchenSettings(newConfig: KitchenSettingsConfig, activeCatererId: String = "caterer_1") {
        _kitchenSettings.value = newConfig
        firestoreConfigRepository.saveKitchenSettings(newConfig) { success, errorMsg ->
            if (success) {
                showFeedback("🔥 Saved & Synced to Firebase Firestore! (फायरबेस पर सेटिंग्स सिंक हो गईं)")
            } else {
                showFeedback("✅ Kitchen Settings Saved Locally (${errorMsg ?: "Offline Mode"})")
            }
        }
        // Also update local CatererEntity serving ratios so customer profile immediately reflects it
        viewModelScope.launch {
            val caterer = caterersList.value.find { it.id == activeCatererId } ?: caterersList.value.firstOrNull()
            if (caterer != null) {
                val updated = caterer.copy(
                    biryaniPersonsPerKg = newConfig.biryaniPersonsPerKg,
                    sweetPersonsPerKg = newConfig.sweetPersonsPerKg,
                    gravyPersonsPerKg = newConfig.gravyPersonsPerKg,
                    rotiPersonsPerUnit = newConfig.rotiPersonsPerUnit
                )
                repository.updateCatererDetails(updated)
            }
        }
    }

    fun updateCatererServingRatios(
        catererId: String,
        biryaniPersonsPerKg: Double,
        sweetPersonsPerKg: Double,
        gravyPersonsPerKg: Double,
        rotiPersonsPerUnit: Double
    ) {
        viewModelScope.launch {
            val caterer = caterersList.value.find { it.id == catererId } ?: return@launch
            val updated = caterer.copy(
                biryaniPersonsPerKg = biryaniPersonsPerKg,
                sweetPersonsPerKg = sweetPersonsPerKg,
                gravyPersonsPerKg = gravyPersonsPerKg,
                rotiPersonsPerUnit = rotiPersonsPerUnit
            )
            repository.updateCatererDetails(updated)
            showFeedback("✅ Serving Capacity Updated: 1 Kg Biryani = $biryaniPersonsPerKg log, 1 Kg Sweet = $sweetPersonsPerKg log")
        }
    }

    fun isKitchenFavorite(catererId: String): Boolean {
        return _favoriteKitchenIds.value.contains(catererId)
    }

    fun toggleFavoriteKitchen(caterer: CatererEntity) {
        val currentUserId = _currentUser.value?.id ?: "cust_1"
        val isCurrentlyFav = _favoriteKitchenIds.value.contains(caterer.id)
        val newFavState = !isCurrentlyFav

        // Optimistic local state update
        val updatedSet = _favoriteKitchenIds.value.toMutableSet()
        if (newFavState) {
            updatedSet.add(caterer.id)
        } else {
            updatedSet.remove(caterer.id)
        }
        _favoriteKitchenIds.value = updatedSet

        // Persist to Room Database
        viewModelScope.launch {
            if (newFavState) {
                repository.addFavorite(currentUserId, caterer)
            } else {
                repository.removeFavorite(currentUserId, caterer.id)
            }
        }

        // Persist and Sync to Firestore User Document
        firestoreFavoritesRepository.toggleFavorite(
            userId = currentUserId,
            userProfile = _currentUser.value,
            caterer = caterer,
            isFavorite = newFavState
        ) { success, _ ->
            // Silent remote sync callback
        }

        if (newFavState) {
            showFeedback("Saved ${caterer.name} to Favorites! ❤️ (दिलपसंद में जोड़ा)")
        } else {
            showFeedback("Removed ${caterer.name} from Favorites 💔")
        }
    }

    fun clearFeedback() {
        _userFeedbackMessage.value = null
    }

    fun showFeedback(msg: String) {
        _userFeedbackMessage.value = msg
    }
}
