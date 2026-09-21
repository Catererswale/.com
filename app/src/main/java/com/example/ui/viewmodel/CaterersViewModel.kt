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
import com.example.data.models.KitchenUtensilEntity
import com.example.data.models.KycStatus
import com.example.data.models.Language
import com.example.data.models.MenuItemEntity
import com.example.data.models.NotificationEntity
import com.example.data.models.OrderEntity
import com.example.data.models.OrderStatus
import com.example.data.models.PartnerReviewEntity
import com.example.data.models.PaymentMethod
import com.example.data.models.PaymentStatus
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
import com.example.util.CancellationAlertData
import com.example.util.CancellationTier
import com.example.util.KitchenOrderSoundAlertManager
import com.example.util.NotificationHelper
import com.example.util.TimeSlotUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
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

        // Automatic 9:30 AM Morning Deg/Bartan Return Check & Alert Dispatch
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            repository.trigger930AmBartanMorningAlert(getApplication())
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

    // Customer Dietary Preference Filter: "ALL", "VEG", "NON_VEG"
    private val _customerDietFilter = MutableStateFlow("ALL")
    val customerDietFilter: StateFlow<String> = _customerDietFilter.asStateFlow()

    fun setCustomerDietFilter(diet: String) {
        _customerDietFilter.value = diet
        if (diet == "VEG") {
            _isVegMode.value = true
        } else if (diet == "ALL" || diet == "NON_VEG") {
            _isVegMode.value = false
        }
    }

    // Zomato-style Veg Mode Settings:
    // isVegMode: True when customer has toggled on Veg Mode
    private val _isVegMode = MutableStateFlow(false)
    val isVegMode: StateFlow<Boolean> = _isVegMode.asStateFlow()

    // vegModeScope: "ALL_RESTAURANTS" (Veg from all kitchens) vs "PURE_VEG_ONLY" (Only 100% Pure Veg kitchens)
    private val _vegModeScope = MutableStateFlow("ALL_RESTAURANTS")
    val vegModeScope: StateFlow<String> = _vegModeScope.asStateFlow()

    // vegModeDays: "ALL_DAYS" or selected day letters e.g. setOf("TUE", "THU", "SAT")
    private val _vegModeDaysMode = MutableStateFlow("ALL_DAYS") // "ALL_DAYS" or "CUSTOM_DAYS"
    val vegModeDaysMode: StateFlow<String> = _vegModeDaysMode.asStateFlow()

    private val _selectedVegDays = MutableStateFlow(setOf("M", "T", "W", "T", "F", "S", "S"))
    val selectedVegDays: StateFlow<Set<String>> = _selectedVegDays.asStateFlow()

    fun setVegMode(enabled: Boolean, scope: String = _vegModeScope.value, daysMode: String = _vegModeDaysMode.value) {
        _isVegMode.value = enabled
        _vegModeScope.value = scope
        _vegModeDaysMode.value = daysMode
        if (enabled) {
            _customerDietFilter.value = "VEG"
        } else {
            _customerDietFilter.value = "ALL"
        }
    }

    fun setVegModeScope(scope: String) {
        _vegModeScope.value = scope
        if (_isVegMode.value) {
            _customerDietFilter.value = "VEG"
        }
    }

    fun setVegModeDaysMode(daysMode: String) {
        _vegModeDaysMode.value = daysMode
    }

    fun toggleVegDay(day: String) {
        val current = _selectedVegDays.value.toMutableSet()
        if (current.contains(day)) {
            if (current.size > 1) current.remove(day)
        } else {
            current.add(day)
        }
        _selectedVegDays.value = current
    }

    // Delivery Location
    private val _deliveryLocation = MutableStateFlow("Okhla Phase 3, New Delhi")
    val deliveryLocation: StateFlow<String> = _deliveryLocation.asStateFlow()

    // Advanced Discovery & Search Filters: Delivery Time, Budget, Rating
    private val _filterMaxDeliveryTime = MutableStateFlow<Int?>(null) // e.g. 45 min
    val filterMaxDeliveryTime: StateFlow<Int?> = _filterMaxDeliveryTime.asStateFlow()

    private val _filterMaxBudget = MutableStateFlow<Double?>(null) // e.g. 1500.0
    val filterMaxBudget: StateFlow<Double?> = _filterMaxBudget.asStateFlow()

    private val _filterMinRating = MutableStateFlow<Float?>(null) // e.g. 4.8f
    val filterMinRating: StateFlow<Float?> = _filterMinRating.asStateFlow()

    fun setFilterMaxDeliveryTime(mins: Int?) {
        _filterMaxDeliveryTime.value = mins
    }

    fun setFilterMaxBudget(budget: Double?) {
        _filterMaxBudget.value = budget
    }

    fun setFilterMinRating(rating: Float?) {
        _filterMinRating.value = rating
    }

    fun clearAllAdvancedFilters() {
        _filterMaxDeliveryTime.value = null
        _filterMaxBudget.value = null
        _filterMinRating.value = null
    }

    // Selected Caterer for Detail View
    private val _selectedCatererId = MutableStateFlow<String?>("caterer_1")
    val selectedCatererId: StateFlow<String?> = _selectedCatererId.asStateFlow()

    // Deep Link / Standee QR Auto-Navigation (Browser & Camera scan)
    private val _deepLinkedCatererId = MutableStateFlow<String?>(null)
    val deepLinkedCatererId: StateFlow<String?> = _deepLinkedCatererId.asStateFlow()

    fun setDeepLinkedCatererId(catererId: String?) {
        _deepLinkedCatererId.value = catererId
        if (!catererId.isNullOrBlank()) {
            _selectedCatererId.value = catererId
        }
    }

    fun clearDeepLinkedCatererId() {
        _deepLinkedCatererId.value = null
    }

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

    val kitchenUtensilsList: StateFlow<List<KitchenUtensilEntity>> = repository.getUtensilsByKitchen("caterer_1")
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

    // Filtered Caterers based on search, category, location, dietary preference, Veg Mode & advanced discovery filters
    val filteredCaterers: StateFlow<List<CatererEntity>> = combine(
        caterersList,
        _searchQuery,
        _selectedCategory,
        _favoriteKitchenIds,
        _customerDietFilter,
        _deliveryLocation,
        _isVegMode,
        _vegModeScope,
        _filterMaxDeliveryTime,
        _filterMaxBudget,
        _filterMinRating
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val list = args[0] as List<CatererEntity>
        val query = args[1] as String
        val category = args[2] as String
        @Suppress("UNCHECKED_CAST")
        val favorites = args[3] as Set<String>
        val dietFilter = args[4] as String
        val location = args[5] as String
        val isVegModeActive = args[6] as Boolean
        val vegScope = args[7] as String
        val maxDeliveryTime = args[8] as? Int
        val maxBudget = args[9] as? Double
        val minRating = args[10] as? Float

        var result = list

        // Veg Mode Filtering (Zomato-style: All Restaurants vs Pure Veg Restaurants only)
        if (isVegModeActive) {
            if (vegScope == "PURE_VEG_ONLY") {
                // Strictly 100% Pure Veg kitchens
                result = result.filter { it.dietaryType.equals("PURE_VEG", ignoreCase = true) }
            } else {
                // All restaurants with veg offerings (exclude strict Non-Veg only specialists)
                result = result.filter { !it.dietaryType.equals("NON_VEG", ignoreCase = true) }
            }
        } else if (dietFilter == "VEG") {
            result = result.filter { !it.dietaryType.equals("NON_VEG", ignoreCase = true) }
        } else if (dietFilter == "NON_VEG") {
            result = result.filter { !it.dietaryType.equals("PURE_VEG", ignoreCase = true) }
        }

        // Advanced Search & Discovery Filters
        if (maxDeliveryTime != null && maxDeliveryTime > 0) {
            result = result.filter { it.deliveryTimeMinutes <= maxDeliveryTime }
        }
        if (maxBudget != null && maxBudget > 0.0) {
            result = result.filter { it.minOrderAmount <= maxBudget }
        }
        if (minRating != null && minRating > 0f) {
            result = result.filter { it.rating >= minRating }
        }

        if (category.equals("Favorites", ignoreCase = true) || category.contains("Favorite", ignoreCase = true)) {
            result = result.filter { favorites.contains(it.id) }
        }
        // Location relevance matching (if location selected, prioritize or filter matching kitchens)
        if (location.isNotBlank()) {
            val locKeywords = location.split(",", " ")
                .map { it.trim().lowercase() }
                .filter { it.length > 2 && it != "new" && it != "delhi" && it != "road" && it != "sector" }
            val matching = result.filter { caterer ->
                val addr = caterer.address.lowercase()
                locKeywords.any { kw -> addr.contains(kw) }
            }
            // If matching kitchens exist in that area, sort/show them first
            if (matching.isNotEmpty()) {
                val nonMatching = result.filterNot { matching.contains(it) }
                result = matching + nonMatching
            }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            val isVegIntent = q.contains("only veg") || q.contains("pure veg") || q.contains("veg caterer") || q.contains("veg only") || q.contains("shakahari") || q == "veg"
            if (isVegIntent) {
                result = result.filter { it.dietaryType.equals("PURE_VEG", ignoreCase = true) }
            } else {
                result = result.filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.kitchenName.contains(query, ignoreCase = true) ||
                    it.address.contains(query, ignoreCase = true) ||
                    it.dietaryType.contains(query, ignoreCase = true)
                }
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

    private val _customAddonServices = MutableStateFlow(
        listOf(
            CateringAddOn(
                id = "biryani_server",
                name = "Biryani Serving Staff",
                hindiName = "बिरयानी निकालने वाला कारीगर / हेल्पर",
                price = 600.0,
                unit = "1 Staff",
                icon = "🧑‍🍳",
                description = "Uniform-clad skilled helper to portion degh biryani cleanly without mess or wastage.",
                servesText = "Per 50-100 guests",
                isAvailable = true
            ),
            CateringAddOn(
                id = "kachumar_salan",
                name = "Extra Kachumar, Raita & Salan",
                hindiName = "एक्स्ट्रा कचूमर, रायता व सालन किट",
                price = 150.0,
                unit = "Large Set",
                icon = "🥗",
                description = "Sliced onion salad with lemon & green chili, fresh mint boondi raita & rich dawat mirchi ka salan.",
                servesText = "Serves 25-30 guests",
                isAvailable = true
            ),
            CateringAddOn(
                id = "disposable_plates",
                name = "Disposable Plates, Spoons & Tissue",
                hindiName = "डिस्पोजेबल प्लेट्स, चम्मच व टिशू सेट",
                price = 250.0,
                unit = "Pack of 30",
                icon = "🍽️",
                description = "Heavy 3-compartment partitioned plates, wrapped wooden spoons, 2-ply soft napkins & toothpicks.",
                servesText = "Pack of 30 plates",
                isAvailable = true
            ),
            CateringAddOn(
                id = "mukhwas_kit",
                name = "Shahi Mukhwas & Saunf Mishri Kit",
                hindiName = "शाही सौंफ, मिश्री व मुखवास किट",
                price = 99.0,
                unit = "Pack of 50",
                icon = "🍬",
                description = "Silver coated cardamom, roasted sweet saunf, rock sugar crystals and refreshing lemon wet wipes.",
                servesText = "Pack for 50 guests",
                isAvailable = true
            )
        )
    )
    val customAddonServices: StateFlow<List<CateringAddOn>> = _customAddonServices.asStateFlow()

    fun updateAddonService(addOn: CateringAddOn) {
        _customAddonServices.value = _customAddonServices.value.map {
            if (it.id == addOn.id) addOn else it
        }
        showFeedback("✅ Add-on updated: ${addOn.name} (₹${addOn.price.toInt()} • ${addOn.servesText})")
    }

    fun addCustomAddonService(addOn: CateringAddOn) {
        _customAddonServices.value = _customAddonServices.value + addOn
        showFeedback("✅ Added new Add-on: ${addOn.name}")
    }

    fun deleteAddonService(addOnId: String) {
        _customAddonServices.value = _customAddonServices.value.filter { it.id != addOnId }
        showFeedback("Add-on service removed")
    }

    fun toggleAddonAvailability(addOnId: String, isAvailable: Boolean) {
        _customAddonServices.value = _customAddonServices.value.map {
            if (it.id == addOnId) it.copy(isAvailable = isAvailable) else it
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
            if (quantity > 1.0) {
                showFeedback("${addOn.name}: ${quantity.toInt()} Units ✨")
            } else {
                showFeedback("Added ${addOn.name} to Cart ✨")
            }
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
        customAdvanceAmount: Double? = null,
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
                isOfflineBooking = isOfflineBooking,
                customAdvanceAmount = customAdvanceAmount
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

            // Trigger loud real-time audio alarm for Kitchen Partner if enabled
            val currentSettings = _kitchenSettings.value
            if (currentSettings.isOrderSoundAlertEnabled) {
                val createdOrder = repository.getOrderById(orderId) ?: OrderEntity(
                    orderId = orderId,
                    customerName = customerName,
                    customerMobile = customerMobile,
                    deliveryAddress = address,
                    catererId = catererId,
                    catererName = catererName,
                    itemsSummary = itemsSummary,
                    totalAmount = totalAmount,
                    advancePaidAmount = if (is30PercentAdvance) totalAmount * (currentSettings.defaultAdvancePercentage / 100.0) else (customAdvanceAmount ?: 0.0),
                    balanceAmount = totalAmount - (if (is30PercentAdvance) totalAmount * (currentSettings.defaultAdvancePercentage / 100.0) else (customAdvanceAmount ?: 0.0)),
                    paymentMethod = paymentMethod,
                    paymentStatus = PaymentStatus.ADVANCE_PAID_30,
                    orderStatus = OrderStatus.NEW,
                    deliveryDate = deliveryDate,
                    deliveryTimeSlot = deliveryTimeSlot
                )
                KitchenOrderSoundAlertManager.triggerOrderAlert(getApplication(), createdOrder)
            }

            showFeedback("Order $orderId placed successfully! 🎉")
            onSuccess(orderId)
        }
    }

    fun triggerKitchenAlarmTest() {
        // Trigger high-alert incoming order alarm with test order so kitchen can test tone & accept/reject popup
        val sampleOrder = ordersList.value.firstOrNull { it.orderStatus == OrderStatus.NEW }
            ?: ordersList.value.firstOrNull()
            ?: OrderEntity(
                orderId = "ORD-" + System.currentTimeMillis().toString().takeLast(6),
                customerName = "Rohan Verma",
                customerMobile = "+91 98765 43210",
                deliveryAddress = "Bandra West, Mumbai - 400050",
                catererId = "cat_1",
                catererName = "A1 Huma Central Kitchen",
                itemsSummary = "Hyderabadi Chicken Biryani (50 Pax), Mirchi Salan, Boondi Raita, Gulab Jamun",
                totalAmount = 14500.0,
                advancePaidAmount = 7250.0,
                balanceAmount = 7250.0,
                paymentMethod = PaymentMethod.UPI,
                paymentStatus = PaymentStatus.ADVANCE_PAID_30,
                orderStatus = OrderStatus.NEW,
                deliveryDate = TimeSlotUtils.createDateFormat("yyyy-MM-dd").format(TimeSlotUtils.getIndianCalendar().time),
                deliveryTimeSlot = "11:00 AM - 02:00 PM (Lunch)"
            )
        KitchenOrderSoundAlertManager.triggerOrderAlert(getApplication(), sampleOrder)
        showFeedback("🚨 High-Alert Incoming Order Tone & Accept/Reject Popup Active!")
    }

    fun stopKitchenAlarm() {
        KitchenOrderSoundAlertManager.stopAlert()
        showFeedback("🔕 Kitchen audio alarm muted.")
    }

    fun muteKitchenToneOnly() {
        KitchenOrderSoundAlertManager.muteSoundOnly()
        showFeedback("🔇 High-alert tone muted. Please Accept or Reject the order.")
    }

    fun acceptOrderByKitchen(orderId: String) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId)
            if (order != null) {
                val updated = order.copy(orderStatus = OrderStatus.ACCEPTED)
                repository.updateOrder(updated)
                repository.updateOrderStatus(orderId, OrderStatus.ACCEPTED, getApplication())
                KitchenOrderSoundAlertManager.stopAlert()
                showFeedback("✅ Order #${order.orderId.takeLast(6).uppercase()} Accepted! Kitchen KOT generated.")
            } else {
                repository.updateOrderStatus(orderId, OrderStatus.ACCEPTED, getApplication())
                KitchenOrderSoundAlertManager.stopAlert()
                showFeedback("✅ Order Accepted!")
            }
        }
    }

    fun rejectOrderByKitchen(orderId: String, reason: String) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId)
            if (order != null) {
                val updated = order.copy(
                    orderStatus = OrderStatus.CANCELLED,
                    cancellationReason = "Kitchen Rejected: $reason",
                    refundAmount = order.advancePaidAmount,
                    kitchenSettlementAmount = 0.0,
                    companyAdsFundAmount = 0.0,
                    paymentStatus = if (order.advancePaidAmount > 0) PaymentStatus.REFUNDED else order.paymentStatus
                )
                repository.updateOrder(updated)
                repository.updateOrderStatus(orderId, OrderStatus.CANCELLED, getApplication())
                KitchenOrderSoundAlertManager.stopAlert()
                showFeedback("❌ Order #${order.orderId.takeLast(6).uppercase()} Rejected. Full Customer Refund (₹${order.advancePaidAmount.toInt()}) Initiated.")
            } else {
                repository.updateOrderStatus(orderId, OrderStatus.CANCELLED, getApplication())
                KitchenOrderSoundAlertManager.stopAlert()
                showFeedback("❌ Order Rejected.")
            }
        }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            if (status == OrderStatus.PREPARING) {
                val order = repository.getOrderById(orderId)
                if (order != null) {
                    val isAllowed = TimeSlotUtils.isKitchenPrepAllowed(order.deliveryDate, order.deliveryTimeSlot)
                    if (!isAllowed) {
                        val countdown = TimeSlotUtils.getKitchenPrepCountdownLabel(order.deliveryDate, order.deliveryTimeSlot)
                        showFeedback("⚠️ Freshness Lock: Kitchen can only start cooking 10 hours before delivery! ($countdown)")
                        return@launch
                    }
                    val updated = order.copy(
                        orderStatus = OrderStatus.PREPARING,
                        prepStartedTimestamp = System.currentTimeMillis()
                    )
                    repository.updateOrder(updated)
                    repository.updateOrderStatus(orderId, status, getApplication())
                    if (KitchenOrderSoundAlertManager.currentAlertOrder.value?.orderId == orderId) {
                        KitchenOrderSoundAlertManager.stopAlert()
                    }
                    showFeedback("🍳 Cooking started for Order $orderId! Payout eligibility activated.")
                    return@launch
                }
            }
            repository.updateOrderStatus(orderId, status, getApplication())
            if (KitchenOrderSoundAlertManager.currentAlertOrder.value?.orderId == orderId) {
                KitchenOrderSoundAlertManager.stopAlert()
            }
            showFeedback("Order $orderId status updated to ${status.name}")
        }
    }

    /**
     * Customer Order Cancellation adhering to the 3-Tier Policy:
     * - Same Day: 0% customer refund. Kitchen receives advance settlement minus 10% platform commission.
     * - > 24 Hours: 100% full refund to customer. Kitchen alerted with urgent alarm siren.
     * - 12 to 24 Hours: No cash return. Advance deposited to Company Ads & Offers Fund.
     * - < 12 Hours: 0% customer refund. Advance minus 10% commission settled to kitchen.
     */
    fun cancelOrderByCustomer(
        orderId: String,
        reason: String,
        onCompleted: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId)
            if (order == null) {
                showFeedback("Order not found")
                onCompleted(false, "Order not found")
                return@launch
            }
            if (order.isNonCancellable || order.orderStatus == OrderStatus.OUT_FOR_DELIVERY || order.orderStatus == OrderStatus.DELIVERED) {
                val lockMsg = "Order cannot be cancelled: Already locked with 100% full payment or out for delivery."
                showFeedback(lockMsg)
                onCompleted(false, lockMsg)
                return@launch
            }

            val policy = TimeSlotUtils.evaluateCancellationPolicy(order)
            val updatedOrder = order.copy(
                orderStatus = OrderStatus.CANCELLED,
                cancellationReason = reason,
                refundAmount = policy.customerRefundAmount,
                kitchenSettlementAmount = policy.kitchenSettlementAmount,
                companyAdsFundAmount = policy.companyAdsFundAmount,
                paymentStatus = if (policy.customerRefundAmount > 0) PaymentStatus.REFUNDED else order.paymentStatus
            )

            repository.updateOrder(updatedOrder)

            // Trigger loud audio alarm/siren for Kitchen Partner
            KitchenOrderSoundAlertManager.triggerCancellationAlert(
                context = getApplication(),
                alertData = CancellationAlertData(
                    order = updatedOrder,
                    reason = reason,
                    policyExplanation = policy.policyExplanation,
                    customerRefund = policy.customerRefundAmount,
                    kitchenSettlement = policy.kitchenSettlementAmount,
                    companyFund = policy.companyAdsFundAmount,
                    isSameDay = policy.isSameDay
                )
            )

            val custNotifMsg = when (policy.tier) {
                CancellationTier.MORE_THAN_24_HOURS_FULL_REFUND ->
                    "100% Refund (₹${policy.customerRefundAmount.toInt()}) processed for Order #${order.orderId}."
                CancellationTier.BETWEEN_12_AND_24_HOURS_RESCHEDULE_OR_FORFEIT ->
                    "Order #${order.orderId} cancelled. Advance deposited into Company Ads & Offers Fund."
                CancellationTier.SAME_DAY_NO_REFUND ->
                    "Same-day Order #${order.orderId} cancelled. ₹0 customer refund. Kitchen settlement credited."
                CancellationTier.LESS_THAN_12_HOURS_NO_REFUND ->
                    "Order #${order.orderId} cancelled (<12h). ₹0 customer refund. Kitchen compensated for preparation."
            }

            NotificationHelper.showSystemNotification(
                context = getApplication(),
                title = "Order #${order.orderId} Cancelled",
                message = custNotifMsg
            )

            showFeedback(custNotifMsg)
            onCompleted(true, custNotifMsg)
        }
    }

    /**
     * Customer Reschedule Window:
     * - Reschedule up to 7 days into the future.
     * - Customer must complete the remaining 50% balance payment to lock the booking.
     * - Once rescheduled, order is permanently marked non-cancellable!
     */
    fun rescheduleOrderByCustomer(
        orderId: String,
        newDeliveryDate: String,
        newDeliveryTimeSlot: String,
        onCompleted: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId)
            if (order == null) {
                showFeedback("Order not found")
                onCompleted(false, "Order not found")
                return@launch
            }

            val updatedOrder = order.copy(
                deliveryDate = newDeliveryDate,
                deliveryTimeSlot = newDeliveryTimeSlot,
                advancePaidAmount = order.totalAmount, // 100% full payment completed
                balanceAmount = 0.0,
                paymentStatus = PaymentStatus.FULL_PAID,
                isRescheduled = true,
                isNonCancellable = true
            )

            repository.updateOrder(updatedOrder)

            NotificationHelper.showSystemNotification(
                context = getApplication(),
                title = "Order Rescheduled Successfully! 🗓️",
                message = "Order #${order.orderId} rescheduled to $newDeliveryDate ($newDeliveryTimeSlot) with 100% Full Payment locked."
            )

            val successMsg = "✅ Order #${order.orderId} rescheduled to $newDeliveryDate ($newDeliveryTimeSlot)! Full payment locked."
            showFeedback(successMsg)
            onCompleted(true, successMsg)
        }
    }

    fun assignDeliveryBoy(
        orderId: String,
        boy: DeliveryBoyEntity,
        bartanDescription: String? = null,
        handiCount: Int = 0,
        spoonsCount: Int = 0,
        boxesCount: Int = 0
    ) {
        viewModelScope.launch {
            repository.assignDeliveryBoy(
                orderId = orderId,
                boy = boy,
                bartanDescription = bartanDescription,
                handiCount = handiCount,
                spoonsCount = spoonsCount,
                boxesCount = boxesCount,
                context = getApplication()
            )
            val containerMsg = if (!bartanDescription.isNullOrBlank()) " with containers: $bartanDescription" else ""
            showFeedback("Assigned ${boy.name} to Order #$orderId$containerMsg")
        }
    }

    /**
     * Smart Multi-Order Assignment:
     * Kitchen can assign any number of selected orders (1, 2, 5, 10+) to a single delivery partner at once.
     * All assigned orders are automatically moved to OUT_FOR_DELIVERY status with live notification.
     */
    fun assignMultipleOrdersToDeliveryBoy(orderIds: List<String>, boy: DeliveryBoyEntity, onCompleted: (() -> Unit)? = null) {
        viewModelScope.launch {
            orderIds.forEach { id ->
                repository.assignDeliveryBoy(id, boy, getApplication())
                repository.updateOrderStatus(id, OrderStatus.OUT_FOR_DELIVERY, getApplication())
            }
            showFeedback("🚀 ${orderIds.size} Orders assigned to ${boy.name} & Dispatched!")
            onCompleted?.invoke()
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
            showFeedback("Delivery Boy '${boy.name}' registered successfully! ✅")
        }
    }

    fun updateDeliveryBoy(boy: DeliveryBoyEntity) {
        viewModelScope.launch {
            repository.updateDeliveryBoy(boy)
            showFeedback("Delivery Boy '${boy.name}' details updated! ✏️")
        }
    }

    fun deleteDeliveryBoy(boyId: String, kitchenId: String, boyName: String) {
        viewModelScope.launch {
            repository.deleteDeliveryBoy(boyId, kitchenId)
            showFeedback("Delivery Boy '$boyName' removed from kitchen fleet! 🗑️")
        }
    }

    fun markBartanCollected(id: String, date: String) {
        viewModelScope.launch {
            repository.updateBartanCollected(id, true, date)
            showFeedback("Bartan marked as collected ✅")
        }
    }

    fun markBartanStatus(id: String, status: String, date: String = "2026-07-25") {
        viewModelScope.launch {
            val isCollected = (status == "COLLECTED" || status == "RETURNED_TO_KITCHEN")
            repository.updateBartanCollectedWithStatus(id, isCollected, date, status)
            showFeedback("Bartan return status updated: $status ✅")
        }
    }

    fun trigger930AmMorningAlert(showToast: Boolean = true) {
        viewModelScope.launch {
            val count = repository.trigger930AmBartanMorningAlert(getApplication())
            if (showToast) {
                if (count > 0) {
                    showFeedback("⏰ 9:30 AM Alert Sent! $count pending container(s) notified to respective Kitchens & Delivery Boys.")
                } else {
                    showFeedback("All containers have been returned! No pending 9:30 AM returns.")
                }
            }
        }
    }

    fun saveOrUpdateUtensil(utensil: KitchenUtensilEntity) {
        viewModelScope.launch {
            repository.saveUtensil(utensil)
            showFeedback("Utensil '${utensil.name}' updated! Total Stock: ${utensil.totalStock}")
        }
    }

    fun deleteUtensil(id: String) {
        viewModelScope.launch {
            repository.deleteUtensil(id)
            showFeedback("Utensil removed from inventory")
        }
    }

    fun reassignBartanPickupBoy(
        recordId: String,
        newBoy: DeliveryBoyEntity,
        customerName: String = "",
        handiCount: Int = 0
    ) {
        viewModelScope.launch {
            repository.reassignBartanPickupBoy(
                recordId = recordId,
                newBoyId = newBoy.id,
                newBoyName = newBoy.name,
                newBoyMobile = newBoy.mobile,
                context = getApplication()
            )
            showFeedback("🔄 Pickup assigned to ${newBoy.name}! Notification sent.")
        }
    }

    fun sendManualContainerReminderToDeliveryBoy(
        bartanRecordId: String?,
        orderId: String,
        deliveryBoyId: String,
        deliveryBoyName: String,
        deliveryBoyMobile: String,
        customerName: String,
        customerMobile: String,
        customerAddress: String,
        containerDescription: String,
        catererName: String = "A1 Huma Caterers",
        daysOverdue: Int = 1,
        onComplete: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            repository.sendManualContainerReminderToDeliveryBoy(
                bartanRecordId = bartanRecordId,
                orderId = orderId,
                deliveryBoyId = deliveryBoyId,
                deliveryBoyName = deliveryBoyName,
                deliveryBoyMobile = deliveryBoyMobile,
                customerName = customerName,
                customerMobile = customerMobile,
                customerAddress = customerAddress,
                containerDescription = containerDescription,
                catererName = catererName,
                daysOverdue = daysOverdue,
                context = getApplication()
            )
            val boyName = deliveryBoyName.ifBlank { "Delivery Boy" }
            showFeedback("🔔 Overdue reminder notification sent to $boyName for Order #$orderId!")
            onComplete?.invoke()
        }
    }

    fun getBartansForDeliveryBoy(deliveryBoyId: String): Flow<List<com.example.data.models.BartanRecordEntity>> {
        return repository.getBartanRecordsByDeliveryBoy(deliveryBoyId)
    }

    fun markCashReceivedByKitchen(orderId: String) {
        viewModelScope.launch {
            repository.markCashReceivedByKitchen(orderId, getApplication())
            showFeedback("Cash Received & Settled for Order #$orderId ✅")
        }
    }

    fun deliveryBoyNotifyCashHandover(orderId: String) {
        viewModelScope.launch {
            repository.deliveryBoyNotifyCashHandover(orderId, getApplication())
            showFeedback("Cash Handover Notification sent to Kitchen Cashier 🔔")
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
                    rotiPersonsPerUnit = newConfig.rotiPersonsPerUnit,
                    offersAddonServices = newConfig.offersAddonServices
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
