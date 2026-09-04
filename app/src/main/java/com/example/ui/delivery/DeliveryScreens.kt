package com.example.ui.delivery

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    val tabs = listOf("My Deliveries", "Earnings", "Bartan Return")

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
                            text = { Text(title, style = MaterialTheme.typography.labelLarge, fontSize = 13.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium) },
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
                1 -> DeliveryEarningsScreen(viewModel)
                2 -> DeliveryBartanReturnScreen(viewModel)
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Bartan Return Collection", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Collect heavy degs & serving utensils from completed orders", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(bartanList) { record ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(record.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Items: ${record.itemsDescription}", fontSize = 11.sp, color = SaffronPrimary)
                    }

                    if (!record.isCollected) {
                        Button(
                            onClick = { viewModel.markBartanCollected(record.id, "2026-07-25") },
                            colors = ButtonDefaults.buttonColors(containerColor = VegGreen)
                        ) {
                            Text("Collect Bartan", fontSize = 10.sp)
                        }
                    } else {
                        Text("Returned ✅", color = VegGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
