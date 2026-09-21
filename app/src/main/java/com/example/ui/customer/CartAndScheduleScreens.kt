package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CartItemEntity
import com.example.data.models.CateringAddOn
import com.example.data.models.UnitType
import com.example.data.repository.CaterersViewModel
import com.example.ui.common.VegNonVegBadge
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen
import com.example.util.TimeSlotUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CartScreen(
    viewModel: CaterersViewModel,
    onBack: () -> Unit,
    onProceedToSchedule: (isAdvance: Boolean, date: String, timeSlot: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItemsList.collectAsState()
    val settings by viewModel.kitchenSettings.collectAsState()
    val caterers by viewModel.caterersList.collectAsState()
    val userPoints by viewModel.userLoyaltyPoints.collectAsState()
    val appliedPromo by viewModel.appliedAdminPromoCode.collectAsState()

    val cartCatererId = cartItems.firstOrNull { !it.id.startsWith("cart_addon_") }?.catererId
    val activeCaterer = caterers.find { it.id == cartCatererId }
    val isAddonEnabled = activeCaterer?.offersAddonServices ?: settings.offersAddonServices

    var is50PercentAdvance by remember { mutableStateOf(true) }
    var promoInput by remember { mutableStateOf("") }
    var isRedeemingLoyalty by remember { mutableStateOf(false) }

    // Date and Time Slot selection inside Cart (evaluated in IST)
    val dateList = remember {
        val calendar = TimeSlotUtils.getIndianCalendar()
        val formatter = TimeSlotUtils.createDateFormat("yyyy-MM-dd")
        val displayFormatter = TimeSlotUtils.createDateFormat("EEE, dd MMM")
        val list = mutableListOf<Pair<String, String>>()
        for (i in 0..29) {
            val dateStr = formatter.format(calendar.time)
            val labelStr = if (i == 0) "${displayFormatter.format(calendar.time)} (Today)" else displayFormatter.format(calendar.time)
            list.add(Pair(dateStr, labelStr))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }
    var selectedDatePair by remember { mutableStateOf(dateList.first()) } // Default Today
    val configuredSlots = remember(settings) {
        if (settings.deliveryTimeSlots.isNotEmpty()) settings.deliveryTimeSlots else listOf(
            "11:30 AM - 02:00 PM (Lunch Dawat)",
            "02:00 PM - 05:00 PM (Afternoon Feast)",
            "07:00 PM - 10:30 PM (Dinner / Walima)"
        )
    }
    val availableSlots = remember(selectedDatePair, settings) {
        TimeSlotUtils.getAvailableTimeSlots(selectedDatePair.first, configuredSlots, settings)
    }
    var selectedTimeSlot by remember(availableSlots) {
        mutableStateOf(availableSlots.firstOrNull() ?: configuredSlots.first())
    }

    // Catering Add-on Services (Configured by Caterer Partner)
    val configuredAddOns by viewModel.customAddonServices.collectAsState()
    val availableAddOns = configuredAddOns.filter { it.isAvailable }

    // Item Level Calculation
    val rawItemsSubtotal = cartItems.sumOf { it.originalPricePerUnit * it.quantity }
    val productDiscountAmount = cartItems.sumOf { maxOf(0.0, (it.originalPricePerUnit - it.pricePerUnit) * it.quantity) }
    val itemsSubtotalAfterProductDiscount = cartItems.sumOf { it.totalPrice }

    // Caterer Per-Order Level Discount Calculation
    val catererDiscountAmount = remember(itemsSubtotalAfterProductDiscount, settings) {
        if (itemsSubtotalAfterProductDiscount >= settings.catererMinOrderForDiscount) {
            when (settings.catererOrderDiscountType) {
                com.example.data.models.DiscountType.PERCENTAGE -> {
                    itemsSubtotalAfterProductDiscount * (settings.catererOrderDiscountValue / 100.0)
                }
                com.example.data.models.DiscountType.FLAT -> {
                    minOf(itemsSubtotalAfterProductDiscount, settings.catererOrderDiscountValue)
                }
                else -> 0.0
            }
        } else 0.0
    }

    // Super Admin Per-Order Level Discount Calculation
    val adminDiscountAmount = remember(itemsSubtotalAfterProductDiscount, settings, appliedPromo) {
        val isEligible = itemsSubtotalAfterProductDiscount >= settings.adminMinOrderForDiscount
        val isPromoApplied = appliedPromo.isNotBlank() || settings.adminOrderDiscountType != com.example.data.models.DiscountType.NONE
        if (isEligible && isPromoApplied) {
            when (settings.adminOrderDiscountType) {
                com.example.data.models.DiscountType.PERCENTAGE -> {
                    itemsSubtotalAfterProductDiscount * (settings.adminOrderDiscountValue / 100.0)
                }
                com.example.data.models.DiscountType.FLAT -> {
                    minOf(itemsSubtotalAfterProductDiscount, settings.adminOrderDiscountValue)
                }
                else -> 0.0
            }
        } else 0.0
    }

    val subtotalAfterOrderDiscounts = maxOf(0.0, itemsSubtotalAfterProductDiscount - catererDiscountAmount - adminDiscountAmount)

    // Loyalty Points Discount Calculation
    val maxRedeemableRupees = subtotalAfterOrderDiscounts * (settings.maxLoyaltyRedeemPercent / 100.0)
    val availablePointsRupees = userPoints * settings.loyaltyPointRupeeValue
    val loyaltyDiscountAmount = if (isRedeemingLoyalty && settings.isLoyaltyEnabled) {
        minOf(availablePointsRupees, maxRedeemableRupees)
    } else 0.0

    val redeemedPointsCount = if (loyaltyDiscountAmount > 0 && settings.loyaltyPointRupeeValue > 0) {
        (loyaltyDiscountAmount / settings.loyaltyPointRupeeValue).toInt()
    } else 0

    val finalTotalPayable = maxOf(0.0, subtotalAfterOrderDiscounts - loyaltyDiscountAmount)
    val earnedPoints = if (settings.isLoyaltyEnabled) {
        ((finalTotalPayable / 100.0) * settings.loyaltyEarnPointsPer100Rs).toInt()
    } else 0

    // 50% Advance Token & 50% Balance on Delivery
    val advancePay = finalTotalPayable * 0.50
    val balancePay = finalTotalPayable - advancePay

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9F6F0))
        ) {
            // Header
            Surface(
                color = SaffronPrimary,
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("cart_back")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "My Catering Order Cart",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Your catering cart is empty!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            modifier = Modifier.testTag("empty_cart_browse")
                        ) {
                            Text("Browse Royal Caterers")
                        }
                    }
                }
            } else {
                val foodCartItems = remember(cartItems) { cartItems.filter { !it.id.startsWith("cart_addon_") } }
                val addonCartItems = remember(cartItems) { cartItems.filter { it.id.startsWith("cart_addon_") } }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    // Birthday & Function Fast Delivery Priority Assurance Banner
                    item {
                        Surface(
                            color = Color(0xFFFFF7ED),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⚡", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Fast Event Delivery (10 - 200 Guests)",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF9A3412)
                                    )
                                    Text(
                                        text = "Freshly prepared in sealed hot degh and delivered on dot for your function.",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFFC2410C)
                                    )
                                }
                            }
                        }
                    }

                    if (foodCartItems.isNotEmpty()) {
                        item {
                            Text(
                                text = "Selected Catering Dishes",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        items(foodCartItems, key = { it.id }) { item ->
                            CartItemRow(
                                item = item,
                                onRemove = { viewModel.removeCartItem(item.id) }
                            )
                        }
                    }

                    // --- ADD-ON SERVICES SECTION (Slider in horizontal scroll - Compact: 2-3 visible at once) ---
                    if (isAddonEnabled && availableAddOns.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AmberSecondary.copy(alpha = 0.6f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("✨", fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    "Catering Add-on Services (दावत सुविधाएं)",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.5.sp,
                                                    color = Color(0xFF78350F)
                                                )
                                                Text(
                                                    "Staff, serving utensils & plate packages",
                                                    fontSize = 10.5.sp,
                                                    color = Color(0xFF92400E)
                                                )
                                            }
                                        }
                                        Surface(
                                            color = AmberSecondary.copy(alpha = 0.25f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                "Swipe ➡️",
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF78350F),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // HORIZONTAL SLIDER: Compact width (116.dp) so 2 to 3 services fit side-by-side on screen
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(horizontal = 2.dp)
                                    ) {
                                        items(availableAddOns, key = { it.id }) { addOn ->
                                            val inCartItem = cartItems.find { it.menuItemId == addOn.id || it.id == "cart_addon_${addOn.id}" }
                                            val isAdded = inCartItem != null
                                            val currentQty = inCartItem?.quantity ?: 0.0

                                            Surface(
                                                color = if (isAdded) Color(0xFFFFFBEB) else Color.White,
                                                shape = RoundedCornerShape(12.dp),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    if (isAdded) 1.5.dp else 1.dp,
                                                    if (isAdded) SaffronPrimary else Color(0xFFFED7AA)
                                                ),
                                                shadowElevation = if (isAdded) 2.dp else 1.dp,
                                                modifier = Modifier
                                                    .width(116.dp)
                                                    .testTag("addon_slider_card_${addOn.id}")
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(8.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    // Top Icon & Price tag
                                                    Box(
                                                        modifier = Modifier
                                                            .size(34.dp)
                                                            .background(
                                                                if (isAdded) AmberSecondary.copy(alpha = 0.35f) else Color(0xFFFFF7ED),
                                                                CircleShape
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(addOn.icon, fontSize = 18.sp)
                                                    }

                                                    Spacer(modifier = Modifier.height(4.dp))

                                                    Text(
                                                        text = "₹${addOn.price.toInt()}",
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 12.5.sp,
                                                        color = Color(0xFF92400E)
                                                    )

                                                    Spacer(modifier = Modifier.height(2.dp))

                                                    // Service Name & Hindi subtitle (Compact & truncated cleanly)
                                                    Text(
                                                        text = addOn.name,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.5.sp,
                                                        color = Color(0xFF1E293B),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        textAlign = TextAlign.Center
                                                    )
                                                    Text(
                                                        text = addOn.hindiName,
                                                        fontSize = 9.sp,
                                                        color = Color(0xFFB45309),
                                                        fontWeight = FontWeight.Medium,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        textAlign = TextAlign.Center
                                                    )

                                                    Spacer(modifier = Modifier.height(4.dp))

                                                    // Plate / Package Quantity Badge
                                                    Surface(
                                                        color = Color(0xFFF1F5F9),
                                                        shape = RoundedCornerShape(4.dp),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            text = addOn.servesText,
                                                            fontSize = 8.5.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = Color(0xFF475569),
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.height(8.dp))

                                                    // Bottom Action: Remains sticky with instant Stepper [–] Qty [+]
                                                    if (!isAdded) {
                                                        Button(
                                                            onClick = { viewModel.addAddOnService(addOn, 1.0) },
                                                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                                            shape = RoundedCornerShape(6.dp),
                                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(28.dp)
                                                                .testTag("add_addon_${addOn.id}")
                                                        ) {
                                                            Text("+ Add", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    } else {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(28.dp)
                                                                .background(Color.White, RoundedCornerShape(6.dp))
                                                                .border(1.dp, SaffronPrimary, RoundedCornerShape(6.dp))
                                                                .padding(horizontal = 4.dp)
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(24.dp)
                                                                    .clickable {
                                                                        if (currentQty > 1.0) {
                                                                            viewModel.addAddOnService(addOn, currentQty - 1.0)
                                                                        } else {
                                                                            viewModel.removeAddOnService(addOn.id)
                                                                        }
                                                                    },
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text(
                                                                    "–",
                                                                    fontWeight = FontWeight.ExtraBold,
                                                                    fontSize = 14.sp,
                                                                    color = SaffronPrimary
                                                                )
                                                            }

                                                            Text(
                                                                text = "${currentQty.toInt()}",
                                                                fontWeight = FontWeight.ExtraBold,
                                                                fontSize = 11.5.sp,
                                                                color = Color.Black
                                                            )

                                                            Box(
                                                                modifier = Modifier
                                                                    .size(24.dp)
                                                                    .clickable {
                                                                        viewModel.addAddOnService(addOn, currentQty + 1.0)
                                                                    },
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text(
                                                                    "+",
                                                                    fontWeight = FontWeight.ExtraBold,
                                                                    fontSize = 14.sp,
                                                                    color = SaffronPrimary
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Selected Add-ons summary list inside card
                                    if (addonCartItems.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Surface(
                                            color = Color(0xFFFFF7ED),
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberSecondary.copy(alpha = 0.4f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                                Text(
                                                    text = "✓ Selected Add-on Services (${addonCartItems.sumOf { it.quantity }.toInt()} units added):",
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF9A3412)
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                addonCartItems.forEach { addOnItem ->
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 1.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = "• ${addOnItem.name} (${addOnItem.quantity.toInt()}x)",
                                                            fontSize = 10.5.sp,
                                                            color = Color(0xFF334155),
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                        Text(
                                                            text = "₹${addOnItem.totalPrice.toInt()}",
                                                            fontSize = 10.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF78350F)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Caterer partner only delivers pure food (सिर्फ खाना ही बेचेगा)
                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = Color(0xFFDCFCE7),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("🍲", fontSize = 18.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "${activeCaterer?.name ?: "Kitchen Partner"} • केवल शुद्ध खाना (Pure Food)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = Color(0xFF166534)
                                        )
                                        Text(
                                            text = "इस पार्टनर के पास केवल खाना बनाने व डिलीवरी की सुविधा है, कोई अतिरिक्त ऐड-ऑन सेवा लागू नहीं है।",
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF15803D)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // --- DATE & TIME SLOT SELECTION IN CART ---
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Event Date & Delivery Slot", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("तारीख व समय स्लॉट चुनें (Sealed & Hot Degh delivery)", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Select Catering Event Date:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                                Spacer(modifier = Modifier.height(6.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(dateList) { pair ->
                                        val isSelected = pair.first == selectedDatePair.first
                                        Surface(
                                            color = if (isSelected) SaffronPrimary else Color(0xFFF1F5F9),
                                            contentColor = if (isSelected) Color.White else Color(0xFF334155),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .clickable { selectedDatePair = pair }
                                                .testTag("date_chip_${pair.first}")
                                        ) {
                                            Text(
                                                text = pair.second,
                                                fontSize = 11.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Select Delivery Time Slot:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                                Spacer(modifier = Modifier.height(6.dp))

                                if (availableSlots.isEmpty()) {
                                    Surface(
                                        color = Color(0xFFFFF1F2),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECDD3)),
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                "⚠️ All slots closed for today",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp,
                                                color = Color(0xFFBE123C)
                                            )
                                            Text(
                                                "Please select tomorrow or a future date for catering booking.",
                                                fontSize = 11.sp,
                                                color = Color(0xFF475569)
                                            )
                                        }
                                    }
                                } else {
                                    availableSlots.forEach { slot ->
                                        val isSlotSelected = slot == selectedTimeSlot
                                        Surface(
                                            color = if (isSlotSelected) Color(0xFFFFF7ED) else Color(0xFFF8FAFC),
                                            shape = RoundedCornerShape(10.dp),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.2.dp,
                                                if (isSlotSelected) SaffronPrimary else Color(0xFFE2E8F0)
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp)
                                                .clickable { selectedTimeSlot = slot }
                                                .testTag("slot_chip_$slot")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = isSlotSelected,
                                                    onClick = { selectedTimeSlot = slot },
                                                    colors = RadioButtonDefaults.colors(selectedColor = SaffronPrimary),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = slot,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSlotSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSlotSelected) Color(0xFF9A3412) else Color(0xFF1E293B)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Admin Promo Code Section
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalOffer, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Super Admin Promo Code", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                if (appliedPromo.isNotBlank()) {
                                    Surface(
                                        color = Color(0xFFE8F5E9),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("Code '$appliedPromo' Applied! 🎉", fontWeight = FontWeight.Bold, color = VegGreen, fontSize = 13.sp)
                                                Text("Saved ₹${adminDiscountAmount.toInt()} on this order", fontSize = 11.sp, color = VegGreen)
                                            }
                                            TextButton(onClick = { viewModel.removeAdminPromoCode() }) {
                                                Text("Remove", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = promoInput,
                                            onValueChange = { promoInput = it },
                                            placeholder = { Text("Enter Promo Code (e.g. ${settings.adminPromoCode})", fontSize = 12.sp) },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )
                                        Button(
                                            onClick = {
                                                if (promoInput.isNotBlank()) {
                                                    viewModel.applyAdminPromoCode(promoInput)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Apply", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Admin Loyalty Points Section
                    if (settings.isLoyaltyEnabled) {
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AmberSecondary)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("🎁", fontSize = 20.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text("Admin Loyalty Points", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF5D4037))
                                                Text("Balance: $userPoints Points (₹${(userPoints * settings.loyaltyPointRupeeValue).toInt()} Value)", fontSize = 11.sp, color = Color(0xFF8D6E63))
                                            }
                                        }
                                        Switch(
                                            checked = isRedeemingLoyalty,
                                            onCheckedChange = { isRedeemingLoyalty = it },
                                            colors = SwitchDefaults.colors(checkedThumbColor = AmberSecondary, checkedTrackColor = Color(0xFFFFECB3)),
                                            enabled = userPoints > 0
                                        )
                                    }

                                    if (isRedeemingLoyalty) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Divider(color = Color(0xFFFFE082))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            "Redeeming $redeemedPointsCount Points for ₹${loyaltyDiscountAmount.toInt()} Instant Discount! (Max ${settings.maxLoyaltyRedeemPercent}% of order value)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = VegGreen
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // --- 50% ADVANCE PAYMENT SELECTION ---
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Catering Booking Payment Terms",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, if (is50PercentAdvance) SaffronPrimary else Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { is50PercentAdvance = true }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = is50PercentAdvance,
                                        onClick = { is50PercentAdvance = true },
                                        colors = RadioButtonDefaults.colors(selectedColor = SaffronPrimary)
                                    )
                                    Column(modifier = Modifier.padding(start = 8.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Pay 50% Advance Token Now", fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                                                Text(
                                                    "Recommended",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF166534),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            "Pay ₹${advancePay.toInt()} today to lock kitchen date & raw material preparation. Remaining balance ₹${balancePay.toInt()} payable on delivery.",
                                            fontSize = 12.sp,
                                            color = VegGreen,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(color = Color(0xFFF1F5F9))
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { is50PercentAdvance = false }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = !is50PercentAdvance,
                                        onClick = { is50PercentAdvance = false },
                                        colors = RadioButtonDefaults.colors(selectedColor = SaffronPrimary)
                                    )
                                    Column(modifier = Modifier.padding(start = 8.dp)) {
                                        Text("Pay 100% Full Amount Now", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Pay total ₹${finalTotalPayable.toInt()} online now without any remaining balance.", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    // --- BILL SUMMARY ---
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Bill Summary & Breakdown", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Dishes & Add-ons Base Total", fontSize = 13.sp, color = Color.Gray)
                                    Text("₹${rawItemsSubtotal.toInt()}", fontSize = 13.sp, color = Color.Gray)
                                }

                                if (productDiscountAmount > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Per-Product Items Discount", fontSize = 13.sp, color = VegGreen)
                                        Text("-₹${productDiscountAmount.toInt()}", fontSize = 13.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (catererDiscountAmount > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Caterer Order Discount (${if (settings.catererOrderDiscountType == com.example.data.models.DiscountType.PERCENTAGE) "${settings.catererOrderDiscountValue.toInt()}%" else "₹${settings.catererOrderDiscountValue.toInt()}"})", fontSize = 13.sp, color = VegGreen)
                                        Text("-₹${catererDiscountAmount.toInt()}", fontSize = 13.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (adminDiscountAmount > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Super Admin Promo Discount", fontSize = 13.sp, color = VegGreen)
                                        Text("-₹${adminDiscountAmount.toInt()}", fontSize = 13.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (loyaltyDiscountAmount > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Admin Loyalty Points Discount ($redeemedPointsCount Pts)", fontSize = 13.sp, color = Color(0xFFD84315))
                                        Text("-₹${loyaltyDiscountAmount.toInt()}", fontSize = 13.sp, color = Color(0xFFD84315), fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Catering Delivery & Van Transit", fontSize = 13.sp)
                                    Text("FREE / Included", fontSize = 13.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Divider(color = Color(0xFFE2E8F0))
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total Order Value", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("₹${finalTotalPayable.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                                }

                                if (is50PercentAdvance) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("50% Advance Payable Today", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SaffronPrimary)
                                        Text("₹${advancePay.toInt()}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SaffronPrimary)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Remaining 50% on Delivery", fontSize = 12.sp, color = Color.Gray)
                                        Text("₹${balancePay.toInt()}", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }

                                if (settings.isLoyaltyEnabled && earnedPoints > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        color = Color(0xFFFFF3E0),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            "✨ Earn +$earnedPoints Admin Loyalty Points on completing this booking!",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE65100),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                if (is50PercentAdvance) "50% Advance Today" else "Full Payable",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                "₹${if (is50PercentAdvance) advancePay.toInt() else finalTotalPayable.toInt()}",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary
                            )
                            if (is50PercentAdvance) {
                                Text("Bal: ₹${balancePay.toInt()}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        Button(
                            enabled = availableSlots.isNotEmpty() && selectedTimeSlot.isNotBlank(),
                            onClick = { onProceedToSchedule(is50PercentAdvance, selectedDatePair.first, selectedTimeSlot) },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("continue_to_schedule")
                        ) {
                            Text("Delivery Address & Phone 👉", fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItemEntity,
    onRemove: () -> Unit
) {
    val hasProductDiscount = item.originalPricePerUnit > item.pricePerUnit

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                VegNonVegBadge(foodType = item.foodType)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${item.quantity} ${item.unitType.name} × ₹${item.pricePerUnit.toInt()}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        if (hasProductDiscount) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "₹${item.originalPricePerUnit.toInt()}",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                style = TextStyle(textDecoration = TextDecoration.LineThrough)
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("₹${item.totalPrice.toInt()}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SaffronPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp).testTag("remove_cart_${item.id}")) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun DeliveryScheduleScreen(
    viewModel: CaterersViewModel,
    onBack: () -> Unit,
    onProceedToAddress: (selectedDate: String, selectedTimeSlot: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.kitchenSettings.collectAsState()

    val dateList = remember {
        val calendar = TimeSlotUtils.getIndianCalendar()
        val formatter = TimeSlotUtils.createDateFormat("yyyy-MM-dd")
        val displayFormatter = TimeSlotUtils.createDateFormat("EEE, dd MMM")
        val list = mutableListOf<Pair<String, String>>()
        for (i in 0..29) {
            val dateStr = formatter.format(calendar.time)
            val labelStr = if (i == 0) "${displayFormatter.format(calendar.time)} (Today)" else displayFormatter.format(calendar.time)
            list.add(Pair(dateStr, labelStr))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    var selectedDatePair by remember { mutableStateOf(dateList.first()) }

    // Dynamic filtering based on Same Day settings
    val allConfiguredSlots = remember(settings) {
        if (settings.deliveryTimeSlots.isNotEmpty()) settings.deliveryTimeSlots else listOf(
            "11:00 AM - 02:00 PM (Lunch)",
            "02:00 PM - 05:00 PM (Snacks/High Tea)",
            "07:00 PM - 10:30 PM (Dinner)"
        )
    }

    val availableSlots = remember(selectedDatePair, settings) {
        TimeSlotUtils.getAvailableTimeSlots(selectedDatePair.first, allConfiguredSlots, settings)
    }

    var selectedTimeSlot by remember(availableSlots) {
        mutableStateOf(availableSlots.firstOrNull() ?: "")
    }

    val isToday = remember(selectedDatePair) { TimeSlotUtils.isToday(selectedDatePair.first) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6F0))
    ) {
        // Top Bar
        Surface(
            color = SaffronPrimary,
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("schedule_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "Delivery Schedule (30 Days)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            // Same Day / Future Date Status Notice
            item {
                if (isToday) {
                    if (!settings.allowSameDayBooking) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color.Red, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "⚠️ Same Day Bookings Closed Today",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFC62828)
                                    )
                                    Text(
                                        text = "Kitchen is only accepting advance bookings for tomorrow or future dates.",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    } else if (availableSlots.isEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "⏰ Same Day Cutoff Reached",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100)
                                    )
                                    Text(
                                        text = "Preparation requires ${settings.sameDayPrepLeadTimeHours} hrs. All remaining slots for today have passed cutoff. Please select tomorrow.",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    } else {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "⚡ Same Day Delivery Available Today!",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B5E20)
                                    )
                                    Text(
                                        text = "Showing available time slots (Filtered: Current time + ${settings.sameDayPrepLeadTimeHours} hrs prep lead time).",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Advance Booking: All delivery time slots are open for ${selectedDatePair.second}.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Select Date Section
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Select Catering Date", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dateList) { datePair ->
                        val isSelected = datePair.first == selectedDatePair.first
                        Surface(
                            onClick = { selectedDatePair = datePair },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) SaffronPrimary else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) SaffronPrimary else Color.LightGray),
                            modifier = Modifier.testTag("date_chip_${datePair.first}")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = datePair.second,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Select Time Slot
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Select Delivery Time Slot", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))

                if (availableSlots.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No time slots available for this date. Please select tomorrow or another date above.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Column {
                        availableSlots.chunked(2).forEach { rowSlots ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowSlots.forEach { slot ->
                                    val isSelected = slot == selectedTimeSlot
                                    Surface(
                                        onClick = { selectedTimeSlot = slot },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) SaffronPrimary.copy(alpha = 0.12f) else Color.White,
                                        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) SaffronPrimary else Color.LightGray),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("time_slot_$slot")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = slot,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) SaffronPrimary else Color.Black
                                            )
                                            if (isSelected) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(16.dp))
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

        // Bottom Continue Button
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                enabled = availableSlots.isNotEmpty() && selectedTimeSlot.isNotBlank(),
                onClick = { onProceedToAddress(selectedDatePair.first, selectedTimeSlot) },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("proceed_to_address_button")
            ) {
                Text("Proceed to Delivery Address", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
