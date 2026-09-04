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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.data.models.PaymentMethod
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen

import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ContactPhone

@Composable
fun AddressScreen(
    onBack: () -> Unit,
    onProceedToPayment: (selectedAddress: String, primaryPhone: String, alternatePhone: String) -> Unit,
    initialPhone: String = "+91 98765 11223",
    initialAltPhone: String = "",
    modifier: Modifier = Modifier
) {
    var savedAddresses by remember {
        mutableStateOf(
            listOf(
                "Flat 402, Green Park Apartments, Okhla Phase 3, New Delhi - 110020",
                "House 18, Block B, Preet Vihar, New Delhi - 110092"
            )
        )
    }
    var selectedAddress by remember { mutableStateOf(savedAddresses.first()) }
    var primaryPhone by remember { mutableStateOf(initialPhone) }
    var alternatePhone by remember { mutableStateOf(initialAltPhone) }
    var showAddDialog by remember { mutableStateOf(false) }

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
                IconButton(onClick = onBack, modifier = Modifier.testTag("address_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Delivery Address & Contact", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Saved Delivery Locations", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_address_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add New", fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(savedAddresses.size) { index ->
                val address = savedAddresses[index]
                val isSelected = address == selectedAddress
                Card(
                    onClick = { selectedAddress = address },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("address_card_$index"),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFFFF3E0) else Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) SaffronPrimary else Color.LightGray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedAddress = address },
                            colors = RadioButtonDefaults.colors(selectedColor = SaffronPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Home, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delivery Address #${index + 1}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(address, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
                        }
                    }
                }
            }

            // Phone Number Input Section (Requested by user)
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Contact Phone Numbers (फोन नंबर)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Halwai & Catering dispatch team will call on this number", fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = primaryPhone,
                            onValueChange = { primaryPhone = it },
                            label = { Text("Primary Mobile Number *") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                            },
                            placeholder = { Text("+91 98765 11223") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("primary_phone_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = alternatePhone,
                            onValueChange = { alternatePhone = it },
                            label = { Text("Alternate / Event Coordinator Phone (Optional)") },
                            leadingIcon = {
                                Icon(Icons.Default.ContactPhone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            },
                            placeholder = { Text("e.g. +91 98112 33445 (Venue Manager)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("alternate_phone_input")
                        )
                    }
                }
            }

            // Google Map Pin Simulation Box
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PinDrop, contentDescription = null, tint = Color(0xFF00695C), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Google Maps Location Pin", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF004D40))
                            Text("Point precisely mapped for catering delivery van entrance", fontSize = 11.sp, color = Color(0xFF00695C))
                        }
                    }
                }
            }
        }

        Surface(color = Color.White, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    val finalPhone = if (primaryPhone.isNotBlank()) primaryPhone else "+91 98765 11223"
                    onProceedToPayment(selectedAddress, finalPhone, alternatePhone)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("proceed_to_payment_button")
            ) {
                Text("Proceed to 50% Advance Payment 👉", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showAddDialog) {
        var newAddressText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Delivery Address", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newAddressText,
                        onValueChange = { newAddressText = it },
                        label = { Text("Complete Address with Pincode") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newAddressText.isNotBlank()) {
                            savedAddresses = savedAddresses + newAddressText
                            selectedAddress = newAddressText
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Save Address")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun PaymentScreen(
    totalAmount: Double,
    is30PercentAdvance: Boolean,
    deliveryAddress: String = "",
    customerPhone: String = "",
    deliveryDate: String = "",
    deliveryTimeSlot: String = "",
    onBack: () -> Unit,
    onConfirmPayment: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.UPI) }

    // User requested 50% Advance Payment
    val payableNow = if (is30PercentAdvance) totalAmount * 0.50 else totalAmount
    val balancePay = totalAmount - payableNow

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
                IconButton(onClick = onBack, modifier = Modifier.testTag("payment_back")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Catering Payment Gateway", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Catering Order", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text("₹${totalAmount.toInt()}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            if (is30PercentAdvance) "50% Advance Token Payable Today" else "Full Payment Payable Today",
                            color = AmberSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("₹${payableNow.toInt()}", color = AmberSecondary, fontSize = 28.sp, fontWeight = FontWeight.Bold)

                        if (is30PercentAdvance) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = Color(0xFF0F172A),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Remaining 50% Balance on Delivery:", color = Color(0xFFCBD5E1), fontSize = 11.5.sp)
                                    Text("₹${balancePay.toInt()}", color = VegGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                "🔒 50% token guarantees date reservation, sealed degh preparation & on-time delivery.",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.5.sp,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }

                if (deliveryDate.isNotBlank() || customerPhone.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (deliveryDate.isNotBlank()) {
                                Text("📅 Event Date & Slot: $deliveryDate • $deliveryTimeSlot", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                            }
                            if (customerPhone.isNotBlank()) {
                                Text("📞 Contact Mobile: $customerPhone", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Select Payment Option", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                PaymentMethodOption(
                    title = "UPI (Google Pay, PhonePe, Paytm)",
                    icon = Icons.Default.Smartphone,
                    method = PaymentMethod.UPI,
                    selected = selectedMethod,
                    onSelect = { selectedMethod = PaymentMethod.UPI }
                )
                PaymentMethodOption(
                    title = "Dynamic QR Code Scanner",
                    icon = Icons.Default.QrCodeScanner,
                    method = PaymentMethod.DYNAMIC_QR,
                    selected = selectedMethod,
                    onSelect = { selectedMethod = PaymentMethod.DYNAMIC_QR }
                )
                PaymentMethodOption(
                    title = "Credit / Debit Card",
                    icon = Icons.Default.CreditCard,
                    method = PaymentMethod.CARD,
                    selected = selectedMethod,
                    onSelect = { selectedMethod = PaymentMethod.CARD }
                )
                PaymentMethodOption(
                    title = "Net Banking / Corporate Wallet",
                    icon = Icons.Default.Money,
                    method = PaymentMethod.NET_BANKING,
                    selected = selectedMethod,
                    onSelect = { selectedMethod = PaymentMethod.NET_BANKING }
                )
                PaymentMethodOption(
                    title = "Cash Balance on Delivery",
                    icon = Icons.Default.Money,
                    method = PaymentMethod.CASH_ON_DELIVERY,
                    selected = selectedMethod,
                    onSelect = { selectedMethod = PaymentMethod.CASH_ON_DELIVERY }
                )
            }
        }

        Surface(color = Color.White, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { onConfirmPayment(selectedMethod) },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("pay_and_confirm_button")
            ) {
                Text(
                    if (is30PercentAdvance) "Pay 50% Advance (₹${payableNow.toInt()}) & Lock Booking" else "Pay Full ₹${payableNow.toInt()} & Confirm Booking",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodOption(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    method: PaymentMethod,
    selected: PaymentMethod,
    onSelect: () -> Unit
) {
    val isSelected = method == selected
    Card(
        onClick = onSelect,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("pay_method_${method.name}"),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFFFF3E0) else Color.White),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) SaffronPrimary else Color.LightGray),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = SaffronPrimary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(icon, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun OrderConfirmationScreen(
    orderId: String,
    onTrackOrder: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6F0))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(VegGreen, RoundedCornerShape(40.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Catering Booking Confirmed!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
        Text("Order ID: $orderId", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Your caterer has received the booking notification. Production schedule is active.",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { /* Invoice simulation */ }) {
                        Icon(Icons.Default.Download, contentDescription = "Download Invoice", tint = SaffronPrimary)
                    }
                    Text("Download Invoice", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { /* Share simulation */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = SaffronPrimary)
                    }
                    Text("Share Booking", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onTrackOrder,
            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("track_order_button")
        ) {
            Text("Track Live Order Status", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onGoHome,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("back_to_home_button")
        ) {
            Text("Back to Home")
        }
    }
}
