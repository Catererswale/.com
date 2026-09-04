package com.example.ui.customer

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SupportAgent
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
    modifier: Modifier = Modifier
) {
    val orders by viewModel.ordersList.collectAsState()
    val notifications by viewModel.notificationsList.collectAsState()
    val userNotifications = notifications.filter { it.targetRole == UserRole.CUSTOMER }

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
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (order.orderStatus.name == "DELIVERED") {
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
            }
        }
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
