package com.example.ui.delivery

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.models.OrderEntity
import com.example.data.models.OrderStatus
import com.example.data.repository.CaterersViewModel
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen

@Composable
fun DeliveryMainContainer(
    viewModel: CaterersViewModel,
    modifier: Modifier = Modifier
) {
    var isOnlineDuty by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Deliveries", "50% Cash Collection", "Container Return", "Earnings")

    val orders by viewModel.ordersList.collectAsState()
    val assignedOrders = orders.filter { it.deliveryBoyId == "db_1" || it.orderStatus == OrderStatus.ASSIGNED_DELIVERY || it.orderStatus == OrderStatus.OUT_FOR_DELIVERY }

    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Surface(color = Color(0xFF1E293B), contentColor = Color.White) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(SaffronPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Ramesh Sharma (Delivery Partner)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Aadhaar Verified ✅ | A1 Huma Kitchen", fontSize = 11.sp, color = AmberSecondary)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isOnlineDuty) "ONLINE" else "OFFLINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isOnlineDuty) VegGreen else Color.Red)
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = isOnlineDuty,
                            onCheckedChange = { isOnlineDuty = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = VegGreen),
                            modifier = Modifier.testTag("delivery_online_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, style = MaterialTheme.typography.labelLarge, fontSize = 12.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium) },
                            modifier = Modifier.testTag("delivery_tab_$index")
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFF9F6F0))
        ) {
            when (selectedTab) {
                0 -> DeliveryOrdersScreen(assignedOrders, viewModel)
                1 -> DeliveryCashCollectionScreen(viewModel)
                2 -> DeliveryBartanReturnScreen(viewModel)
                3 -> DeliveryEarningsScreen(viewModel)
            }
        }
    }
}

@Composable
fun DeliveryOrdersScreen(
    orders: List<OrderEntity>,
    viewModel: CaterersViewModel
) {
    var otpDialogOrder by remember { mutableStateOf<OrderEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Multi-Order Smart Route Active", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${orders.size} Delivery stops assigned for current time slot", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(orders.size) { index ->
            val order = orders[index]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("delivery_order_card_${order.orderId}"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(color = SaffronPrimary, shape = RoundedCornerShape(4.dp)) {
                            Text("STOP #${index + 1}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Text("#${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SaffronPrimary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("📍 Address: ${order.deliveryAddress}", fontSize = 12.sp, color = Color.Gray)
                    Text("📦 Items: ${order.itemsSummary}", fontSize = 12.sp, color = Color(0xFF212121), fontWeight = FontWeight.Medium)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF3E0), RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Balance Cash/UPI to Collect:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("₹${order.balanceAmount.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { /* Navigation map simulation */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Navigate", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { otpDialogOrder = order },
                            colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("enter_delivery_otp_${order.orderId}")
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Verify OTP", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    if (otpDialogOrder != null) {
        val order = otpDialogOrder!!
        var enteredOtp by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { otpDialogOrder = null },
            title = { Text("Enter 4-Digit Delivery OTP for #${order.orderId}") },
            text = {
                Column {
                    Text("Ask customer ${order.customerName} for the 4-digit OTP shown in their app.", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = { enteredOtp = it },
                        label = { Text("4-Digit OTP") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("delivery_otp_input")
                    )
                    if (errorMessage != null) {
                        Text(errorMessage!!, color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.verifyDeliveryOtp(order.orderId, enteredOtp) { success ->
                            if (success) {
                                otpDialogOrder = null
                            } else {
                                errorMessage = "Invalid 4-Digit OTP! Please re-ask customer."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                    modifier = Modifier.testTag("confirm_delivery_otp_button")
                ) {
                    Text("Verify & Complete Delivery")
                }
            },
            dismissButton = {
                TextButton(onClick = { otpDialogOrder = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun DeliveryEarningsScreen(viewModel: CaterersViewModel) {
    val orders by viewModel.ordersList.collectAsState()
    val deliveredOrders = orders.filter { it.orderStatus == OrderStatus.DELIVERED }
    val totalCashCollected = deliveredOrders.sumOf { it.balanceAmount }
    val totalDeliveries = deliveredOrders.size
    val deliveryFeeEarned = totalDeliveries * 150.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Today's Payout & Delivery Earnings", color = Color.LightGray, fontSize = 12.sp)
                    Text("₹${deliveryFeeEarned.toInt()}", color = AmberSecondary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Completed Deliveries: $totalDeliveries | Per Drop Fee: ₹150", color = Color.White, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cash on Delivery Handover Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Money, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("70% COD Cash Collected", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                            Text("Auto-Settled", color = VegGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total Customer Cash Collected: ₹${totalCashCollected.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                    Text("Status: Handed over directly to A1 Huma Kitchen Cashier ✅", fontSize = 11.sp, color = VegGreen, fontWeight = FontWeight.Medium)
                    Text("This cash amount is automatically credited to the kitchen and deducted from weekly platform transfer.", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Delivered Orders Breakdown", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(deliveredOrders.size) { index ->
            val order = deliveredOrders[index]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Order #${order.orderId} - ${order.customerName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Balance Collected: ₹${order.balanceAmount.toInt()} -> Kitchen Cashier", fontSize = 11.sp, color = VegGreen)
                    }
                    Text("Fee: ₹150", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SaffronPrimary)
                }
            }
        }
    }
}

@Composable
fun DeliveryBartanReturnScreen(viewModel: CaterersViewModel) {
    val bartanList by viewModel.bartanRecordsList.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    var filterSelection by remember { mutableStateOf("ALL") } // ALL, PENDING, RETURNED

    val pendingList = bartanList.filter { !it.isCollected }
    val returnedList = bartanList.filter { it.isCollected }

    val displayedList = when (filterSelection) {
        "PENDING" -> pendingList
        "RETURNED" -> returnedList
        else -> bartanList
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Header card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⏰", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "9:30 AM Morning Container Return Tasks",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF1E3A8A)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (pendingList.isNotEmpty()) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                        ) {
                            Text(
                                "${pendingList.size} Pickups Due",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (pendingList.isNotEmpty()) Color(0xFFB91C1C) else Color(0xFF15803D),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Food Delivery Containers (Degs/Handis) are Kitchen Property (Bhade par nahi hain). Jis order ko aap deliver karke aaye the, uske bartan customer se collect karke kitchen ko lautana hai.",
                        fontSize = 11.sp,
                        color = Color(0xFF1E40AF),
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.trigger930AmMorningAlert(showToast = true) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🔔 Check / Refresh 9:30 AM Pickup Reminders", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Chips: Kitna Baki Hai vs Kitna Return Aa Gaya
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterSelection == "ALL",
                    onClick = { filterSelection = "ALL" },
                    label = { Text("Sabhi Containers (${bartanList.size})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = filterSelection == "PENDING",
                    onClick = { filterSelection = "PENDING" },
                    label = { Text("⏳ Kitna Baki Hai (${pendingList.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFEF3C7))
                )
                FilterChip(
                    selected = filterSelection == "RETURNED",
                    onClick = { filterSelection = "RETURNED" },
                    label = { Text("✅ Return Aa Gaya (${returnedList.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFDCFCE7))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("Orders for Container Pickup & Return", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (displayedList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Koi container pickup nahi mila is filter me! 🎉", fontSize = 12.5.sp, color = Color.Gray)
                    }
                }
            }
        }

        items(displayedList) { record ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Order #${record.orderId}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (record.isCollected) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                                ) {
                                    Text(
                                        if (record.isCollected) "Returned to Kitchen ✅" else "Pickup Pending ⏳ (ग्राहक के पास बाकी)",
                                        color = if (record.isCollected) VegGreen else Color(0xFFB45309),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("👤 Customer: ${record.customerName}", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                            Text("📍 Address: ${record.customerAddress.ifBlank { "Delivery Address" }}", fontSize = 11.sp, color = Color.Gray)
                            Text("🍲 Utensils: ${record.itemsDescription}", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = SaffronPrimary)
                            Text("🏢 Return to Kitchen: ${record.catererName.ifBlank { "A1 Huma Caterers" }}", fontSize = 11.sp, color = Color(0xFF475569))
                            Text("🛵 Delivery Boy: Ramesh Sharma (You)", fontSize = 10.5.sp, color = Color.Gray)
                        }

                        Row {
                            if (record.customerMobile.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${record.customerMobile}"))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = "Call Customer", tint = VegGreen, modifier = Modifier.size(18.dp))
                                }
                            }
                            IconButton(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(record.customerAddress.ifBlank { "New Delhi" })}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    context.startActivity(mapIntent)
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = "Navigate", tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!record.isCollected) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🔒", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Kitchen Verification Pending (किचन में जमा होना बाकी)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF92400E)
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "Sirf Kitchen Manager/Cashier hi confirm karega ki Handi/Bartan mil gaya hai. Delivery boy ise mark nahi kar sakta. Bartan kitchen counter par laakar jama karein.",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF78350F),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    } else {
                        Surface(
                            color = Color(0xFFF0FDF4),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("✅ Handi/Bartan verified & received by Kitchen", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryCashCollectionScreen(viewModel: CaterersViewModel) {
    val orders by viewModel.ordersList.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    var cashFilter by remember { mutableStateOf("PENDING") } // PENDING, SUBMITTED, ALL

    // Cash orders handled by delivery boy
    val cashOrders = orders.filter { it.totalAmount > 0 }
    val pendingCashOrders = cashOrders.filter { !it.isCashSubmittedToKitchen }
    val submittedCashOrders = cashOrders.filter { it.isCashSubmittedToKitchen }

    val displayedCashOrders = when (cashFilter) {
        "PENDING" -> pendingCashOrders
        "SUBMITTED" -> submittedCashOrders
        else -> cashOrders
    }

    val totalPendingCash = pendingCashOrders.sumOf { (if (it.cashCollectedByDeliveryBoy > 0) it.cashCollectedByDeliveryBoy else it.balanceAmount).toDouble() }
    val totalSubmittedCash = submittedCashOrders.sumOf { (if (it.cashCollectedByDeliveryBoy > 0) it.cashCollectedByDeliveryBoy else it.balanceAmount).toDouble() }
    val totalHandledCash = totalPendingCash + totalSubmittedCash

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Strict Policy Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFDE68A))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💵", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Delivery Boy 50% Cash Collection Policy",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Delivery hone ke baad customer ka 50% balance cash delivery boy ke paas rahega. Delivery boy yeh cash kitchen ko laakar dega. Jab tak kitchen mark nahi karega, tab tak status change nahi hoga!",
                        fontSize = 11.5.sp,
                        color = Color(0xFF78350F),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metrics: Kitna baki hai vs Kitna diya hai
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("⚠️ Kitna Baki Hai", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                        Text("₹${totalPendingCash.toInt()}", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFDC2626))
                        Text("Cash in Hand (किचन को देना बाकी)", fontSize = 9.sp, color = Color(0xFFB91C1C))
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("✅ Kitna Diya Hai", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                        Text("₹${totalSubmittedCash.toInt()}", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = VegGreen)
                        Text("Kitchen me Jama Hua", fontSize = 9.sp, color = Color(0xFF15803D))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = cashFilter == "PENDING",
                    onClick = { cashFilter = "PENDING" },
                    label = { Text("⚠️ Baki Cash (${pendingCashOrders.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFEE2E2))
                )
                FilterChip(
                    selected = cashFilter == "SUBMITTED",
                    onClick = { cashFilter = "SUBMITTED" },
                    label = { Text("✅ Jama Cash (${submittedCashOrders.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFDCFCE7))
                )
                FilterChip(
                    selected = cashFilter == "ALL",
                    onClick = { cashFilter = "ALL" },
                    label = { Text("Sabhi Orders (${cashOrders.size})", fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Cash Collection Records", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (displayedCashOrders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Koi cash record nahi mila is filter me! 💵", fontSize = 12.5.sp, color = Color.Gray)
                    }
                }
            }
        }

        items(displayedCashOrders) { order ->
            val cashToCollect = if (order.cashCollectedByDeliveryBoy > 0) order.cashCollectedByDeliveryBoy else order.balanceAmount
            val isSubmitted = order.isCashSubmittedToKitchen

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Order header & status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Order #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isSubmitted) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    if (isSubmitted) "Kitchen Handover Done ✅" else "Cash with Delivery Boy ⚠️",
                                    color = if (isSubmitted) VegGreen else Color(0xFFB45309),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text("₹${cashToCollect.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = if (isSubmitted) VegGreen else Color(0xFFDC2626))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Customer Details
                    Text("👤 Customer Details:", fontSize = 10.5.sp, color = Color.Gray)
                    Text(order.customerName, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("📞 ${order.customerMobile}", fontSize = 11.sp, color = Color(0xFF2563EB))
                            Text("📍 ${order.deliveryAddress}", fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                        }
                        Row {
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.customerMobile}"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Call Customer", tint = VegGreen, modifier = Modifier.size(17.dp))
                            }
                            IconButton(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(order.deliveryAddress)}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    context.startActivity(mapIntent)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = "Navigate", tint = Color(0xFF2563EB), modifier = Modifier.size(17.dp))
                            }
                        }
                    }

                    androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))

                    // Bill Summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total: ₹${order.totalAmount.toInt()}", fontSize = 11.sp, color = Color.Gray)
                        Text("50% Adv: ₹${order.advancePaidAmount.toInt()}", fontSize = 11.sp, color = VegGreen)
                        Text("50% Balance: ₹${cashToCollect.toInt()}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Delivery Boy Responsibility Note
                    Surface(
                        color = if (isSubmitted) Color(0xFFF8FAFC) else Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("🛵 Delivery Boy: Ramesh Sharma (You)", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Text(
                                if (isSubmitted) "Status: ₹${cashToCollect.toInt()} kitchen cashier ko jama ho gaya hai aur verify ho gaya."
                                else "Status: ₹${cashToCollect.toInt()} aapke paas hai. Kitchen ko laakar dega. Jab tak kitchen cashier mark nahi karega tab tak status change nahi hoga.",
                                fontSize = 10.sp,
                                color = if (isSubmitted) VegGreen else Color(0xFF991B1B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action
                    if (!isSubmitted) {
                        Button(
                            onClick = {
                                viewModel.deliveryBoyNotifyCashHandover(order.orderId)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🏢 Handover Cash at Kitchen Counter (किचन को कैश सौंपा)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Surface(
                            color = Color(0xFFF0FDF4),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("✅ Cash Verified & Fully Settled with Kitchen", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                            }
                        }
                    }
                }
            }
        }
    }
}
