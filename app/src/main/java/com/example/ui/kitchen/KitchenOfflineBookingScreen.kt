package com.example.ui.kitchen

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.CateringAddOn
import com.example.data.models.FoodType
import com.example.data.models.OrderEntity
import com.example.data.models.OrderStatus
import com.example.data.models.PaymentMethod
import com.example.data.models.PaymentStatus
import com.example.data.models.UnitType
import com.example.data.repository.CaterersViewModel
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun KitchenOfflineBookingScreen(viewModel: CaterersViewModel) {
    val context = LocalContext.current
    val menuItems by viewModel.menuItemsList.collectAsState()
    val ordersList by viewModel.ordersList.collectAsState()
    val caterersList by viewModel.caterersList.collectAsState()
    val currentCaterer = caterersList.find { it.id == "caterer_1" } ?: caterersList.firstOrNull()
    val isOfflineBookingEnabled = currentCaterer?.isOfflineBookingEnabled ?: true

    // Step state: 1 = Menu Selection (Category & Dishes), 2 = Customer & Booking Details, 3 = Receipt & Invoice
    var currentStep by remember { mutableIntStateOf(1) }

    // Pre-defined customer directory for auto-search simulation
    val knownCustomers = remember {
        listOf(
            Triple("+91 98765 43210", "Rohan Verma", "Flat 402, Green Park Main, New Delhi"),
            Triple("+91 98111 22334", "Imran Khan", "A-12 Jamia Nagar, Okhla Phase 3, New Delhi"),
            Triple("+91 99887 76655", "Suresh Sharma", "Plot 18, Sector 62, Noida, UP"),
            Triple("+91 88000 11223", "Shabbir Ahmed", "H.No 145, Batla House, Jamia Nagar, New Delhi"),
            Triple("+91 97112 33445", "Priya Malhotra", "C-88, South Extension Part 2, New Delhi")
        )
    }

    // Settings & Available Slots
    val settings by viewModel.kitchenSettings.collectAsState()
    val availableAddonServices by viewModel.customAddonServices.collectAsState()

    // --- STEP 1 STATE: Menu, Category & POS Cart ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedDietFilter by remember { mutableStateOf("ALL") } // ALL, VEG, NON_VEG
    val offlineCart = remember { mutableStateMapOf<String, Double>() } // ItemId -> Quantity

    // Add-On Services Selection State
    val selectedAddOns = remember { mutableStateMapOf<String, Double>() } // addOnId -> quantity
    val customAddonItems = remember { mutableStateListOf<Pair<String, Double>>() } // custom name -> price
    var customAddonNameInput by remember { mutableStateOf("") }
    var customAddonPriceInput by remember { mutableStateOf("") }

    // Custom Item Input
    var customItemName by remember { mutableStateOf("") }
    var customItemPrice by remember { mutableStateOf("") }
    val customCartItems = remember { mutableStateListOf<Pair<String, Double>>() }

    // --- STEP 2 STATE: Customer, Event & Advance Payment Details ---
    var customerMobile by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var customerAltMobile by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }
    var isCustomerFound by remember { mutableStateOf(false) }

    val todayCalendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayFormatted = dateFormat.format(todayCalendar.time)

    var eventDate by remember { mutableStateOf(todayFormatted) }

    val availableOfflineSlots = remember(eventDate, settings) {
        com.example.util.TimeSlotUtils.getAvailableTimeSlots(
            eventDate,
            if (settings.deliveryTimeSlots.isNotEmpty()) settings.deliveryTimeSlots else listOf(
                "12:00 PM - 02:00 PM (Lunch)",
                "07:30 PM - 10:00 PM (Dinner)",
                "08:00 AM - 10:00 AM (Breakfast / Morning)"
            ),
            settings
        )
    }

    var eventTimeSlot by remember(availableOfflineSlots) {
        mutableStateOf(availableOfflineSlots.firstOrNull() ?: "12:00 PM - 02:00 PM (Lunch)")
    }

    var isDeliveryRequired by remember { mutableStateOf(true) }
    var isMetalDegGiven by remember { mutableStateOf(true) }
    var cookingNotes by remember { mutableStateOf("") }

    // Discount State
    var isDiscountEnabled by remember { mutableStateOf(false) }
    var discountType by remember { mutableStateOf("FLAT") } // "FLAT" (₹) or "PERCENT" (%)
    var discountInputValue by remember { mutableStateOf("") }
    var discountReason by remember { mutableStateOf("") }

    var advancePaidInput by remember { mutableStateOf("") }
    var isCashOnDelivery by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CASH_ON_DELIVERY) }

    // --- STEP 3 STATE: Generated Invoice ---
    var generatedInvoiceOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var generatedInvoiceId by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    // Perform Auto-Search when phone number changes
    fun searchCustomerByPhone(phoneQuery: String) {
        customerMobile = phoneQuery
        val cleanQuery = phoneQuery.replace(" ", "").replace("+91", "").trim()
        if (cleanQuery.length >= 4) {
            val foundKnown = knownCustomers.find { it.first.replace(" ", "").contains(cleanQuery) }
            val foundOrder = ordersList.find { it.customerMobile.replace(" ", "").contains(cleanQuery) }

            if (foundKnown != null) {
                customerName = foundKnown.second
                customerAddress = foundKnown.third
                isCustomerFound = true
            } else if (foundOrder != null) {
                customerName = foundOrder.customerName
                customerAddress = foundOrder.deliveryAddress
                isCustomerFound = true
            } else {
                isCustomerFound = false
            }
        } else {
            isCustomerFound = false
        }
    }

    // Calculations
    val menuItemsTotal = offlineCart.entries.sumOf { (itemId, qty) ->
        val item = menuItems.find { it.id == itemId }
        (item?.pricePerUnit ?: 0.0) * qty
    }
    val customItemsTotal = customCartItems.sumOf { it.second }
    val addOnsTotal = selectedAddOns.entries.sumOf { (addOnId, qty) ->
        val addOn = availableAddonServices.find { it.id == addOnId }
        (addOn?.price ?: 0.0) * qty
    } + customAddonItems.sumOf { it.second }

    val totalCartAmount = menuItemsTotal + customItemsTotal + addOnsTotal
    val totalCartItemsCount = offlineCart.size + customCartItems.size + selectedAddOns.size + customAddonItems.size

    val calculatedDiscount = remember(isDiscountEnabled, discountType, discountInputValue, totalCartAmount) {
        if (!isDiscountEnabled) 0.0
        else {
            val inputNum = discountInputValue.toDoubleOrNull() ?: 0.0
            val raw = if (discountType == "PERCENT") {
                totalCartAmount * (inputNum / 100.0)
            } else {
                inputNum
            }
            raw.coerceIn(0.0, totalCartAmount)
        }
    }

    val finalNetBillAmount = (totalCartAmount - calculatedDiscount).coerceAtLeast(0.0)
    val auto30PercentAdvance = finalNetBillAmount * 0.30
    val actualAdvancePaid = if (advancePaidInput.isNotBlank()) {
        (advancePaidInput.toDoubleOrNull() ?: 0.0).coerceIn(0.0, finalNetBillAmount)
    } else if (isCashOnDelivery) {
        0.0
    } else {
        auto30PercentAdvance
    }
    val remainingBalance = (finalNetBillAmount - actualAdvancePaid).coerceAtLeast(0.0)

    val categories = listOf(
        "All" to "सभी",
        "Biryani & Rice" to "बिरयानी व चावल",
        "Mughlai & Gravy" to "ग्रेवी व करी",
        "Tandoori & Starters" to "स्टार्टर व कबाब",
        "Breads & Roti" to "रोटी व नान",
        "Desserts & Sweets" to "मिठाई व खीर",
        "Catering Add-ons" to "कैटरिंग सर्विस व सेटअप"
    )

    val filteredMenuItems = remember(selectedCategory, selectedDietFilter, searchQuery, menuItems) {
        menuItems.filter { item ->
            val matchCat = if (selectedCategory == "All") true else item.category.contains(selectedCategory, ignoreCase = true)
            val matchDiet = when (selectedDietFilter) {
                "VEG" -> item.foodType == FoodType.VEG
                "NON_VEG" -> item.foodType == FoodType.NON_VEG
                else -> true
            }
            val matchSearch = if (searchQuery.isBlank()) true else {
                item.name.contains(searchQuery, ignoreCase = true) || item.category.contains(searchQuery, ignoreCase = true)
            }
            matchCat && matchDiet && matchSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // --- TOP HEADER WITH STEPPER ---
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Offline Booking (ऑफ़लाइन बुकिंग)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "A1 Huma Kitchen • Counter Booking System",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    if (!isOfflineBookingEnabled) {
                        Surface(color = Color(0xFFFFEBEE), shape = RoundedCornerShape(12.dp)) {
                            Text(
                                "Disabled by Admin ⚠️",
                                color = Color(0xFFC62828),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(12.dp)) {
                            Text(
                                "Booking Active 🟢",
                                color = VegGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3-Step Wizard Navigation Stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Step 1 Chip
                    Surface(
                        onClick = { currentStep = 1 },
                        shape = RoundedCornerShape(8.dp),
                        color = if (currentStep == 1) SaffronPrimary else if (currentStep > 1) Color(0xFFE2E8F0) else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp)) {
                            Text(
                                text = if (currentStep > 1) "1. Menu ✅" else "1. Menu (मेनू)",
                                fontSize = 11.5.sp,
                                fontWeight = if (currentStep == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (currentStep == 1) Color.White else Color(0xFF334155)
                            )
                        }
                    }

                    // Step 2 Chip
                    Surface(
                        onClick = {
                            if (totalCartItemsCount > 0) currentStep = 2
                            else Toast.makeText(context, "Please add items from menu first", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = if (currentStep == 2) SaffronPrimary else if (currentStep > 2) Color(0xFFE2E8F0) else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp)) {
                            Text(
                                text = if (currentStep > 2) "2. Details ✅" else "2. Details (ग्राहक/एडवांस)",
                                fontSize = 11.5.sp,
                                fontWeight = if (currentStep == 2) FontWeight.Bold else FontWeight.Medium,
                                color = if (currentStep == 2) Color.White else Color(0xFF334155)
                            )
                        }
                    }

                    // Step 3 Chip
                    Surface(
                        onClick = {
                            if (generatedInvoiceOrder != null) currentStep = 3
                            else Toast.makeText(context, "Generate bill first in Step 2", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = if (currentStep == 3) VegGreen else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp)) {
                            Text(
                                text = "3. Receipt (रसीद)",
                                fontSize = 11.5.sp,
                                fontWeight = if (currentStep == 3) FontWeight.Bold else FontWeight.Medium,
                                color = if (currentStep == 3) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }

        // --- MAIN BODY CONTENT BASED ON CURRENT STEP ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentStep) {
                // =========================================================================
                // STEP 1: CATEGORY MENU & FOOD ITEM SELECTION (CUSTOMER APP + POS EXPERIENCE)
                // =========================================================================
                1 -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Search & Veg/Non-Veg Filter Bar
                        Surface(
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Search dishes: Biryani, Korma, Paneer, Naan...", fontSize = 12.sp) },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = SaffronPrimary, modifier = Modifier.size(18.dp)) },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { searchQuery = "" }) {
                                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("offline_menu_search"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SaffronPrimary,
                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Diet Filter Buttons (All / Veg / Non-Veg / Add-ons)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    listOf(
                                        Triple("ALL", "All Dishes (सभी)", Color(0xFF1E293B)),
                                        Triple("VEG", "Pure Veg 🟢", VegGreen),
                                        Triple("NON_VEG", "Non-Veg 🔴", Color(0xFFD32F2F)),
                                        Triple("ADD_ONS", "Add-ons ✨", Color(0xFFB45309))
                                    ).forEach { (code, label, color) ->
                                        val isSelected = if (code == "ADD_ONS") {
                                            selectedDietFilter == "ADD_ONS" || selectedCategory == "Catering Add-ons"
                                        } else {
                                            selectedDietFilter == code && selectedCategory != "Catering Add-ons"
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(
                                                    if (isSelected) {
                                                        if (code == "ADD_ONS") Color(0xFFFEF3C7) else color.copy(alpha = 0.15f)
                                                    } else Color(0xFFF1F5F9)
                                                )
                                                .border(
                                                    width = if (isSelected) 1.5.dp else 1.dp,
                                                    color = if (isSelected) color else Color.Transparent,
                                                    shape = RoundedCornerShape(20.dp)
                                                )
                                                .clickable {
                                                    if (code == "ADD_ONS") {
                                                        selectedDietFilter = "ADD_ONS"
                                                        selectedCategory = "Catering Add-ons"
                                                    } else {
                                                        selectedDietFilter = code
                                                        if (selectedCategory == "Catering Add-ons") {
                                                            selectedCategory = "All"
                                                        }
                                                    }
                                                }
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = label,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) color else Color(0xFF475569)
                                                )
                                                if (code == "ADD_ONS" && (selectedAddOns.isNotEmpty() || customAddonItems.isNotEmpty())) {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Surface(
                                                        color = Color(0xFFB45309),
                                                        shape = CircleShape
                                                    ) {
                                                        Text(
                                                            text = "${selectedAddOns.size + customAddonItems.size}",
                                                            color = Color.White,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Horizontal Category Scrollable Tabs (Customer App Style)
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    contentPadding = PaddingValues(bottom = 2.dp)
                                ) {
                                    items(categories) { (catKey, catHindi) ->
                                        val isSelected = selectedCategory == catKey
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(if (isSelected) SaffronPrimary else Color(0xFFF1F5F9))
                                                .clickable {
                                                    selectedCategory = catKey
                                                    if (catKey == "Catering Add-ons") {
                                                        selectedDietFilter = "ADD_ONS"
                                                    } else if (selectedDietFilter == "ADD_ONS") {
                                                        selectedDietFilter = "ALL"
                                                    }
                                                }
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = catKey,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else Color(0xFF334155)
                                                )
                                                Text(
                                                    text = catHindi,
                                                    fontSize = 9.sp,
                                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else Color.Gray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Dish Items List (Customer App & POS Item Cards)
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            if (selectedCategory == "Catering Add-ons" || selectedDietFilter == "ADD_ONS") {
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${availableAddonServices.size} Add-On Services (स्टाफ व सेटअप)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF64748B)
                                        )
                                        if (totalCartItemsCount > 0) {
                                            TextButton(onClick = {
                                                offlineCart.clear()
                                                customCartItems.clear()
                                                selectedAddOns.clear()
                                                customAddonItems.clear()
                                            }) {
                                                Text("Clear Cart 🗑️", fontSize = 11.sp, color = Color(0xFFD32F2F))
                                            }
                                        }
                                    }

                                    Surface(
                                        color = Color(0xFFFFFBEB),
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFED7AA)),
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("✨", fontSize = 22.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    "Catering Add-On Services (अतिरिक्त सेवाएं)",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF92400E)
                                                )
                                                Text(
                                                    "Biryani servers, disposable plates, extra raita/salan & shahi mukhwas",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFFB45309)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                items(availableAddonServices) { addOn ->
                                    val currentQty = selectedAddOns[addOn.id] ?: 0.0
                                    val isAdded = currentQty > 0
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .border(
                                                width = if (isAdded) 1.5.dp else 0.5.dp,
                                                color = if (isAdded) VegGreen else Color(0xFFE2E8F0),
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isAdded) Color(0xFFF0FDF4) else Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(44.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(0xFFFFF7ED)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(addOn.icon, fontSize = 22.sp)
                                                    }
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Column {
                                                        Text(
                                                            text = addOn.name,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 14.sp,
                                                            color = Color(0xFF1E293B)
                                                        )
                                                        Text(
                                                            text = addOn.hindiName,
                                                            fontSize = 11.sp,
                                                            color = Color(0xFFB45309),
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                        Spacer(modifier = Modifier.height(3.dp))
                                                        Row(
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = "₹${addOn.price.toInt()} / ${addOn.unit}",
                                                                fontWeight = FontWeight.ExtraBold,
                                                                fontSize = 13.sp,
                                                                color = VegGreen
                                                            )
                                                            Surface(
                                                                color = Color(0xFFF1F5F9),
                                                                shape = RoundedCornerShape(4.dp)
                                                            ) {
                                                                Text(
                                                                    text = "• ${addOn.servesText}",
                                                                    fontSize = 10.sp,
                                                                    color = Color(0xFF475569),
                                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                        }
                                                        if (addOn.description.isNotBlank()) {
                                                            Spacer(modifier = Modifier.height(3.dp))
                                                            Text(
                                                                text = addOn.description,
                                                                fontSize = 11.sp,
                                                                color = Color.Gray,
                                                                maxLines = 2
                                                            )
                                                        }
                                                    }
                                                }

                                                if (isAdded) {
                                                    Surface(
                                                        color = VegGreen,
                                                        shape = RoundedCornerShape(16.dp),
                                                        modifier = Modifier.height(34.dp)
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            modifier = Modifier.padding(horizontal = 4.dp)
                                                        ) {
                                                            IconButton(
                                                                onClick = {
                                                                    if (currentQty <= 1.0) {
                                                                        selectedAddOns.remove(addOn.id)
                                                                    } else {
                                                                        selectedAddOns[addOn.id] = currentQty - 1.0
                                                                    }
                                                                },
                                                                modifier = Modifier.size(26.dp)
                                                            ) {
                                                                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White, modifier = Modifier.size(14.dp))
                                                            }
                                                            Text(
                                                                text = "${currentQty.toInt()}",
                                                                color = Color.White,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 13.sp,
                                                                modifier = Modifier.padding(horizontal = 6.dp)
                                                            )
                                                            IconButton(
                                                                onClick = {
                                                                    selectedAddOns[addOn.id] = currentQty + 1.0
                                                                },
                                                                modifier = Modifier.size(26.dp)
                                                            ) {
                                                                Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White, modifier = Modifier.size(14.dp))
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    Button(
                                                        onClick = {
                                                            selectedAddOns[addOn.id] = 1.0
                                                            Toast.makeText(context, "Added ${addOn.name}", Toast.LENGTH_SHORT).show()
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                                                        shape = RoundedCornerShape(16.dp),
                                                        modifier = Modifier.height(34.dp).testTag("add_addon_offline_${addOn.id}")
                                                    ) {
                                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("ADD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${filteredMenuItems.size} Dishes in ${if (selectedCategory == "All") "Entire Menu" else selectedCategory}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF64748B)
                                        )
                                        if (totalCartItemsCount > 0) {
                                            TextButton(onClick = {
                                                offlineCart.clear()
                                                customCartItems.clear()
                                                selectedAddOns.clear()
                                                customAddonItems.clear()
                                            }) {
                                                Text("Clear Cart 🗑️", fontSize = 11.sp, color = Color(0xFFD32F2F))
                                            }
                                        }
                                    }
                                }

                                items(filteredMenuItems) { item ->
                                val currentQty = offlineCart[item.id] ?: 0.0
                                val isInCart = currentQty > 0
                                val unitLabel = when (item.unitType) {
                                    UnitType.KG -> "Kg"
                                    UnitType.PORTION -> "Portion"
                                    UnitType.DOZEN -> "Dozen"
                                    UnitType.LITRE -> "Litre"
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .border(
                                            width = if (isInCart) 1.5.dp else 0.5.dp,
                                            color = if (isInCart) SaffronPrimary else Color(0xFFE2E8F0),
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isInCart) Color(0xFFFFFBF5) else Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                                                if (item.imageUrl.isNotBlank()) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(48.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color(0xFFFFF3E0)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        AsyncImage(
                                                            model = item.imageUrl,
                                                            contentDescription = item.name,
                                                            modifier = Modifier.fillMaxSize(),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                }

                                                // Veg / Non-Veg Indicator Icon
                                                Surface(
                                                    color = Color.Transparent,
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (item.foodType == FoodType.VEG) VegGreen else Color(0xFFD32F2F)),
                                                    shape = RoundedCornerShape(3.dp),
                                                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Surface(
                                                            color = if (item.foodType == FoodType.VEG) VegGreen else Color(0xFFD32F2F),
                                                            shape = CircleShape,
                                                            modifier = Modifier.size(8.dp)
                                                        ) {}
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(10.dp))

                                                Column {
                                                    Text(
                                                        text = item.name,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp,
                                                        color = Color(0xFF1E293B)
                                                    )
                                                    Text(
                                                        text = item.category,
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF64748B)
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = "₹${item.pricePerUnit}",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 14.sp,
                                                            color = SaffronPrimary
                                                        )
                                                        Text(
                                                            text = " / $unitLabel",
                                                            fontSize = 12.sp,
                                                            color = Color.Gray
                                                        )
                                                        if (item.minQuantity > 1.0) {
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp)) {
                                                                val minQtyFormatted = if (item.minQuantity % 1.0 == 0.0) "${item.minQuantity.toInt()}" else String.format(java.util.Locale.ENGLISH, "%.1f", item.minQuantity)
                                                                Text(
                                                                    text = "Min $minQtyFormatted $unitLabel",
                                                                    fontSize = 9.sp,
                                                                    color = Color(0xFF475569),
                                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            // Item Action: Add or Stepper
                                            if (isInCart) {
                                                val step = if (item.unitType == UnitType.KG || item.unitType == UnitType.LITRE) 0.5 else 1.0
                                                val formattedQty = if (currentQty % 1.0 == 0.0) "${currentQty.toInt()}" else String.format(java.util.Locale.ENGLISH, "%.1f", currentQty)

                                                Column(horizontalAlignment = Alignment.End) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(20.dp))
                                                            .background(SaffronPrimary)
                                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                                    ) {
                                                        IconButton(
                                                            onClick = {
                                                                val rawNext = currentQty - step
                                                                val next = kotlin.math.round(rawNext * 10.0) / 10.0
                                                                if (next <= 0.0) offlineCart.remove(item.id)
                                                                else offlineCart[item.id] = next
                                                            },
                                                            modifier = Modifier.size(28.dp)
                                                        ) {
                                                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White, modifier = Modifier.size(16.dp))
                                                        }

                                                        Text(
                                                            text = "$formattedQty $unitLabel",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 12.sp,
                                                            color = Color.White,
                                                            modifier = Modifier.padding(horizontal = 6.dp)
                                                        )

                                                        IconButton(
                                                            onClick = {
                                                                val rawNext = currentQty + step
                                                                val next = kotlin.math.round(rawNext * 10.0) / 10.0
                                                                offlineCart[item.id] = next
                                                            },
                                                            modifier = Modifier.size(28.dp)
                                                        ) {
                                                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White, modifier = Modifier.size(16.dp))
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = "= ₹${String.format("%.0f", currentQty * item.pricePerUnit)}",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = VegGreen
                                                    )
                                                }
                                            } else {
                                                Button(
                                                    onClick = {
                                                        offlineCart[item.id] = if (item.minQuantity >= 0.5) item.minQuantity else 1.0
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                                    shape = RoundedCornerShape(16.dp),
                                                    modifier = Modifier.height(34.dp).testTag("add_offline_${item.id}")
                                                ) {
                                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("ADD (1 $unitLabel)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        // Quick bulk deg & weight chips for catering (0.5kg, 1kg, 1.5kg, 2kg, 2.5kg, 3kg, 3.5kg, 5kg, 10kg, 15kg, 25kg, 50kg)
                                        if (isInCart && item.unitType == UnitType.KG) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Divider(color = Color(0xFFF1F5F9))
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .horizontalScroll(rememberScrollState()),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Qty / Degs:", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                                listOf(0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 5.0, 10.0, 15.0, 25.0, 50.0).forEach { degQty ->
                                                    val isDegSelected = kotlin.math.abs(currentQty - degQty) < 0.05
                                                    val label = if (degQty % 1.0 == 0.0) "${degQty.toInt()} Kg" else "$degQty Kg"
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .background(if (isDegSelected) SaffronPrimary else Color(0xFFF1F5F9))
                                                            .clickable { offlineCart[item.id] = degQty }
                                                            .padding(horizontal = 7.dp, vertical = 3.dp)
                                                    ) {
                                                        Text(
                                                            text = label,
                                                            fontSize = 10.sp,
                                                            fontWeight = if (isDegSelected) FontWeight.Bold else FontWeight.Normal,
                                                            color = if (isDegSelected) Color.White else Color(0xFF475569)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                }
                            }

                            // Custom Line Item Section (e.g. Extra Degs, Service boys, Transport)
                            item {
                                Spacer(modifier = Modifier.height(14.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "+ Add Custom Service / Item (कस्टम आइटम जोड़ें)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = "Add extra catering services like Service Staff, Live Halwai, Transport, etc. (Note: Deg/Bartan bhade par nahi dena hai, containers are kitchen property to be returned).",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = customItemName,
                                                onValueChange = { customItemName = it },
                                                placeholder = { Text("Service (e.g. 2 Service Waiters)", fontSize = 11.sp) },
                                                singleLine = true,
                                                modifier = Modifier.weight(2f).height(48.dp),
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                            )

                                            OutlinedTextField(
                                                value = customItemPrice,
                                                onValueChange = { customItemPrice = it },
                                                placeholder = { Text("₹ Amount", fontSize = 11.sp) },
                                                singleLine = true,
                                                modifier = Modifier.weight(1.1f).height(48.dp),
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                            )

                                            Button(
                                                onClick = {
                                                    val pr = customItemPrice.toDoubleOrNull() ?: 0.0
                                                    if (customItemName.isNotBlank() && pr > 0) {
                                                        customCartItems.add(Pair(customItemName.trim(), pr))
                                                        customItemName = ""
                                                        customItemPrice = ""
                                                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(44.dp)
                                            ) {
                                                Text("+ Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(80.dp)) // Padding for sticky bottom bar
                            }
                        }

                        // --- STICKY BOTTOM POS CART BAR ---
                        Surface(
                            color = Color(0xFF1E293B),
                            shadowElevation = 8.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "$totalCartItemsCount Items Selected",
                                            color = Color.LightGray,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Text(
                                        text = "₹${String.format("%.2f", totalCartAmount)}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (totalCartItemsCount > 0) {
                                            currentStep = 2
                                        } else {
                                            Toast.makeText(context, "Please add at least 1 dish to continue", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    enabled = totalCartItemsCount > 0,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SaffronPrimary,
                                        disabledContainerColor = Color.DarkGray
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(44.dp).testTag("proceed_to_booking_details")
                                ) {
                                    Text("Next: Booking Details ➔", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // STEP 2: CUSTOMER, EVENT & FINANCIAL DETAILS (POS CHECKOUT FORM)
                // =========================================================================
                2 -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        item {
                            // Back Button to Step 1
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { currentStep = 1 }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("⬅️ Back to Menu & Dishes (मेनू बदलें)", color = SaffronPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Selected Items Breakdown Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🛒 Selected Menu Items ($totalCartItemsCount)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                                        Text("₹${String.format("%.2f", totalCartAmount)}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SaffronPrimary)
                                    }
                                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                                    offlineCart.forEach { (itemId, qty) ->
                                        val item = menuItems.find { it.id == itemId }
                                        val total = (item?.pricePerUnit ?: 0.0) * qty
                                        val unitLabel = when (item?.unitType) {
                                            UnitType.KG -> "Kg"
                                            UnitType.PORTION -> "Portion"
                                            UnitType.DOZEN -> "Dozen"
                                            UnitType.LITRE -> "Litre"
                                            null -> "Unit"
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(
                                                    onClick = { offlineCart.remove(itemId) },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                                }
                                                Spacer(modifier = Modifier.width(4.dp))
                                                val formattedQty = if (qty % 1.0 == 0.0) "${qty.toInt()}" else String.format(java.util.Locale.ENGLISH, "%.1f", qty)
                                                Text("${item?.name ?: "Item"} ($formattedQty $unitLabel)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                            }
                                            Text("₹${String.format("%.0f", total)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    customCartItems.forEachIndexed { index, (cName, cPrice) ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(
                                                    onClick = { customCartItems.removeAt(index) },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                                }
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("$cName (Custom)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = SaffronPrimary)
                                            }
                                            Text("₹${String.format("%.0f", cPrice)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    selectedAddOns.forEach { (addOnId, qty) ->
                                        val addOn = availableAddonServices.find { it.id == addOnId }
                                        val total = (addOn?.price ?: 0.0) * qty
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(
                                                    onClick = { selectedAddOns.remove(addOnId) },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                                }
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("${addOn?.icon ?: "✨"} ${addOn?.name ?: "Add-on"} (${qty.toInt()}x)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFFB45309))
                                            }
                                            Text("₹${String.format("%.0f", total)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    customAddonItems.forEachIndexed { index, (aName, aPrice) ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(
                                                    onClick = { customAddonItems.removeAt(index) },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                                }
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("✨ $aName (Custom Service)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFFB45309))
                                            }
                                            Text("₹${String.format("%.0f", aPrice)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // 1. Customer Search & Information Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("1. Customer Details (ग्राहक की जानकारी)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Phone Number Auto-Lookup
                                    OutlinedTextField(
                                        value = customerMobile,
                                        onValueChange = { searchCustomerByPhone(it) },
                                        label = { Text("Customer Mobile Number (मोबाइल नंबर ऑटो खोजें)") },
                                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = SaffronPrimary) },
                                        trailingIcon = {
                                            if (isCustomerFound) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = "Found", tint = VegGreen)
                                            } else {
                                                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
                                            }
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth().testTag("offline_mobile_input"),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                    )

                                    if (isCustomerFound) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            color = Color(0xFFE8F5E9),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "✓ Found Existing Customer: $customerName (पुराना ग्राहक मिला - विवरण ऑटो भरा गया)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = VegGreen,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Name
                                    OutlinedTextField(
                                        value = customerName,
                                        onValueChange = { customerName = it },
                                        label = { Text("Customer Full Name (ग्राहक का नाम) *") },
                                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SaffronPrimary) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth().testTag("offline_name_input"),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Alternate Mobile (optional)
                                    OutlinedTextField(
                                        value = customerAltMobile,
                                        onValueChange = { customerAltMobile = it },
                                        label = { Text("Alternate / WhatsApp Mobile (वैकल्पिक)") },
                                        leadingIcon = { Icon(Icons.Default.Call, contentDescription = null, tint = Color.Gray) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // 2. Event Schedule, Date & Time Slot Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("2. Event Date & Slot (कार्यक्रम तारीख व समय)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = eventDate,
                                            onValueChange = { eventDate = it },
                                            label = { Text("Event Date (YYYY-MM-DD)") },
                                            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = SaffronPrimary) },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                        )

                                        Button(
                                            onClick = {
                                                val c = Calendar.getInstance()
                                                val dpd = DatePickerDialog(
                                                    context,
                                                    { _, year, month, dayOfMonth ->
                                                        eventDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                                                    },
                                                    c.get(Calendar.YEAR),
                                                    c.get(Calendar.MONTH),
                                                    c.get(Calendar.DAY_OF_MONTH)
                                                )
                                                dpd.show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(50.dp)
                                        ) {
                                            Text("Pick 🗓️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Quick Date Chips
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        val cal1 = Calendar.getInstance()
                                        val dToday = dateFormat.format(cal1.time)
                                        cal1.add(Calendar.DAY_OF_YEAR, 1)
                                        val dTomorrow = dateFormat.format(cal1.time)
                                        cal1.add(Calendar.DAY_OF_YEAR, 1)
                                        val dDayAfter = dateFormat.format(cal1.time)

                                        listOf(
                                            dToday to "Today (आज)",
                                            dTomorrow to "Tomorrow (कल)",
                                            dDayAfter to "Day After"
                                        ).forEach { (dt, label) ->
                                            val isSelected = eventDate == dt
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (isSelected) SaffronPrimary else Color(0xFFF1F5F9))
                                                    .clickable { eventDate = dt }
                                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                            ) {
                                                Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color(0xFF334155))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = eventTimeSlot,
                                        onValueChange = { eventTimeSlot = it },
                                        label = { Text("Delivery Time Slot / डिलीवरी समय") },
                                        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = SaffronPrimary) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Slot Presets
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        availableOfflineSlots.take(3).forEach { slot ->
                                            val isSelected = eventTimeSlot == slot
                                            val shortLabel = try {
                                                val parts = slot.split("-")
                                                parts[0].trim() + " - " + parts[1].trim().split(" ")[0]
                                            } catch (e: Exception) { slot }
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (isSelected) SaffronPrimary else Color(0xFFF1F5F9))
                                                    .clickable { eventTimeSlot = slot }
                                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                            ) {
                                                Text(shortLabel, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color(0xFF334155))
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // 3. Venue Address & Logistics Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("3. Venue & Logistics (कार्यक्रम स्थान व बर्तन)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = customerAddress,
                                        onValueChange = { customerAddress = it },
                                        label = { Text("Delivery Venue Address (कार्यक्रम स्थल का पता) *") },
                                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = SaffronPrimary) },
                                        modifier = Modifier.fillMaxWidth().testTag("offline_address_input"),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Delivery Mode Toggle
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isDeliveryRequired) SaffronPrimary.copy(alpha = 0.15f) else Color(0xFFF1F5F9))
                                                .border(1.dp, if (isDeliveryRequired) SaffronPrimary else Color.Transparent, RoundedCornerShape(10.dp))
                                                .clickable { isDeliveryRequired = true }
                                                .padding(10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Kitchen Delivery 🚴", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDeliveryRequired) SaffronPrimary else Color(0xFF475569))
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (!isDeliveryRequired) SaffronPrimary.copy(alpha = 0.15f) else Color(0xFFF1F5F9))
                                                .border(1.dp, if (!isDeliveryRequired) SaffronPrimary else Color.Transparent, RoundedCornerShape(10.dp))
                                                .clickable { isDeliveryRequired = false }
                                                .padding(10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Self Pickup at Kitchen 🛍️", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (!isDeliveryRequired) SaffronPrimary else Color(0xFF475569))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Bartan / Deg Return Tracking Checkbox
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isMetalDegGiven,
                                            onCheckedChange = { isMetalDegGiven = it },
                                            colors = CheckboxDefaults.colors(checkedColor = SaffronPrimary)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Column {
                                            Text("🍲 Metal Handi / Degs Delivery (देग वापसी आवश्यक)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                            Text("Deg/Bartan bhade par nahi dena hai. Food container return mangana hai (9:30 AM daily notification & delivery boy return task)", fontSize = 10.sp, color = Color(0xFFB45309))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Cooking / Delivery Notes
                                    OutlinedTextField(
                                        value = cookingNotes,
                                        onValueChange = { cookingNotes = it },
                                        label = { Text("Special Cooking / Delivery Notes (e.g. Mild Spicy, Hot Degs)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // 3B. Catering Add-On Services (अतिरिक्त कैटरिंग सेवाएं व स्टाफ)
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("✨", fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    "3B. Add-On Services (अतिरिक्त सेवाएं व स्टाफ)",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = Color(0xFF1E293B)
                                                )
                                                Text(
                                                    "Serving staff, disposable crockery, extra salan/raita",
                                                    fontSize = 11.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }

                                        if (selectedAddOns.isNotEmpty() || customAddonItems.isNotEmpty()) {
                                            Surface(
                                                color = Color(0xFFFEF3C7),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = "₹${addOnsTotal.toInt()} added",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFB45309),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // List of available add-ons with quick selection toggles/counters
                                    availableAddonServices.forEach { addOn ->
                                        val currentQty = selectedAddOns[addOn.id] ?: 0.0
                                        val isSelected = currentQty > 0

                                        Surface(
                                            color = if (isSelected) Color(0xFFF0FDF4) else Color(0xFFF8FAFC),
                                            shape = RoundedCornerShape(10.dp),
                                            border = androidx.compose.foundation.BorderStroke(
                                                if (isSelected) 1.5.dp else 1.dp,
                                                if (isSelected) VegGreen else Color(0xFFE2E8F0)
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    modifier = Modifier.weight(1f),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(addOn.icon, fontSize = 22.sp)
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Column {
                                                        Text(
                                                            text = addOn.name,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 13.sp,
                                                            color = Color(0xFF1E293B)
                                                        )
                                                        Text(
                                                            text = "${addOn.hindiName} • ₹${addOn.price.toInt()} / ${addOn.unit} (${addOn.servesText})",
                                                            fontSize = 11.sp,
                                                            color = if (isSelected) VegGreen else Color(0xFF64748B),
                                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                                        )
                                                    }
                                                }

                                                if (isSelected) {
                                                    Surface(
                                                        color = VegGreen,
                                                        shape = RoundedCornerShape(14.dp)
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                        ) {
                                                            IconButton(
                                                                onClick = {
                                                                    if (currentQty <= 1.0) selectedAddOns.remove(addOn.id)
                                                                    else selectedAddOns[addOn.id] = currentQty - 1.0
                                                                },
                                                                modifier = Modifier.size(24.dp)
                                                            ) {
                                                                Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                                            }
                                                            Text(
                                                                text = "${currentQty.toInt()}",
                                                                color = Color.White,
                                                                fontSize = 12.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                modifier = Modifier.padding(horizontal = 6.dp)
                                                            )
                                                            IconButton(
                                                                onClick = { selectedAddOns[addOn.id] = currentQty + 1.0 },
                                                                modifier = Modifier.size(24.dp)
                                                            ) {
                                                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    OutlinedButton(
                                                        onClick = { selectedAddOns[addOn.id] = 1.0 },
                                                        shape = RoundedCornerShape(10.dp),
                                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VegGreen),
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, VegGreen),
                                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                        modifier = Modifier.height(30.dp)
                                                    ) {
                                                        Text("+ Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Add Custom On-The-Fly Add-On Service
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "+ Custom Catering Service (अन्य विशेष सेवा जोड़ें):",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF475569)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = customAddonNameInput,
                                            onValueChange = { customAddonNameInput = it },
                                            placeholder = { Text("Service (e.g. VIP Crockery)", fontSize = 11.sp) },
                                            singleLine = true,
                                            modifier = Modifier.weight(2f).height(46.dp),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                        )

                                        OutlinedTextField(
                                            value = customAddonPriceInput,
                                            onValueChange = { customAddonPriceInput = it },
                                            placeholder = { Text("₹ Cost", fontSize = 11.sp) },
                                            singleLine = true,
                                            modifier = Modifier.weight(1.1f).height(46.dp),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                        )

                                        Button(
                                            onClick = {
                                                val pr = customAddonPriceInput.toDoubleOrNull() ?: 0.0
                                                if (customAddonNameInput.isNotBlank() && pr > 0) {
                                                    customAddonItems.add(Pair(customAddonNameInput.trim(), pr))
                                                    customAddonNameInput = ""
                                                    customAddonPriceInput = ""
                                                    Toast.makeText(context, "Added add-on service", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(46.dp)
                                        ) {
                                            Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // 4. Financial & Advance Payment Settlement Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = VegGreen, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("4. Bill & Advance Payment (बिल व एडवांस हिसाब)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Items Subtotal Display with Itemized Breakdown
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Items Breakdown (सामान व दर):", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFF475569))
                                                Text("Price / दर", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF64748B))
                                            }
                                            Divider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFE2E8F0))

                                            var reviewIdx = 1
                                            offlineCart.forEach { (itemId, qty) ->
                                                val item = menuItems.find { it.id == itemId }
                                                val unitLabel = when (item?.unitType) {
                                                    UnitType.KG -> "Kg"
                                                    UnitType.PORTION -> "Portion"
                                                    UnitType.DOZEN -> "Dozen"
                                                    UnitType.LITRE -> "Litre"
                                                    null -> "Unit"
                                                }
                                                val formattedQty = if (qty % 1.0 == 0.0) "${qty.toInt()}" else String.format(java.util.Locale.ENGLISH, "%.1f", qty)
                                                val rate = item?.pricePerUnit ?: 0.0
                                                val itemTot = rate * qty
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        "$reviewIdx. ${item?.name ?: "Item"} ($formattedQty $unitLabel @ ₹${rate.toInt()}/$unitLabel)",
                                                        fontSize = 11.5.sp,
                                                        color = Color(0xFF1E293B),
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Text(
                                                        "₹${String.format(java.util.Locale.ENGLISH, "%.2f", itemTot)}",
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF0F172A)
                                                    )
                                                }
                                                reviewIdx++
                                            }
                                            customCartItems.forEach { (cName, cPrice) ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        "$reviewIdx. $cName (Custom Item)",
                                                        fontSize = 11.5.sp,
                                                        color = Color(0xFF1E293B),
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Text(
                                                        "₹${String.format(java.util.Locale.ENGLISH, "%.2f", cPrice)}",
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF0F172A)
                                                    )
                                                }
                                                reviewIdx++
                                            }
                                            selectedAddOns.forEach { (addOnId, qty) ->
                                                val addOn = availableAddonServices.find { it.id == addOnId }
                                                if (addOn != null) {
                                                    val addOnTot = addOn.price * qty
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            "$reviewIdx. ${addOn.name} (${qty.toInt()}x @ ₹${addOn.price.toInt()})",
                                                            fontSize = 11.5.sp,
                                                            color = Color(0xFF1E293B),
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                        Text(
                                                            "₹${String.format(java.util.Locale.ENGLISH, "%.2f", addOnTot)}",
                                                            fontSize = 11.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF0F172A)
                                                        )
                                                    }
                                                    reviewIdx++
                                                }
                                            }
                                            customAddonItems.forEach { (aName, aPrice) ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        "$reviewIdx. $aName (Add-on)",
                                                        fontSize = 11.5.sp,
                                                        color = Color(0xFF1E293B),
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Text(
                                                        "₹${String.format(java.util.Locale.ENGLISH, "%.2f", aPrice)}",
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF0F172A)
                                                    )
                                                }
                                                reviewIdx++
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Items Subtotal Display
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Food & Dishes Subtotal:", fontSize = 12.5.sp, color = Color(0xFF475569))
                                        Text("₹${String.format(java.util.Locale.ENGLISH, "%.2f", menuItemsTotal + customItemsTotal)}", fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, color = Color(0xFF1E293B))
                                    }
                                    if (addOnsTotal > 0) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Add-On Services (अतिरिक्त सेवाएं) ✨:", fontSize = 12.5.sp, color = Color(0xFFB45309), fontWeight = FontWeight.SemiBold)
                                            Text("+₹${String.format("%.2f", addOnsTotal)}", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color(0xFFB45309))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Total Gross Amount (कुल मूल्य):", fontSize = 13.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Bold)
                                        Text("₹${String.format("%.2f", totalCartAmount)}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // --- DISCOUNT SECTION ---
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isDiscountEnabled && calculatedDiscount > 0) Color(0xFFF0FDF4) else Color(0xFFF8FAFC)
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isDiscountEnabled && calculatedDiscount > 0) Color(0xFF86EFAC) else Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        Icons.Default.LocalOffer,
                                                        contentDescription = null,
                                                        tint = if (isDiscountEnabled) VegGreen else Color.Gray,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Column {
                                                        Text(
                                                            text = "Apply Discount / छूट दें",
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isDiscountEnabled) Color(0xFF166534) else Color(0xFF334155)
                                                        )
                                                        Text(
                                                            text = "Flat ₹ or % discount for customer",
                                                            fontSize = 10.sp,
                                                            color = Color.Gray
                                                        )
                                                    }
                                                }

                                                Switch(
                                                    checked = isDiscountEnabled,
                                                    onCheckedChange = {
                                                        isDiscountEnabled = it
                                                        if (!it) {
                                                            discountInputValue = ""
                                                            discountReason = ""
                                                        }
                                                    },
                                                    colors = SwitchDefaults.colors(
                                                        checkedThumbColor = Color.White,
                                                        checkedTrackColor = VegGreen
                                                    )
                                                )
                                            }

                                            if (isDiscountEnabled) {
                                                Spacer(modifier = Modifier.height(10.dp))

                                                // Discount Type Switcher: Flat ₹ vs Percentage %
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color(0xFFE2E8F0))
                                                        .padding(2.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = if (discountType == "FLAT") Color.White else Color.Transparent,
                                                        shadowElevation = if (discountType == "FLAT") 1.dp else 0.dp,
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clickable {
                                                                discountType = "FLAT"
                                                                discountInputValue = ""
                                                            }
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(vertical = 7.dp),
                                                            horizontalArrangement = Arrangement.Center,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = "₹ Flat Rupee (नकद छूट)",
                                                                fontSize = 11.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (discountType == "FLAT") VegGreen else Color.Gray
                                                            )
                                                        }
                                                    }

                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = if (discountType == "PERCENT") Color.White else Color.Transparent,
                                                        shadowElevation = if (discountType == "PERCENT") 1.dp else 0.dp,
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clickable {
                                                                discountType = "PERCENT"
                                                                discountInputValue = ""
                                                            }
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(vertical = 7.dp),
                                                            horizontalArrangement = Arrangement.Center,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(Icons.Default.Percent, contentDescription = null, modifier = Modifier.size(13.dp), tint = if (discountType == "PERCENT") VegGreen else Color.Gray)
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text(
                                                                text = "% Percent (प्रतिशत छूट)",
                                                                fontSize = 11.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (discountType == "PERCENT") VegGreen else Color.Gray
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(10.dp))

                                                // Discount Input Field
                                                OutlinedTextField(
                                                    value = discountInputValue,
                                                    onValueChange = { input ->
                                                        if (input.all { it.isDigit() || it == '.' }) {
                                                            discountInputValue = input
                                                        }
                                                    },
                                                    label = {
                                                        Text(if (discountType == "FLAT") "Discount Amount / छूट राशि (₹)" else "Discount Percentage / प्रतिशत छूट (%)")
                                                    },
                                                    prefix = {
                                                        Text(
                                                            if (discountType == "FLAT") "₹ " else "% ",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 14.sp,
                                                            color = VegGreen
                                                        )
                                                    },
                                                    placeholder = {
                                                        Text(if (discountType == "FLAT") "e.g. 500" else "e.g. 10", fontSize = 12.sp)
                                                    },
                                                    singleLine = true,
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.fillMaxWidth().testTag("offline_discount_input"),
                                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VegGreen)
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                // Quick Presets
                                                Text("Quick Discount Presets (क्विक छूट):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                                                Spacer(modifier = Modifier.height(4.dp))

                                                LazyRow(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    if (discountType == "FLAT") {
                                                        val flatPresets = listOf(200, 500, 1000, 1500, 2000)
                                                        items(flatPresets) { amt ->
                                                            val isSelected = discountInputValue == amt.toString()
                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(8.dp))
                                                                    .background(if (isSelected) VegGreen else Color(0xFFDCFCE7))
                                                                    .clickable { discountInputValue = amt.toString() }
                                                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                                            ) {
                                                                Text("₹$amt Off", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color(0xFF15803D))
                                                            }
                                                        }
                                                    } else {
                                                        val percentPresets = listOf(5, 10, 15, 20)
                                                        items(percentPresets) { pct ->
                                                            val isSelected = discountInputValue == pct.toString()
                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(8.dp))
                                                                    .background(if (isSelected) VegGreen else Color(0xFFDCFCE7))
                                                                    .clickable { discountInputValue = pct.toString() }
                                                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                                            ) {
                                                                Text("$pct% Off", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color(0xFF15803D))
                                                            }
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))

                                                // Discount Reason / Notes (Optional)
                                                OutlinedTextField(
                                                    value = discountReason,
                                                    onValueChange = { discountReason = it },
                                                    label = { Text("Discount Reason / Note (कारण - optional)", fontSize = 11.sp) },
                                                    placeholder = { Text("e.g. Regular Customer, Bulk Booking Deal, Relative", fontSize = 11.sp) },
                                                    singleLine = true,
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VegGreen)
                                                )

                                                if (calculatedDiscount > 0) {
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Surface(
                                                        color = Color(0xFFDCFCE7),
                                                        shape = RoundedCornerShape(6.dp),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = "🎉 Applied: ${if (discountType == "PERCENT") "$discountInputValue% Discount" else "Flat ₹$discountInputValue Off"}",
                                                                fontSize = 11.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFF166534)
                                                            )
                                                            Text(
                                                                text = "-₹${String.format("%.2f", calculatedDiscount)}",
                                                                fontSize = 12.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFF166534)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Discount Applied Row (if any)
                                    if (calculatedDiscount > 0) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Discount Deducted (छूट घटी):", fontSize = 12.5.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                                            Text("-₹${String.format("%.2f", calculatedDiscount)}", fontSize = 13.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }

                                    // Net Final Order Amount Display
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Net Final Order Bill (अंतिम कुल बिल):", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color(0xFF1E293B))
                                            if (calculatedDiscount > 0) {
                                                Text("After ₹${calculatedDiscount.toInt()} discount", fontSize = 10.sp, color = VegGreen)
                                            }
                                        }
                                        Text("₹${String.format(java.util.Locale.ENGLISH, "%.2f", finalNetBillAmount)}", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = SaffronPrimary)
                                    }

                                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                                    // --- CASH ON DELIVERY (COD) OPTIONAL TOGGLE CARD ---
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isCashOnDelivery) Color(0xFFEFF6FF) else Color(0xFFF8FAFC)
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isCashOnDelivery) Color(0xFF2563EB) else Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    val newCOD = !isCashOnDelivery
                                                    isCashOnDelivery = newCOD
                                                    if (newCOD) {
                                                        selectedPaymentMethod = PaymentMethod.CASH_ON_DELIVERY
                                                        if (advancePaidInput.isBlank()) advancePaidInput = "0"
                                                    }
                                                }
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = "Cash on Delivery (COD) 🚚",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = if (isCashOnDelivery) Color(0xFF1D4ED8) else Color(0xFF1E293B)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(
                                                        color = if (isCashOnDelivery) Color(0xFFDBEAFE) else Color(0xFFF1F5F9),
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            text = if (isCashOnDelivery) "COD ACTIVE" else "OPTIONAL",
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isCashOnDelivery) Color(0xFF1D4ED8) else Color(0xFF64748B),
                                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = if (isCashOnDelivery)
                                                        "Advance optional hai (₹0 advance chalega). Baaki/poora bill delivery ke samay Rider/Delivery Boy cash collect karega."
                                                    else
                                                        "Customer delivery par payment karega toh ise ON karein (Advance optional).",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF64748B),
                                                    lineHeight = 15.sp
                                                )
                                            }
                                            Switch(
                                                checked = isCashOnDelivery,
                                                onCheckedChange = { checked ->
                                                    isCashOnDelivery = checked
                                                    if (checked) {
                                                        selectedPaymentMethod = PaymentMethod.CASH_ON_DELIVERY
                                                        if (advancePaidInput.isBlank()) advancePaidInput = "0"
                                                    }
                                                },
                                                colors = SwitchDefaults.colors(
                                                    checkedThumbColor = Color(0xFF2563EB),
                                                    checkedTrackColor = Color(0xFF93C5FD)
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Advance Payment Input
                                    OutlinedTextField(
                                        value = advancePaidInput,
                                        onValueChange = { advancePaidInput = it },
                                        label = {
                                            Text(
                                                if (isCashOnDelivery) "Advance Received (Optional / वैकल्पिक - ₹0 for COD)"
                                                else "Advance Received / एडवांस मिला (₹)"
                                            )
                                        },
                                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = VegGreen) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth().testTag("offline_advance_input"),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VegGreen)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Quick Advance Presets Chips (Based on finalNetBillAmount)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        // ₹0 Full COD Chip
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (actualAdvancePaid == 0.0) Color(0xFFDBEAFE) else Color(0xFFF1F5F9))
                                                .clickable {
                                                    isCashOnDelivery = true
                                                    selectedPaymentMethod = PaymentMethod.CASH_ON_DELIVERY
                                                    advancePaidInput = "0"
                                                }
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                "₹0 (Full COD 🚚)",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (actualAdvancePaid == 0.0) Color(0xFF1D4ED8) else Color(0xFF475569)
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFFE8F5E9))
                                                .clickable {
                                                    advancePaidInput = String.format(java.util.Locale.ENGLISH, "%.0f", finalNetBillAmount * 0.30)
                                                }
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text("30% (₹${(finalNetBillAmount * 0.30).toInt()})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFFE8F5E9))
                                                .clickable {
                                                    advancePaidInput = String.format(java.util.Locale.ENGLISH, "%.0f", finalNetBillAmount * 0.50)
                                                }
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text("50% (₹${(finalNetBillAmount * 0.50).toInt()})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(VegGreen)
                                                .clickable {
                                                    isCashOnDelivery = false
                                                    advancePaidInput = String.format(java.util.Locale.ENGLISH, "%.0f", finalNetBillAmount)
                                                }
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text("100% Full Paid", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Payment Method Selector
                                    Text(
                                        text = if (isCashOnDelivery && actualAdvancePaid == 0.0) "Payment Mode: Cash on Delivery (COD 🚚)"
                                        else "Payment / Advance Received Via (भुगतान माध्यम):",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.DarkGray
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        val modes = listOf(
                                            Pair(PaymentMethod.CASH_ON_DELIVERY, "Cash / COD 💵"),
                                            Pair(PaymentMethod.UPI, "UPI 📲"),
                                            Pair(PaymentMethod.CARD, "Card 💳")
                                        )
                                        modes.forEach { (mode, label) ->
                                            val isSelected = selectedPaymentMethod == mode
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSelected) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                                                    .clickable {
                                                        selectedPaymentMethod = mode
                                                        if (mode == PaymentMethod.CASH_ON_DELIVERY) {
                                                            isCashOnDelivery = true
                                                        }
                                                    }
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Color.White else Color(0xFF334155))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Calculated Remaining Balance Box
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (remainingBalance <= 0) Color(0xFFE8F5E9)
                                            else if (isCashOnDelivery) Color(0xFFEFF6FF)
                                            else Color(0xFFFFF3E0)
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = if (isCashOnDelivery && actualAdvancePaid == 0.0) "Full Amount Due on Delivery (डिलीवरी पर देय):"
                                                    else "Remaining Balance Due / बकाया राशि:",
                                                    fontSize = 11.sp,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    text = "₹${String.format(java.util.Locale.ENGLISH, "%.2f", remainingBalance)}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp,
                                                    color = if (remainingBalance <= 0) VegGreen
                                                    else if (isCashOnDelivery) Color(0xFF1D4ED8)
                                                    else Color(0xFFD32F2F)
                                                )
                                            }
                                            Surface(
                                                color = if (remainingBalance <= 0) VegGreen
                                                else if (isCashOnDelivery) Color(0xFF2563EB)
                                                else Color(0xFFE65100),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    text = if (remainingBalance <= 0) "FULL PAID ✅"
                                                    else if (isCashOnDelivery) "COD / ON DELIVERY 🚚"
                                                    else "BALANCE DUE ⏳",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Submit Order Button
                        item {
                            Button(
                                onClick = {
                                    if (customerName.isBlank()) {
                                        Toast.makeText(context, "Please enter Customer Name", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (customerAddress.isBlank()) {
                                        Toast.makeText(context, "Please enter Delivery Venue Address", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    val summaryList = mutableListOf<String>()
                                    var sNo = 1
                                    offlineCart.forEach { (itemId, qty) ->
                                        val item = menuItems.find { it.id == itemId }
                                        val unitLabel = when (item?.unitType) {
                                            UnitType.KG -> "Kg"
                                            UnitType.PORTION -> "Portion"
                                            UnitType.DOZEN -> "Dozen"
                                            UnitType.LITRE -> "Litre"
                                            null -> "Unit"
                                        }
                                        val formattedQty = if (qty % 1.0 == 0.0) "${qty.toInt()}" else String.format(java.util.Locale.ENGLISH, "%.1f", qty)
                                        val rate = item?.pricePerUnit ?: 0.0
                                        val itemTotal = rate * qty
                                        val itemName = item?.name ?: "Item"
                                        summaryList.add("$sNo. $itemName ($formattedQty $unitLabel @ ₹${rate.toInt()}/$unitLabel) = ₹${String.format(java.util.Locale.ENGLISH, "%.2f", itemTotal)}")
                                        sNo++
                                    }
                                    customCartItems.forEach { (cName, cPrice) ->
                                        summaryList.add("$sNo. $cName (Custom Item) = ₹${String.format(java.util.Locale.ENGLISH, "%.2f", cPrice)}")
                                        sNo++
                                    }
                                    selectedAddOns.forEach { (addOnId, qty) ->
                                        val addOn = availableAddonServices.find { it.id == addOnId }
                                        if (addOn != null) {
                                            val addOnTot = addOn.price * qty
                                            summaryList.add("$sNo. ${addOn.icon} ${addOn.name} (${qty.toInt()}x @ ₹${addOn.price.toInt()}) = ₹${String.format(java.util.Locale.ENGLISH, "%.2f", addOnTot)}")
                                            sNo++
                                        }
                                    }
                                    customAddonItems.forEach { (aName, aPrice) ->
                                        summaryList.add("$sNo. ✨ $aName (Add-on Service) = ₹${String.format(java.util.Locale.ENGLISH, "%.2f", aPrice)}")
                                        sNo++
                                    }
                                    val finalSummary = if (summaryList.isNotEmpty()) summaryList.joinToString("\n") else "1. Special Biryani Catering Setup = ₹0.00"

                                    val invId = "INV-2026-${(1000..9999).random()}"
                                    generatedInvoiceId = invId

                                    val finalTotal = if (finalNetBillAmount > 0) finalNetBillAmount else (totalCartAmount - calculatedDiscount).coerceAtLeast(0.0)
                                    val finalAdvance = actualAdvancePaid
                                    val finalBal = (finalTotal - finalAdvance).coerceAtLeast(0.0)

                                    val orderObj = OrderEntity(
                                        orderId = invId,
                                        customerName = customerName.trim(),
                                        customerMobile = if (customerMobile.isNotBlank()) customerMobile.trim() else "+91 98765 43210",
                                        deliveryAddress = customerAddress.trim(),
                                        catererId = "caterer_1",
                                        catererName = "A1 Huma Caterers",
                                        itemsSummary = finalSummary,
                                        totalAmount = finalTotal,
                                        advancePaidAmount = finalAdvance,
                                        balanceAmount = finalBal,
                                        paymentMethod = selectedPaymentMethod,
                                        paymentStatus = when {
                                            finalBal <= 0.0 -> PaymentStatus.FULL_PAID
                                            finalAdvance <= 0.0 -> PaymentStatus.BALANCE_PENDING
                                            else -> PaymentStatus.ADVANCE_PAID_30
                                        },
                                        orderStatus = OrderStatus.CONFIRMED,
                                        deliveryDate = eventDate,
                                        deliveryTimeSlot = eventTimeSlot,
                                        catererOrderDiscountAmount = calculatedDiscount,
                                        isOfflineBooking = true,
                                        cashCollectedByDeliveryBoy = if (selectedPaymentMethod == PaymentMethod.CASH_ON_DELIVERY) finalBal else 0.0,
                                        isCashSubmittedToKitchen = if (selectedPaymentMethod == PaymentMethod.CASH_ON_DELIVERY && finalAdvance == 0.0) false else true
                                    )

                                    viewModel.placeOrder(
                                        customerName = customerName.trim(),
                                        customerMobile = if (customerMobile.isNotBlank()) customerMobile.trim() else "+91 98765 43210",
                                        address = customerAddress.trim(),
                                        catererId = "caterer_1",
                                        catererName = "A1 Huma Caterers",
                                        itemsSummary = finalSummary,
                                        totalAmount = finalTotal,
                                        is30PercentAdvance = finalBal > 0 && finalAdvance > 0,
                                        paymentMethod = selectedPaymentMethod,
                                        deliveryDate = eventDate,
                                        deliveryTimeSlot = eventTimeSlot,
                                        catererDiscountAmount = calculatedDiscount,
                                        isOfflineBooking = true,
                                        customAdvanceAmount = finalAdvance,
                                        onSuccess = {}
                                    )

                                    generatedInvoiceOrder = orderObj
                                    currentStep = 3
                                    Toast.makeText(context, "✅ Booking Saved & Invoice #$invId Generated!", Toast.LENGTH_LONG).show()
                                },
                                enabled = isOfflineBookingEnabled,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VegGreen,
                                    disabledContainerColor = Color.LightGray
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("create_offline_order_button")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CONFIRM & GENERATE POS BILL (बिल बनाएं) 🧾",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                // =========================================================================
                // STEP 3: POS INVOICE, WHATSAPP BILL SHARE & PRINT RECEIPT
                // =========================================================================
                3 -> {
                    val invOrder = generatedInvoiceOrder
                    if (invOrder == null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No invoice generated yet.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { currentStep = 2 },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(40.dp)
                                    ) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("⬅️ Edit Details", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            generatedInvoiceOrder = null
                                            customerName = ""
                                            customerMobile = ""
                                            customerAltMobile = ""
                                            customerAddress = ""
                                            cookingNotes = ""
                                            isDiscountEnabled = false
                                            discountType = "FLAT"
                                            discountInputValue = ""
                                            discountReason = ""
                                            advancePaidInput = ""
                                            offlineCart.clear()
                                            customCartItems.clear()
                                            selectedAddOns.clear()
                                            customAddonItems.clear()
                                            currentStep = 1
                                            Toast.makeText(context, "✨ Started New Booking (नया बिल)!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(40.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("New Booking ➕", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        // Header
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("A1 HUMA CATERERS", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SaffronPrimary)
                                                Text("FSSAI Lic No: 23319008000123", fontSize = 11.sp, color = Color.Gray)
                                                Text("Okhla Phase 3, Jamia Nagar, New Delhi", fontSize = 11.sp, color = Color.Gray)
                                            }
                                            Surface(
                                                color = Color(0xFFE8F5E9),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("BOOKED ✅", color = VegGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                            }
                                        }

                                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                                        Text("INVOICE / RECEIPT: #${generatedInvoiceId}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF333333))
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Customer Name / नाम:", fontSize = 11.sp, color = Color.Gray)
                                                Text(invOrder.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("Mobile / मोबाइल:", fontSize = 11.sp, color = Color.Gray)
                                                Text(invOrder.customerMobile, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Event Date / तिथि:", fontSize = 11.sp, color = Color.Gray)
                                                Text(invOrder.deliveryDate, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("Time Slot / समय:", fontSize = 11.sp, color = Color.Gray)
                                                Text(invOrder.deliveryTimeSlot, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Venue / पता: ${invOrder.deliveryAddress}", fontSize = 12.sp, color = Color(0xFF444444))

                                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                                        Text("ORDER ITEMS SUMMARY (ऑर्डर सामान व दर):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SaffronPrimary)
                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Itemized Bill Table with Serial Numbers & Product Prices
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            shape = RoundedCornerShape(10.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                            elevation = CardDefaults.cardElevation(1.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                // Table Header
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(Color(0xFFF8FAFC))
                                                        .padding(horizontal = 10.dp, vertical = 7.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text("S.No.", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = Color(0xFF64748B), modifier = Modifier.width(36.dp))
                                                    Text("Item Description & Rate", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = Color(0xFF64748B), modifier = Modifier.weight(1f))
                                                    Text("Price / कुल", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = Color(0xFF64748B), textAlign = TextAlign.End)
                                                }
                                                Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                                                val rawSummary = invOrder.itemsSummary
                                                val rawItems = if (rawSummary.contains("\n")) {
                                                    rawSummary.split("\n").filter { it.isNotBlank() }
                                                } else {
                                                    rawSummary.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                                }

                                                rawItems.forEachIndexed { index, lineText ->
                                                    val parts = lineText.split("=")
                                                    val leftSide = parts[0].trim()
                                                    val rightSide = if (parts.size > 1) parts[1].trim() else ""

                                                    val sNoMatch = Regex("^(\\d+)\\.\\s*(.*)").find(leftSide)
                                                    val itemIndex = sNoMatch?.groupValues?.get(1) ?: "${index + 1}"
                                                    val itemDesc = sNoMatch?.groupValues?.get(2) ?: leftSide

                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Surface(
                                                            color = Color(0xFFEFF6FF),
                                                            shape = RoundedCornerShape(4.dp),
                                                            modifier = Modifier.size(24.dp)
                                                        ) {
                                                            Box(contentAlignment = Alignment.Center) {
                                                                Text(
                                                                    text = itemIndex,
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 11.sp,
                                                                    color = Color(0xFF1D4ED8)
                                                                )
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.width(8.dp))

                                                        Column(modifier = Modifier.weight(1f)) {
                                                            Text(
                                                                text = itemDesc,
                                                                fontWeight = FontWeight.SemiBold,
                                                                fontSize = 12.5.sp,
                                                                color = Color(0xFF1E293B),
                                                                lineHeight = 16.sp
                                                            )
                                                        }

                                                        if (rightSide.isNotBlank()) {
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Text(
                                                                text = rightSide,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 13.sp,
                                                                color = Color(0xFF0F172A),
                                                                textAlign = TextAlign.End
                                                            )
                                                        }
                                                    }
                                                    if (index < rawItems.size - 1) {
                                                        Divider(color = Color(0xFFF1F5F9), thickness = 0.8.dp)
                                                    }
                                                }
                                            }
                                        }

                                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                                        // Financial Summary Box
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp)) {
                                                val originalSubtotal = invOrder.totalAmount + invOrder.catererOrderDiscountAmount
                                                if (invOrder.catererOrderDiscountAmount > 0) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text("Items Subtotal (सामान कुल):", fontSize = 12.5.sp, color = Color(0xFF64748B))
                                                        Text("₹${String.format("%.2f", originalSubtotal)}", fontSize = 13.sp, color = Color(0xFF334155))
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text("Discount Given (छूट) 🏷️:", fontSize = 12.5.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                                                        Text("-₹${String.format("%.2f", invOrder.catererOrderDiscountAmount)}", fontSize = 13.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                                                    }
                                                    Divider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFFFE082))
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("Payment Terms / माध्यम:", fontSize = 12.5.sp, color = Color.Gray)
                                                    Text(
                                                        text = if (invOrder.paymentMethod == PaymentMethod.CASH_ON_DELIVERY) "Cash on Delivery (COD 🚚)" else invOrder.paymentMethod.name,
                                                        fontSize = 12.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1E293B)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("Final Net Bill (कुल शुद्ध राशि):", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                    Text("₹${String.format(java.util.Locale.ENGLISH, "%.2f", invOrder.totalAmount)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        if (invOrder.advancePaidAmount == 0.0) "Advance Received (वैकल्पिक):"
                                                        else "Advance Received (एडवांस जमा ✅):",
                                                        fontSize = 13.sp,
                                                        color = if (invOrder.advancePaidAmount == 0.0) Color.Gray else VegGreen,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        if (invOrder.advancePaidAmount == 0.0) "₹0.00 (Pay on Delivery)"
                                                        else "₹${String.format(java.util.Locale.ENGLISH, "%.2f", invOrder.advancePaidAmount)}",
                                                        fontSize = 14.sp,
                                                        color = if (invOrder.advancePaidAmount == 0.0) Color(0xFF1D4ED8) else VegGreen,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Divider(modifier = Modifier.padding(vertical = 6.dp), color = Color.LightGray)
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        if (invOrder.paymentMethod == PaymentMethod.CASH_ON_DELIVERY && invOrder.advancePaidAmount == 0.0)
                                                            "Full COD Due at Delivery (डिलीवरी पर देय 🚚):"
                                                        else
                                                            "Remaining Balance Due (बकाया राशि ⏳):",
                                                        fontSize = 13.5.sp,
                                                        color = if (invOrder.paymentMethod == PaymentMethod.CASH_ON_DELIVERY) Color(0xFF1D4ED8) else Color(0xFFD32F2F),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        "₹${String.format(java.util.Locale.ENGLISH, "%.2f", invOrder.balanceAmount)}",
                                                        fontSize = 16.sp,
                                                        color = if (invOrder.paymentMethod == PaymentMethod.CASH_ON_DELIVERY) Color(0xFF1D4ED8) else Color(0xFFD32F2F),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        val originalSubtotalVal = invOrder.totalAmount + invOrder.catererOrderDiscountAmount
                                        val discountLines = if (invOrder.catererOrderDiscountAmount > 0) {
                                            "*SUBTOTAL:* ₹${String.format("%.2f", originalSubtotalVal)}\n*SPECIAL DISCOUNT:* -₹${String.format("%.2f", invOrder.catererOrderDiscountAmount)} 🏷️\n"
                                        } else ""

                                        val invoiceText = """
                                            *A1 HUMA CATERERS - BOOKING INVOICE*
                                            ---------------------------------------
                                            *Invoice #:* ${generatedInvoiceId}
                                            *Customer:* ${invOrder.customerName} (${invOrder.customerMobile})
                                            *Event Date:* ${invOrder.deliveryDate}
                                            *Time Slot:* ${invOrder.deliveryTimeSlot}
                                            *Address:* ${invOrder.deliveryAddress}
                                            ---------------------------------------
                                            *ITEMS ORDERED (सामान, दर व कुल):*
${invOrder.itemsSummary}
                                            ---------------------------------------
                                            ${discountLines}*PAYMENT TERMS:* ${if (invOrder.paymentMethod == PaymentMethod.CASH_ON_DELIVERY) "Cash on Delivery (COD 🚚)" else invOrder.paymentMethod.name}
                                            *TOTAL BILL:* ₹${String.format(java.util.Locale.ENGLISH, "%.2f", invOrder.totalAmount)}
                                            *ADVANCE RECEIVED:* ₹${String.format(java.util.Locale.ENGLISH, "%.2f", invOrder.advancePaidAmount)} ${if (invOrder.advancePaidAmount > 0) "✅" else "(COD)"}
                                            *REMAINING BALANCE:* ₹${String.format(java.util.Locale.ENGLISH, "%.2f", invOrder.balanceAmount)} ${if (invOrder.paymentMethod == PaymentMethod.CASH_ON_DELIVERY && invOrder.balanceAmount > 0) "(Collect on Delivery 🚚)" else "⏳"}
                                            ---------------------------------------
                                            Thank you for booking with A1 Huma Caterers!
                                            FSSAI Lic: 23319008000123
                                        """.trimIndent()

                                        // Action Button: Share on WhatsApp
                                        Button(
                                            onClick = {
                                                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                                val clip = android.content.ClipData.newPlainText("Catering Invoice", invoiceText)
                                                clipboardManager?.setPrimaryClip(clip)

                                                val rawPhone = invOrder.customerMobile
                                                val cleanPhone = rawPhone.replace(" ", "").replace("-", "").replace("+", "").trim()
                                                val formattedPhone = if (cleanPhone.startsWith("91")) cleanPhone else if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone

                                                try {
                                                    val encodedText = java.net.URLEncoder.encode(invoiceText, "UTF-8")
                                                    val waUri = Uri.parse("https://api.whatsapp.com/send?phone=$formattedPhone&text=$encodedText")
                                                    val waIntent = Intent(Intent.ACTION_VIEW, waUri)
                                                    context.startActivity(waIntent)
                                                    Toast.makeText(context, "📲 Opening WhatsApp (Bill copied to clipboard)", Toast.LENGTH_SHORT).show()
                                                } catch (e: Exception) {
                                                    try {
                                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                                            type = "text/plain"
                                                            putExtra(Intent.EXTRA_TEXT, invoiceText)
                                                        }
                                                        context.startActivity(Intent.createChooser(sendIntent, "Share WhatsApp Invoice"))
                                                    } catch (e2: Exception) {
                                                        Toast.makeText(context, "📋 Bill copied to clipboard! (व्हाट्सएप पर पेस्ट करें)", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("share_whatsapp_bill")
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("📲 Share WhatsApp Bill (व्हाट्सएप बिल भेजें)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Action Buttons Row: Copy Bill, Print Slip, New Booking
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            // Copy Bill
                                            Button(
                                                onClick = {
                                                    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                                    val clip = android.content.ClipData.newPlainText("Catering Invoice", invoiceText)
                                                    clipboardManager?.setPrimaryClip(clip)
                                                    Toast.makeText(context, "📋 Bill copied to clipboard! (बिल कॉपी हो गया)", Toast.LENGTH_LONG).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f).height(48.dp).testTag("copy_bill_button")
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Copy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = {
                                                    try {
                                                        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                                                        val jobName = "A1_Huma_Invoice_${generatedInvoiceId}"

                                                        val htmlDiscountSection = if (invOrder.catererOrderDiscountAmount > 0) {
                                                            """
                                                            <div class="row"><span>Items Subtotal:</span> <span>₹${String.format(java.util.Locale.ENGLISH, "%.2f", originalSubtotalVal)}</span></div>
                                                            <div class="row green"><span>Discount Deducted:</span> <span>-₹${String.format(java.util.Locale.ENGLISH, "%.2f", invOrder.catererOrderDiscountAmount)}</span></div>
                                                            """.trimIndent()
                                                        } else ""

                                                        val rawSlipItems = if (invOrder.itemsSummary.contains("\n")) {
                                                            invOrder.itemsSummary.split("\n").filter { it.isNotBlank() }
                                                        } else {
                                                            invOrder.itemsSummary.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                                        }
                                                        val htmlItemRows = rawSlipItems.mapIndexed { index, lineText ->
                                                            val parts = lineText.split("=")
                                                            val leftSide = parts[0].trim()
                                                            val rightSide = if (parts.size > 1) parts[1].trim() else ""
                                                            val sNoMatch = Regex("^(\\d+)\\.\\s*(.*)").find(leftSide)
                                                            val sNo = sNoMatch?.groupValues?.get(1) ?: "${index + 1}"
                                                            val desc = sNoMatch?.groupValues?.get(2) ?: leftSide
                                                            """
                                                            <tr style="border-bottom: 1px solid #e2e8f0;">
                                                              <td style="padding: 6px 4px; font-weight: bold; color: #E65100; text-align: center;">$sNo</td>
                                                              <td style="padding: 6px; color: #1e293b;">$desc</td>
                                                              <td style="padding: 6px; text-align: right; font-weight: bold; color: #0f172a;">$rightSide</td>
                                                            </tr>
                                                            """.trimIndent()
                                                        }.joinToString("\n")

                                                        val htmlContent = """
                                                            <!DOCTYPE html><html><head><meta charset="utf-8"/><style>
                                                            body { font-family: sans-serif; padding: 20px; color: #222; }
                                                            .header { text-align: center; border-bottom: 2px solid #E65100; padding-bottom: 10px; margin-bottom: 15px; }
                                                            .company { font-size: 22px; font-weight: bold; color: #E65100; }
                                                            .sub { font-size: 11px; color: #555; margin-top: 3px; }
                                                            .inv-box { background: #f9f9f9; border: 1px solid #ddd; padding: 10px; border-radius: 6px; margin-bottom: 12px; font-size: 12px; }
                                                            .summary { background: #FFF8E1; border: 1px solid #FFE082; padding: 10px; border-radius: 6px; margin-top: 12px; font-size: 13px; }
                                                            .row { display: flex; justify-content: space-between; margin-bottom: 4px; }
                                                            .green { color: #2E7D32; font-weight: bold; }
                                                            .red { color: #D32F2F; font-weight: bold; }
                                                            .footer { text-align: center; margin-top: 20px; font-size: 11px; color: #777; border-top: 1px dashed #ccc; padding-top: 8px; }
                                                            </style></head><body>
                                                            <div class="header">
                                                            <div class="company">A1 HUMA CATERERS</div>
                                                            <div class="sub">Authentic Catering Services | FSSAI Lic: 23319008000123</div>
                                                            <div class="sub">Okhla Phase 3, Jamia Nagar, New Delhi | Contact: +91 98765 43210</div>
                                                            </div>
                                                            <div class="inv-box">
                                                            <div><strong>Invoice No:</strong> #${generatedInvoiceId} &nbsp;|&nbsp; <strong>Date:</strong> ${invOrder.deliveryDate}</div>
                                                            <div style="margin-top: 3px;"><strong>Customer:</strong> ${invOrder.customerName} (${invOrder.customerMobile})</div>
                                                            <div style="margin-top: 3px;"><strong>Delivery Address:</strong> ${invOrder.deliveryAddress}</div>
                                                            <div style="margin-top: 3px;"><strong>Time Slot:</strong> ${invOrder.deliveryTimeSlot}</div>
                                                            </div>
                                                            <h3 style="color: #222; font-size: 14px; margin-bottom: 6px;">Order Items & Services (सामान व दर)</h3>
                                                            <table style="width: 100%; border-collapse: collapse; background: #ffffff; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 12px; margin-bottom: 12px;">
                                                              <thead>
                                                                <tr style="background: #f1f5f9; border-bottom: 2px solid #cbd5e1; text-align: left;">
                                                                  <th style="padding: 6px 4px; width: 32px; text-align: center;">#</th>
                                                                  <th style="padding: 6px;">Item Description & Rate</th>
                                                                  <th style="padding: 6px; text-align: right;">Price (कुल)</th>
                                                                </tr>
                                                              </thead>
                                                              <tbody>
                                                                $htmlItemRows
                                                              </tbody>
                                                            </table>
                                                            <div class="summary">
                                                            $htmlDiscountSection
                                                            <div class="row"><span>Total Net Bill:</span> <strong>₹${String.format(java.util.Locale.ENGLISH, "%.2f", invOrder.totalAmount)}</strong></div>
                                                            <div class="row green"><span>Advance Received:</span> <span>₹${String.format(java.util.Locale.ENGLISH, "%.2f", invOrder.advancePaidAmount)}</span></div>
                                                            <hr style="border: 0; border-top: 1px solid #ddd; margin: 6px 0;"/>
                                                            <div class="row red"><span>Remaining Balance Due:</span> <span>₹${String.format(java.util.Locale.ENGLISH, "%.2f", invOrder.balanceAmount)}</span></div>
                                                            </div>
                                                            <div class="footer">Thank you for booking with A1 Huma Caterers!</div>
                                                            </body></html>
                                                        """.trimIndent()

                                                        val webView = WebView(context).apply {
                                                            webViewClient = object : WebViewClient() {
                                                                override fun onPageFinished(view: WebView?, url: String?) {
                                                                    val printAdapter = createPrintDocumentAdapter(jobName)
                                                                    printManager?.print(jobName, printAdapter, PrintAttributes.Builder().build())
                                                                }
                                                            }
                                                        }
                                                        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                                                        Toast.makeText(context, "🖨️ Opening Printer Dialog...", Toast.LENGTH_SHORT).show()
                                                    } catch (e: Exception) {
                                                        Toast.makeText(context, "Printing error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f).height(48.dp).testTag("print_slip_button")
                                            ) {
                                                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Print", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = {
                                                    // Reset everything for fresh booking
                                                    generatedInvoiceOrder = null
                                                    customerName = ""
                                                    customerMobile = ""
                                                    customerAltMobile = ""
                                                    customerAddress = ""
                                                    cookingNotes = ""
                                                    isDiscountEnabled = false
                                                    discountType = "FLAT"
                                                    discountInputValue = ""
                                                    discountReason = ""
                                                    advancePaidInput = ""
                                                    offlineCart.clear()
                                                    customCartItems.clear()
                                                    selectedAddOns.clear()
                                                    customAddonItems.clear()
                                                    currentStep = 1
                                                    Toast.makeText(context, "✨ Started New Booking (नया बिल)!", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f).height(48.dp).testTag("new_booking_button")
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("New ➕", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
