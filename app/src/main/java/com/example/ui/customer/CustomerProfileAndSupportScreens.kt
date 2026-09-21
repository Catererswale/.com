package com.example.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.example.ui.theme.VegGreen
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VpnKey
import android.widget.Toast
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
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
import com.example.data.models.UserRole
import com.example.data.repository.CaterersViewModel
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen

@Composable
fun CustomerProfileScreen(
    viewModel: CaterersViewModel,
    onBack: () -> Unit,
    onOpenSupport: () -> Unit,
    onTrackOrder: (String) -> Unit = {},
    onRateKitchen: (String) -> Unit = {},
    onOpenOrders: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val orders by viewModel.ordersList.collectAsState()
    val notifications by viewModel.notificationsList.collectAsState()
    val userNotifications = notifications.filter { it.targetRole == UserRole.CUSTOMER }
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    var showWalletDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var selectedRepeatOrder by remember { mutableStateOf<OrderEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6F0))
    ) {
        // Header
        Surface(color = SaffronPrimary, contentColor = Color.White) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("profile_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Customer Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            // Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(SaffronPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text("Rohan Verma", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("+91 98765 11223", fontSize = 13.sp, color = Color.Gray)
                            Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                                Text("Verified Customer", fontSize = 10.sp, color = VegGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Wallet Card (Page 19)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showWalletDialog = true }
                        .testTag("profile_wallet_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFE8F5E9), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = VegGreen, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Foodie Wallet & Passbook", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(color = VegGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                        Text("₹500.00", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VegGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Text("Earned cashback, referral bonus & instant refunds", fontSize = 11.5.sp, color = Color.Gray)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Language Selection Card (Page 18)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLanguageDialog = true }
                        .testTag("profile_language_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(SaffronPrimary.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("App Language (भाषा)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(color = AmberSecondary, shape = RoundedCornerShape(4.dp)) {
                                        Text(currentLanguage.displayName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Text("English / हिन्दी / Hinglish", fontSize = 11.5.sp, color = Color.Gray)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Navigation Card to My Orders
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenOrders() }
                        .testTag("profile_my_orders_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(SaffronPrimary.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("My Orders & Live Tracking", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(color = AmberSecondary, shape = RoundedCornerShape(4.dp)) {
                                        Text("${orders.size} Orders", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp))
                                    }
                                }
                                Text("Track active degh deliveries & rate past feast meals", fontSize = 11.5.sp, color = Color.Gray)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Order History Header
            item {
                Text("My Catering Order History", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(orders) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onTrackOrder(order.orderId) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("#${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SaffronPrimary)
                            Surface(
                                color = if (order.orderStatus.name == "DELIVERED") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(order.orderStatus.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(order.catererName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(order.itemsSummary, fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Date: ${order.deliveryDate} (${order.deliveryTimeSlot})", fontSize = 11.sp)
                            Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        if (order.orderStatus.name != "CANCELLED" && order.deliveryOtp.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            DeliveryOtpSmallColumn(
                                otp = order.deliveryOtp,
                                isDelivered = order.orderStatus.name == "DELIVERED"
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (order.orderStatus.name == "DELIVERED") {
                                OutlinedButton(
                                    onClick = { selectedRepeatOrder = order },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SaffronPrimary),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .padding(end = 8.dp)
                                        .testTag("repeat_order_${order.orderId}")
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Repeat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onRateKitchen(order.orderId) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SaffronPrimary),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .padding(end = 8.dp)
                                        .testTag("rate_partner_${order.orderId}")
                                ) {
                                    Text(
                                        text = if (order.userRating > 0f) "★ Rated ${order.userRating}" else "⭐ Rate Partner",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Button(
                                onClick = { onTrackOrder(order.orderId) },
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(32.dp).testTag("track_order_${order.orderId}")
                            ) {
                                Text(if (order.orderStatus.name == "DELIVERED") "View Delivery Summary" else "Track Live Order", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Notifications
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("App Notifications", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(userNotifications) { notif ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(notif.message, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onOpenSupport,
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_support_button")
                ) {
                    Icon(Icons.Default.SupportAgent, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Help & Customer Support")
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { showDeleteAccountDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("delete_account_button")
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFDC2626))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete My Account (Permanently)", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Dialogs & Modals
    if (showWalletDialog) {
        WalletTransactionsDialog(
            onDismiss = { showWalletDialog = false }
        )
    }

    if (showLanguageDialog) {
        ChangeLanguageDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { lang ->
                viewModel.switchLanguage(lang)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    if (showDeleteAccountDialog) {
        DeleteAccountFlowDialog(
            onDismiss = { showDeleteAccountDialog = false },
            onAccountDeleted = {
                showDeleteAccountDialog = false
                viewModel.logout()
            }
        )
    }

    selectedRepeatOrder?.let { order ->
        OrderDetailsRepeatModal(
            order = order,
            onDismiss = { selectedRepeatOrder = null },
            onRepeatOrder = {
                selectedRepeatOrder = null
                onOpenOrders()
            }
        )
    }
}

@Composable
fun HelpSupportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var ticketSubject by remember { mutableStateOf("") }
    var ticketDetails by remember { mutableStateOf("") }
    var ticketSubmitted by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6F0))
    ) {
        // Header
        Surface(color = SaffronPrimary, contentColor = Color.White) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("support_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Help & Customer Support", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            // Support Channels
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("call_support_card"),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Call Support", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("+91 1800-CAT-WALE", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("whatsapp_support_card"),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = VegGreen, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("WhatsApp Chat", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Instant Resolution", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Raise Support Ticket Form
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Raise a Support Ticket", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = ticketSubject,
                            onValueChange = { ticketSubject = it },
                            label = { Text("Issue Subject / Order ID") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ticket_subject_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = ticketDetails,
                            onValueChange = { ticketDetails = it },
                            label = { Text("Detailed Explanation") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ticket_details_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!ticketSubmitted) {
                            Button(
                                onClick = {
                                    if (ticketSubject.isNotBlank()) {
                                        ticketSubmitted = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("submit_ticket_button")
                            ) {
                                Text("Submit Ticket")
                            }
                        } else {
                            Text("Ticket #TK-9821 Created! Our support agent will call you within 15 mins.", color = VegGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // FAQs
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Frequently Asked Questions (FAQ)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        FaqItem("How does the 30% advance booking work?", "You pay 30% of the total amount at booking time to confirm the kitchen production slot. The remaining balance is paid on delivery via Cash or Dynamic QR UPI.")
                        FaqItem("Can I change my delivery date or time slot?", "No. To maintain bulk kitchen ingredient preparations, delivery date & time slots cannot be modified once confirmed.")
                        FaqItem("How is Bartan/Utensil return handled?", "Heavy serving pots (degs/handis) remain with you during your event. The delivery boy or kitchen collects them the following day.")
                    }
                }
            }
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text("Q: $question", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SaffronPrimary)
        Text(answer, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
    }
}

/**
 * Delivery OTP Small Column / Badge displayed inside Customer Order cards
 * Matching the Live Tracking OTP verification style requested by customer
 */
@Composable
fun DeliveryOtpSmallColumn(
    otp: String,
    isDelivered: Boolean,
    modifier: Modifier = Modifier
) {
    if (otp.isBlank()) return
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Surface(
        color = if (isDelivered) Color(0xFFF8FAFC) else Color(0xFF141416),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            1.dp,
            if (isDelivered) Color(0xFFCBD5E1) else Color(0xFFEA580C).copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                clipboardManager.setText(AnnotatedString(otp))
                Toast.makeText(context, "Delivery OTP $otp Copied to Clipboard! 📋", Toast.LENGTH_SHORT).show()
            }
            .testTag("delivery_otp_badge_$otp")
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 7.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isDelivered) Color(0xFFCBD5E1) else Color(0xFFEA580C),
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.VpnKey,
                            contentDescription = "OTP Key",
                            tint = if (isDelivered) Color(0xFF475569) else Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Delivery OTP",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDelivered) Color(0xFF334155) else Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = if (isDelivered) Color(0xFFDCFCE7) else Color(0xFFEA580C).copy(alpha = 0.25f),
                            shape = RoundedCornerShape(3.dp)
                        ) {
                            Text(
                                text = if (isDelivered) "VERIFIED ✅" else "COMPULSORY",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDelivered) Color(0xFF15803D) else Color(0xFFFB923C),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = if (isDelivered) "Verified with rider upon delivery" else "Share with driver at drop-off • Tap to copy",
                        fontSize = 9.5.sp,
                        color = if (isDelivered) Color(0xFF64748B) else Color(0xFFA1A1AA)
                    )
                }
            }

            // 4 digit blocks in vibrant orange
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                otp.forEach { char ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isDelivered) Color(0xFFE2E8F0) else Color(0xFFEA580C),
                        modifier = Modifier.size(width = 20.dp, height = 24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = char.toString(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDelivered) Color(0xFF1E293B) else Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = "Copy OTP",
                    tint = if (isDelivered) Color(0xFF94A3B8) else Color(0xFFFB923C),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun CustomerOrdersScreen(
    viewModel: CaterersViewModel,
    onBack: () -> Unit,
    onTrackOrder: (String) -> Unit,
    onRateKitchen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.ordersList.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: All, 1: Active, 2: Delivered & Rate
    var orderForCancellation by remember { mutableStateOf<OrderEntity?>(null) }
    var orderForReschedule by remember { mutableStateOf<OrderEntity?>(null) }

    val activeOrder = orders.firstOrNull { it.orderStatus != com.example.data.models.OrderStatus.DELIVERED && it.orderStatus != com.example.data.models.OrderStatus.CANCELLED }
    val latestDeliveredOrder = orders.firstOrNull { it.orderStatus == com.example.data.models.OrderStatus.DELIVERED }

    val filteredOrders = remember(orders, selectedTab) {
        when (selectedTab) {
            1 -> orders.filter { it.orderStatus != com.example.data.models.OrderStatus.DELIVERED && it.orderStatus != com.example.data.models.OrderStatus.CANCELLED }
            2 -> orders.filter { it.orderStatus == com.example.data.models.OrderStatus.DELIVERED }
            else -> orders
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6F0))
    ) {
        // Top App Bar
        Surface(color = SaffronPrimary, contentColor = Color.White) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("orders_screen_back_btn")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column {
                    Text("My Catering Orders (मेरे ऑर्डर्स)", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("Live Order Tracking & Kitchen Ratings", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                }
            }
        }

        // Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "All (${orders.size})" to 0,
                "🔴 Active & Track" to 1,
                "⭐ Rate & Review" to 2
            ).forEach { (label, index) ->
                val isSelected = selectedTab == index
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) SaffronPrimary else Color(0xFFF1F5F9),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = index }
                        .testTag("orders_tab_$index")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF475569)
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Spotlight 1: Active Order Live Tracking Spotlight (Moved from Home Page)
            if (activeOrder != null && (selectedTab == 0 || selectedTab == 1)) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTrackOrder(activeOrder.orderId) }
                            .testTag("orders_active_spotlight_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color(0xFF22C55E), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ACTIVE ORDER IN TRANSIT", color = Color(0xFF4ADE80), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                }
                                Surface(color = AmberSecondary, shape = RoundedCornerShape(4.dp)) {
                                    Text(
                                        text = activeOrder.orderStatus.name,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Order #${activeOrder.orderId}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                    Text("${activeOrder.catererName} • Slot: ${activeOrder.deliveryTimeSlot}", fontSize = 11.5.sp, color = Color(0xFF94A3B8))
                                    Text("Total: ₹${activeOrder.totalAmount.toInt()} (${activeOrder.itemsSummary})", fontSize = 11.sp, color = Color(0xFFCBD5E1), maxLines = 1)
                                }
                                Button(
                                    onClick = { onTrackOrder(activeOrder.orderId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("orders_track_live_order_btn")
                                ) {
                                    Text("🔴 Track Live", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (activeOrder.deliveryOtp.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                DeliveryOtpSmallColumn(
                                    otp = activeOrder.deliveryOtp,
                                    isDelivered = false
                                )
                            }
                        }
                    }
                }
            }

            // Spotlight 2: Delivered Order Feedback & Rating Spotlight (Moved from Home Page)
            if (latestDeliveredOrder != null && (selectedTab == 0 || selectedTab == 2)) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRateKitchen(latestDeliveredOrder.orderId) }
                            .testTag("orders_rate_spotlight_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFFFEF3C7), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("How was the feast from ${latestDeliveredOrder.catererName}?", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color(0xFF92400E))
                                    Text("Rate your experience & earn +50 Foodie Coins ⭐", fontSize = 11.sp, color = Color(0xFFB45309))
                                }
                            }

                            Button(
                                onClick = { onRateKitchen(latestDeliveredOrder.orderId) },
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("orders_rate_feast_btn")
                            ) {
                                Text("⭐ Rate Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Orders list header
            item {
                Text(
                    text = when (selectedTab) {
                        1 -> "Active Orders in Progress (${filteredOrders.size})"
                        2 -> "Delivered & Past Orders (${filteredOrders.size})"
                        else -> "All Orders History (${filteredOrders.size})"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
            }

            if (filteredOrders.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📦", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No orders found in this section", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Your past and active catering orders will appear here.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            items(filteredOrders) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTrackOrder(order.orderId) }
                        .testTag("order_item_card_${order.orderId}"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("#${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SaffronPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                if (order.orderStatus.name != "DELIVERED" && order.orderStatus.name != "CANCELLED") {
                                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                                        Text("LIVE TRACKABLE", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                    }
                                }
                            }
                            Surface(
                                color = if (order.orderStatus.name == "DELIVERED") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(order.orderStatus.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(order.catererName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(order.itemsSummary, fontSize = 11.5.sp, color = Color(0xFF475569))

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📅 ${order.deliveryDate} (${order.deliveryTimeSlot})", fontSize = 11.sp, color = Color.Gray)
                            Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                        }

                        // Delivery Verification OTP Small Column (Customer ko OTP dikhane ke liye)
                        if (order.orderStatus.name != "CANCELLED" && order.deliveryOtp.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            DeliveryOtpSmallColumn(
                                otp = order.deliveryOtp,
                                isDelivered = order.orderStatus.name == "DELIVERED"
                            )
                        }

                        // Bartan / Containers Flow (Visible to Customer in My Orders)
                        if (order.isBartanPending || order.bartanDescription.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = if (order.isBartanReturned) Color(0xFFF0FDF4) else Color(0xFFFFFBEB),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (order.isBartanReturned) Color(0xFFBBF7D0) else Color(0xFFFDE68A)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.SoupKitchen,
                                        contentDescription = null,
                                        tint = if (order.isBartanReturned) Color(0xFF16A34A) else AmberSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "🥘 Kitchen Bartan / Containers:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (order.isBartanReturned) Color(0xFF166534) else Color(0xFF92400E)
                                            )
                                            Surface(
                                                color = if (order.isBartanReturned) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = if (order.isBartanReturned) "Returned ✅" else "Issued With Order ⏳",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (order.isBartanReturned) Color(0xFF166534) else Color(0xFFB45309),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "${order.bartanDescription} • Handed over by delivery partner. Please keep safe for pickup.",
                                            fontSize = 10.5.sp,
                                            color = if (order.isBartanReturned) Color(0xFF14532D) else Color(0xFF78350F)
                                        )
                                    }
                                }
                            }
                        }

                        // If order is cancelled, show policy outcome summary
                        if (order.orderStatus.name == "CANCELLED") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color(0xFFFEF2F2),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "❌ Cancelled: ${order.cancellationReason.ifBlank { "Customer Request" }}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF991B1B)
                                    )
                                    if (order.refundAmount > 0) {
                                        Text("💰 Refund: ₹${order.refundAmount.toInt()} (100%) initiated", fontSize = 10.5.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                                    } else if (order.companyAdsFundAmount > 0) {
                                        Text("📣 Advance deposited to Platform Offers Fund (0% Return)", fontSize = 10.5.sp, color = Color(0xFF92400E))
                                    } else if (order.kitchenSettlementAmount > 0) {
                                        Text("👨‍🍳 Kitchen raw material settlement: ₹${order.kitchenSettlementAmount.toInt()} (0% Customer Return)", fontSize = 10.5.sp, color = Color(0xFF475569))
                                    }
                                }
                            }
                        }

                        // Rescheduled lock notice
                        if (order.isRescheduled || order.isNonCancellable) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = "🔒 Rescheduled (100% Full Payment Locked • Non-cancellable)",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D4ED8),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cancel Order Button for Active Eligible Orders
                            if (order.orderStatus.name != "DELIVERED" &&
                                order.orderStatus.name != "CANCELLED" &&
                                order.orderStatus.name != "OUT_FOR_DELIVERY" &&
                                !order.isNonCancellable
                            ) {
                                OutlinedButton(
                                    onClick = { orderForCancellation = order },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .padding(end = 8.dp)
                                        .testTag("cancel_order_btn_${order.orderId}")
                                ) {
                                    Text("Cancel / Reschedule", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            if (order.orderStatus.name == "DELIVERED") {
                                OutlinedButton(
                                    onClick = { onRateKitchen(order.orderId) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SaffronPrimary),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .padding(end = 8.dp)
                                        .testTag("rate_order_btn_${order.orderId}")
                                ) {
                                    Text(
                                        text = if (order.userRating > 0f) "★ Rated ${order.userRating}" else "⭐ Rate Kitchen",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Button(
                                onClick = { onTrackOrder(order.orderId) },
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("track_order_btn_${order.orderId}")
                            ) {
                                Text(
                                    text = if (order.orderStatus.name == "DELIVERED") "View Summary" else "🔴 Track Live Order",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Customer Cancellation Dialog
        orderForCancellation?.let { ord ->
            CustomerCancellationDialog(
                order = ord,
                onDismiss = { orderForCancellation = null },
                onConfirmCancel = { reason ->
                    viewModel.cancelOrderByCustomer(ord.orderId, reason)
                    orderForCancellation = null
                },
                onSwitchToReschedule = {
                    val target = ord
                    orderForCancellation = null
                    orderForReschedule = target
                }
            )
        }

        // Customer Reschedule Dialog
        orderForReschedule?.let { ord ->
            CustomerRescheduleDialog(
                order = ord,
                onDismiss = { orderForReschedule = null },
                onConfirmReschedule = { newDate, newSlot ->
                    viewModel.rescheduleOrderByCustomer(ord.orderId, newDate, newSlot)
                    orderForReschedule = null
                }
            )
        }
    }
}
