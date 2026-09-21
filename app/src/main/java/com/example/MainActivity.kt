package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.models.PaymentMethod
import com.example.data.models.UserRole
import com.example.data.repository.CaterersViewModel
import com.example.ui.admin.AdminMainContainer
import com.example.ui.auth.AuthScreen
import com.example.ui.common.NotificationFeedbackToast
import com.example.ui.common.RoleSelectorBar
import com.example.ui.customer.AddressScreen
import com.example.ui.customer.CartScreen
import com.example.ui.customer.CatererDetailScreen
import com.example.ui.customer.CustomerHomeScreen
import com.example.ui.customer.CustomerOrdersScreen
import com.example.ui.customer.CustomerProfileScreen
import com.example.ui.customer.DeliveredScreen
import com.example.ui.customer.DeliveryScheduleScreen
import com.example.ui.customer.CustomerOrderSummaryScreen
import com.example.ui.customer.FeedbackAndRatingScreen
import com.example.ui.customer.HelpSupportScreen
import com.example.ui.customer.LiveTrackingScreen
import com.example.ui.customer.OrderConfirmationScreen
import com.example.ui.customer.PaymentScreen
import com.example.ui.delivery.DeliveryMainContainer
import com.example.ui.kitchen.KitchenMainContainer
import com.example.ui.theme.MyApplicationTheme

enum class CustomerScreen {
    HOME,
    CATERER_DETAIL,
    CART,
    DELIVERY_SCHEDULE,
    ADDRESS,
    ORDER_SUMMARY,
    PAYMENT,
    ORDER_CONFIRMATION,
    LIVE_TRACKING,
    DELIVERED,
    FEEDBACK_RATING,
    ORDERS,
    PROFILE,
    HELP_SUPPORT
}

class MainActivity : ComponentActivity() {

    private val viewModel: CaterersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Kolkata"))
            java.util.Locale.setDefault(java.util.Locale("en", "IN"))
        } catch (_: Exception) {}
        enableEdgeToEdge()
        handleDeepLink(intent)

        setContent {
            MyApplicationTheme {
                CaterersWaleApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val uri: Uri = intent?.data ?: return
        val kitchen = uri.getQueryParameter("kitchen")
            ?: uri.getQueryParameter("kitchenId")
            ?: uri.getQueryParameter("caterer")
            ?: if (uri.pathSegments.contains("menu") && uri.lastPathSegment != "menu") uri.lastPathSegment else null
        if (!kitchen.isNullOrBlank()) {
            viewModel.setDeepLinkedCatererId(kitchen)
        }
    }
}

@Composable
fun CaterersWaleApp(viewModel: CaterersViewModel) {
    val currentRole by viewModel.currentRole.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val feedbackMessage by viewModel.userFeedbackMessage.collectAsState()

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // Deep Link Trigger (Browser/Camera Scan)
    val deepLinkedCatererId by viewModel.deepLinkedCatererId.collectAsState()

    // Customer Navigation States
    var customerScreen by remember { mutableStateOf(CustomerScreen.HOME) }
    var selectedCatererId by remember { mutableStateOf("caterer_1") }

    LaunchedEffect(deepLinkedCatererId) {
        val target = deepLinkedCatererId
        if (!target.isNullOrBlank()) {
            selectedCatererId = target
            viewModel.switchRole(UserRole.CUSTOMER)
            customerScreen = CustomerScreen.CATERER_DETAIL
            viewModel.showFeedback("⚡ Opening Live Kitchen Menu...")
            viewModel.clearDeepLinkedCatererId()
        }
    }
    var is30PercentAdvance by remember { mutableStateOf(true) }
    var isExclusiveStoreMode by remember { mutableStateOf(false) }
    var isPartnerPreviewMode by remember { mutableStateOf(false) }

    var selectedDate by remember { mutableStateOf("2026-07-25") }
    var selectedTimeSlot by remember { mutableStateOf("12:30 PM - 01:00 PM") }
    var selectedAddress by remember { mutableStateOf("Flat 402, Green Park Apartments, Okhla Phase 3") }
    var customerPhone by remember { mutableStateOf("+91 98765 11223") }
    var alternatePhone by remember { mutableStateOf("") }
    var confirmedOrderId by remember { mutableStateOf("CW-89210") }

    val cartTotalAmount by viewModel.cartTotalAmount.collectAsState()
    val orders by viewModel.ordersList.collectAsState()
    val kitchenSettings by viewModel.kitchenSettings.collectAsState()
    val caterersList by viewModel.caterersList.collectAsState()
    var finalOrderCalculatedAmount by remember { mutableStateOf(0.0) }
    var finalOrderDeliveryFee by remember { mutableStateOf(0.0) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            RoleSelectorBar(
                currentRole = currentRole,
                onRoleSelected = { viewModel.switchRole(it) },
                currentLanguage = currentLanguage,
                onLanguageChange = { viewModel.switchLanguage(it) },
                currentUser = currentUser,
                isLoggedIn = isLoggedIn,
                onLogout = { viewModel.logout() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!isLoggedIn) {
                AuthScreen(
                    onLoginSuccess = { profile -> viewModel.loginUser(profile) },
                    onQuickDemoLogin = { role -> viewModel.quickDemoLogin(role) }
                )
            } else {
                Crossfade(targetState = currentRole, label = "RoleTransition") { role ->
                    when (role) {
                    UserRole.CUSTOMER -> {
                        when (customerScreen) {
                            CustomerScreen.HOME -> {
                                CustomerHomeScreen(
                                    viewModel = viewModel,
                                    onSelectCaterer = { id ->
                                        selectedCatererId = id
                                        customerScreen = CustomerScreen.CATERER_DETAIL
                                    },
                                    onOpenCart = { customerScreen = CustomerScreen.CART },
                                    onOpenOrders = { customerScreen = CustomerScreen.ORDERS },
                                    onOpenProfile = { customerScreen = CustomerScreen.PROFILE },
                                    onOpenSupport = { customerScreen = CustomerScreen.HELP_SUPPORT },
                                    onTrackOrder = { orderId ->
                                        confirmedOrderId = orderId
                                        customerScreen = CustomerScreen.LIVE_TRACKING
                                    },
                                    onRateKitchen = { orderId ->
                                        confirmedOrderId = orderId
                                        customerScreen = CustomerScreen.FEEDBACK_RATING
                                    }
                                )
                            }

                            CustomerScreen.CATERER_DETAIL -> {
                                CatererDetailScreen(
                                    catererId = selectedCatererId,
                                    viewModel = viewModel,
                                    isExclusiveStore = isExclusiveStoreMode,
                                    isPartnerPreviewMode = isPartnerPreviewMode,
                                    onReturnToKitchen = {
                                        isPartnerPreviewMode = false
                                        isExclusiveStoreMode = false
                                        viewModel.switchRole(UserRole.KITCHEN)
                                    },
                                    onBack = {
                                        if (isPartnerPreviewMode) {
                                            isPartnerPreviewMode = false
                                            isExclusiveStoreMode = false
                                            viewModel.switchRole(UserRole.KITCHEN)
                                        } else {
                                            isExclusiveStoreMode = false
                                            customerScreen = CustomerScreen.HOME
                                        }
                                    },
                                    onOpenCart = { customerScreen = CustomerScreen.CART },
                                    onOpenReviews = {
                                        selectedCatererId = it
                                        customerScreen = CustomerScreen.FEEDBACK_RATING
                                    }
                                )
                            }

                            CustomerScreen.CART -> {
                                CartScreen(
                                    viewModel = viewModel,
                                    onBack = { customerScreen = CustomerScreen.HOME },
                                    onProceedToSchedule = { isAdvance, date, timeSlot ->
                                        is30PercentAdvance = isAdvance
                                        selectedDate = date
                                        selectedTimeSlot = timeSlot
                                        customerScreen = CustomerScreen.ADDRESS
                                    }
                                )
                            }

                            CustomerScreen.DELIVERY_SCHEDULE -> {
                                DeliveryScheduleScreen(
                                    viewModel = viewModel,
                                    onBack = { customerScreen = CustomerScreen.CART },
                                    onProceedToAddress = { date, time ->
                                        selectedDate = date
                                        selectedTimeSlot = time
                                        customerScreen = CustomerScreen.ADDRESS
                                    }
                                )
                            }

                            CustomerScreen.ADDRESS -> {
                                val currentLoc by viewModel.deliveryLocation.collectAsState()
                                AddressScreen(
                                    onBack = { customerScreen = CustomerScreen.CART },
                                    initialPhone = customerPhone,
                                    initialAltPhone = alternatePhone,
                                    defaultAddress = currentLoc,
                                    is30PercentAdvance = is30PercentAdvance,
                                    onProceedToPayment = { addr, phone, altPhone ->
                                        selectedAddress = addr
                                        customerPhone = phone
                                        alternatePhone = altPhone
                                        customerScreen = CustomerScreen.ORDER_SUMMARY
                                    }
                                )
                            }

                            CustomerScreen.ORDER_SUMMARY -> {
                                val cartItems = viewModel.cartItemsList.value
                                val catererId = cartItems.firstOrNull()?.catererId
                                val currentCaterer = caterersList.find { it.id == catererId }
                                val catererName = cartItems.firstOrNull()?.catererName ?: "A1 Huma Caterers"
                                CustomerOrderSummaryScreen(
                                    catererName = catererName,
                                    items = cartItems,
                                    totalAmount = cartTotalAmount,
                                    deliveryAddress = selectedAddress,
                                    deliveryDate = selectedDate,
                                    deliveryTimeSlot = selectedTimeSlot,
                                    customerPhone = customerPhone,
                                    is30PercentAdvance = is30PercentAdvance,
                                    deliveryFee = 0.0,
                                    distanceKm = currentCaterer?.distanceKm ?: 2.4,
                                    isDeliveryChargeEnabled = kitchenSettings.isDeliveryChargeEnabled,
                                    deliveryChargePerKm = kitchenSettings.deliveryChargePerKm,
                                    onBack = { customerScreen = CustomerScreen.ADDRESS },
                                    onProceedToPayment = { calculatedTotal, deliveryFee ->
                                        finalOrderCalculatedAmount = calculatedTotal
                                        finalOrderDeliveryFee = deliveryFee
                                        customerScreen = CustomerScreen.PAYMENT
                                    }
                                )
                            }

                            CustomerScreen.PAYMENT -> {
                                val effectivePaymentAmount = if (finalOrderCalculatedAmount > 0.0) finalOrderCalculatedAmount else cartTotalAmount
                                PaymentScreen(
                                    totalAmount = effectivePaymentAmount,
                                    is30PercentAdvance = is30PercentAdvance,
                                    deliveryAddress = selectedAddress,
                                    customerPhone = customerPhone,
                                    deliveryDate = selectedDate,
                                    deliveryTimeSlot = selectedTimeSlot,
                                    onBack = { customerScreen = CustomerScreen.ORDER_SUMMARY },
                                    onConfirmPayment = { method ->
                                        val cartItems = viewModel.cartItemsList.value
                                        val itemsSummary = cartItems.joinToString(", ") { "${it.name} (${it.quantity} ${it.unitType.name})" }
                                        val catererId = cartItems.firstOrNull()?.catererId ?: "caterer_1"
                                        val catererName = cartItems.firstOrNull()?.catererName ?: "A1 Huma Caterers"

                                        val fullAddress = if (alternatePhone.isNotBlank()) {
                                            "$selectedAddress (Alt Phone: $alternatePhone)"
                                        } else {
                                            selectedAddress
                                        }

                                        viewModel.placeOrder(
                                            customerName = "Rohan Verma",
                                            customerMobile = customerPhone,
                                            address = fullAddress,
                                            catererId = catererId,
                                            catererName = catererName,
                                            itemsSummary = itemsSummary,
                                            totalAmount = effectivePaymentAmount,
                                            is30PercentAdvance = is30PercentAdvance,
                                            paymentMethod = method,
                                            deliveryDate = selectedDate,
                                            deliveryTimeSlot = selectedTimeSlot,
                                            onSuccess = { orderId ->
                                                confirmedOrderId = orderId
                                                customerScreen = CustomerScreen.ORDER_CONFIRMATION
                                            }
                                        )
                                    }
                                )
                            }

                            CustomerScreen.ORDER_CONFIRMATION -> {
                                OrderConfirmationScreen(
                                    orderId = confirmedOrderId,
                                    viewModel = viewModel,
                                    onTrackOrder = {
                                        viewModel.showFeedback("⚡ Opening Live GPS Tracking for #$confirmedOrderId...")
                                        customerScreen = CustomerScreen.LIVE_TRACKING
                                    },
                                    onViewOrders = {
                                        viewModel.showFeedback("Opening My Catering Bookings...")
                                        customerScreen = CustomerScreen.ORDERS
                                    },
                                    onGoHome = {
                                        viewModel.showFeedback("Welcome back to Home")
                                        customerScreen = CustomerScreen.HOME
                                    }
                                )
                            }

                            CustomerScreen.LIVE_TRACKING -> {
                                LiveTrackingScreen(
                                    orderId = confirmedOrderId,
                                    viewModel = viewModel,
                                    onBack = { customerScreen = CustomerScreen.ORDERS },
                                    onOpenDeliveredView = { customerScreen = CustomerScreen.DELIVERED }
                                )
                            }

                            CustomerScreen.DELIVERED -> {
                                val currentOrder = orders.find { it.orderId == confirmedOrderId } ?: orders.first()
                                DeliveredScreen(
                                    order = currentOrder,
                                    viewModel = viewModel,
                                    onBackToHome = { customerScreen = CustomerScreen.ORDERS },
                                    onOpenFeedbackScreen = { orderId ->
                                        confirmedOrderId = orderId
                                        customerScreen = CustomerScreen.FEEDBACK_RATING
                                    }
                                )
                            }

                            CustomerScreen.FEEDBACK_RATING -> {
                                FeedbackAndRatingScreen(
                                    orderId = confirmedOrderId,
                                    catererId = selectedCatererId,
                                    viewModel = viewModel,
                                    onBack = { customerScreen = CustomerScreen.ORDERS },
                                    onNavigateToHome = { customerScreen = CustomerScreen.ORDERS }
                                )
                            }

                            CustomerScreen.ORDERS -> {
                                CustomerOrdersScreen(
                                    viewModel = viewModel,
                                    onBack = { customerScreen = CustomerScreen.HOME },
                                    onTrackOrder = { orderId ->
                                        confirmedOrderId = orderId
                                        customerScreen = CustomerScreen.LIVE_TRACKING
                                    },
                                    onRateKitchen = { orderId ->
                                        confirmedOrderId = orderId
                                        customerScreen = CustomerScreen.FEEDBACK_RATING
                                    }
                                )
                            }

                            CustomerScreen.PROFILE -> {
                                CustomerProfileScreen(
                                    viewModel = viewModel,
                                    onBack = { customerScreen = CustomerScreen.HOME },
                                    onOpenSupport = { customerScreen = CustomerScreen.HELP_SUPPORT },
                                    onOpenOrders = { customerScreen = CustomerScreen.ORDERS },
                                    onTrackOrder = { orderId ->
                                        confirmedOrderId = orderId
                                        customerScreen = CustomerScreen.LIVE_TRACKING
                                    },
                                    onRateKitchen = { orderId ->
                                        confirmedOrderId = orderId
                                        customerScreen = CustomerScreen.FEEDBACK_RATING
                                    }
                                )
                            }

                            CustomerScreen.HELP_SUPPORT -> {
                                HelpSupportScreen(
                                    onBack = { customerScreen = CustomerScreen.HOME }
                                )
                            }
                        }
                    }

                    UserRole.KITCHEN -> {
                        KitchenMainContainer(
                            viewModel = viewModel,
                            onPreviewCustomerStore = { catererId ->
                                selectedCatererId = catererId
                                isExclusiveStoreMode = true
                                isPartnerPreviewMode = true
                                customerScreen = CustomerScreen.CATERER_DETAIL
                                viewModel.switchRole(UserRole.CUSTOMER)
                            }
                        )
                    }

                    UserRole.DELIVERY_BOY -> {
                        DeliveryMainContainer(viewModel = viewModel)
                    }

                    UserRole.SUPER_ADMIN -> {
                        AdminMainContainer(viewModel = viewModel)
                    }
                }
            }
        }

        // Global Notification / Toast Feedback (Positioned at TopCenter so it never blocks CTA buttons)
            NotificationFeedbackToast(
                message = feedbackMessage,
                onDismiss = { viewModel.clearFeedback() },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
            )
        }
    }
}
