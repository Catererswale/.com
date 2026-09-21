package com.example.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import android.content.Intent
import com.example.data.repository.CaterersViewModel
import com.example.data.models.OrderEntity
import com.example.data.models.PaymentStatus
import androidx.compose.runtime.collectAsState
import com.example.util.NotificationHelper

@Composable
fun AddressScreen(
    onBack: () -> Unit,
    onProceedToPayment: (selectedAddress: String, primaryPhone: String, alternatePhone: String) -> Unit,
    initialPhone: String = "+91 98765 11223",
    initialAltPhone: String = "",
    defaultAddress: String = "Okhla Phase 3, New Delhi",
    is30PercentAdvance: Boolean = true,
    modifier: Modifier = Modifier
) {
    var savedAddresses by remember(defaultAddress) {
        mutableStateOf(
            listOf(
                if (defaultAddress.isNotBlank()) defaultAddress else "Flat 402, Green Park Apartments, Okhla Phase 3, New Delhi - 110020",
                "Flat 402, Green Park Apartments, Okhla Phase 3, New Delhi - 110020",
                "House 18, Block B, Preet Vihar, New Delhi - 110092"
            ).distinct()
        )
    }
    var selectedAddress by remember(savedAddresses) { mutableStateOf(savedAddresses.first()) }
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
                Text(
                    if (is30PercentAdvance) "Proceed to 50% Advance Payment 👉" else "Proceed to 100% Full Payment 👉",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showAddDialog) {
        var addressType by remember { mutableStateOf("Banquet / Venue 🎪") }
        var houseFlatText by remember { mutableStateOf("") }
        var floorBuildingText by remember { mutableStateOf("") }
        var streetAreaText by remember { mutableStateOf("") }
        var landmarkText by remember { mutableStateOf("") }
        var cityText by remember { mutableStateOf("New Delhi") }
        var pincodeText by remember { mutableStateOf("110025") }

        Dialog(
            onDismissRequest = { showAddDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .heightIn(max = 660.dp)
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Dialog Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PinDrop, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Delivery Address", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF0F172A))
                            }
                            Text("अलग-अलग कॉलम में पूरा पता भरें (Multi-Column Address)", fontSize = 11.5.sp, color = Color.Gray)
                        }
                        IconButton(onClick = { showAddDialog = false }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Scrollable Fields Column
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Address Type Selector Chips
                        Text("Address Type (स्थान का प्रकार):", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color(0xFF334155))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Venue / Banquet 🎪", "Home 🏠", "Office 🏢", "Other 📍").forEach { tag ->
                                val isSelected = addressType == tag
                                Surface(
                                    color = if (isSelected) Color(0xFFFFF3E0) else Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(20.dp),
                                    border = BorderStroke(1.dp, if (isSelected) SaffronPrimary else Color(0xFFE2E8F0)),
                                    modifier = Modifier.clickable { addressType = tag }
                                ) {
                                    Text(
                                        text = tag,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) SaffronPrimary else Color(0xFF475569),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Column 1: House / Flat / Banquet Hall Name & Number
                        OutlinedTextField(
                            value = houseFlatText,
                            onValueChange = { houseFlatText = it },
                            label = { Text("1. Flat / House / Banquet Hall Name & No. *") },
                            placeholder = { Text("e.g. Royal Palace Banquet / Flat 402, Block A") },
                            leadingIcon = {
                                Icon(Icons.Default.Home, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_addr_house_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Column 2: Floor / Building / Society / Block
                        OutlinedTextField(
                            value = floorBuildingText,
                            onValueChange = { floorBuildingText = it },
                            label = { Text("2. Floor / Building / Society (Optional)") },
                            placeholder = { Text("e.g. Ground Floor, Garden Wing / Tower C") },
                            leadingIcon = {
                                Icon(Icons.Default.Business, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_addr_floor_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Column 3: Street / Road / Area / Colony
                        OutlinedTextField(
                            value = streetAreaText,
                            onValueChange = { streetAreaText = it },
                            label = { Text("3. Street, Road & Locality / Colony *") },
                            placeholder = { Text("e.g. Main Ring Road, Lajpat Nagar 4") },
                            leadingIcon = {
                                Icon(Icons.Default.Navigation, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_addr_street_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Column 4: Nearby Landmark
                        OutlinedTextField(
                            value = landmarkText,
                            onValueChange = { landmarkText = it },
                            label = { Text("4. Nearby Landmark (पहचान)") },
                            placeholder = { Text("e.g. Near Shiv Mandir / Metro Pillar 45") },
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_addr_landmark_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Column 5 & 6: City and Pincode side-by-side in 2 Columns
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = cityText,
                                onValueChange = { cityText = it },
                                label = { Text("5. City / District *") },
                                placeholder = { Text("e.g. New Delhi") },
                                singleLine = true,
                                modifier = Modifier.weight(1.1f).testTag("add_addr_city_input"),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = pincodeText,
                                onValueChange = { if (it.length <= 6) pincodeText = it },
                                label = { Text("6. Pincode *") },
                                placeholder = { Text("110025") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(0.9f).testTag("add_addr_pincode_input"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        // Live Preview of Full Formatted Address
                        val previewParts = listOf(
                            houseFlatText.trim(),
                            floorBuildingText.trim(),
                            streetAreaText.trim(),
                            if (landmarkText.isNotBlank()) "Near ${landmarkText.trim()}" else "",
                            if (cityText.isNotBlank()) "${cityText.trim()}${if (pincodeText.isNotBlank()) " - ${pincodeText.trim()}" else ""}" else pincodeText.trim()
                        ).filter { it.isNotBlank() }

                        if (previewParts.isNotEmpty()) {
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Address Preview (पूरा पता preview):", fontSize = 10.5.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${addressType.split(" ").last()} ${previewParts.joinToString(", ")}",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Buttons (Cancel & Save Address)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddDialog = false },
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Cancel", fontSize = 13.5.sp, color = Color.Gray)
                        }

                        Button(
                            onClick = {
                                val parts = listOf(
                                    houseFlatText.trim(),
                                    floorBuildingText.trim(),
                                    streetAreaText.trim(),
                                    if (landmarkText.isNotBlank()) "Near ${landmarkText.trim()}" else "",
                                    if (cityText.isNotBlank()) "${cityText.trim()}${if (pincodeText.isNotBlank()) " - ${pincodeText.trim()}" else ""}" else pincodeText.trim()
                                ).filter { it.isNotBlank() }

                                val iconPrefix = addressType.split(" ").last()
                                val fullAddress = if (parts.isNotEmpty()) {
                                    "$iconPrefix ${parts.joinToString(", ")}"
                                } else ""

                                if (fullAddress.isNotBlank()) {
                                    savedAddresses = savedAddresses + fullAddress
                                    selectedAddress = fullAddress
                                    showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            enabled = houseFlatText.isNotBlank() || streetAreaText.isNotBlank(),
                            modifier = Modifier.weight(1.4f).height(46.dp).testTag("save_address_dialog_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Address", fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
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
    var selectedMethod by remember { mutableStateOf(PaymentMethod.DYNAMIC_QR) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var upiIdInput by remember { mutableStateOf("") }
    var cardNumberInput by remember { mutableStateOf("") }
    var cardExpiryInput by remember { mutableStateOf("") }
    var cardCvvInput by remember { mutableStateOf("") }
    var selectedBank by remember { mutableStateOf("State Bank of India") }
    var copiedUpiId by remember { mutableStateOf(false) }

    // User requested 50% Advance Payment
    val payableNow = if (is30PercentAdvance) totalAmount * 0.50 else totalAmount
    val balancePay = totalAmount - payableNow

    if (isProcessingPayment) {
        Dialog(onDismissRequest = { /* Protected during banking gateway verification */ }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = SaffronPrimary,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Connecting Bank Gateway 🔒",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Verifying ₹${payableNow.toInt()} via ${selectedMethod.name.replace("_", " ")}...",
                        fontSize = 12.5.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Do not press back or refresh the page",
                        fontSize = 11.5.sp,
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        LaunchedEffect(Unit) {
            delay(1300L)
            isProcessingPayment = false
            onConfirmPayment(selectedMethod)
        }
    }

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
                        } else {
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
                                    Text("Remaining Balance on Delivery:", color = Color(0xFFCBD5E1), fontSize = 11.5.sp)
                                    Text("₹0 (Fully Paid)", color = VegGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                "✨ 100% full payment selected: No balance left to pay during delivery.",
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
                Text(
                    if (is30PercentAdvance) "Select 50% Advance Online Payment Mode" else "Select 100% Full Online Payment Mode",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    if (is30PercentAdvance) "Catering date lock aur rashan booking ke liye 50% advance anivarya hai." else "Pura payment online ho raha hai. Delivery par ₹0 balance rahega.",
                    fontSize = 11.5.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                PaymentMethodOption(
                    title = "UPI (Google Pay, PhonePe, Paytm, BHIM)",
                    icon = Icons.Default.Smartphone,
                    method = PaymentMethod.UPI,
                    selected = selectedMethod,
                    onSelect = { selectedMethod = PaymentMethod.UPI }
                )
                PaymentMethodOption(
                    title = "Dynamic UPI QR Code (Instant Scan & Pay)",
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
                    title = "Net Banking / Corporate Netbanking",
                    icon = Icons.Default.Money,
                    method = PaymentMethod.NET_BANKING,
                    selected = selectedMethod,
                    onSelect = { selectedMethod = PaymentMethod.NET_BANKING }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Payment Details according to selected method
                PaymentMethodDetailsCard(
                    method = selectedMethod,
                    payableNow = payableNow,
                    upiIdInput = upiIdInput,
                    onUpiIdChange = { upiIdInput = it },
                    cardNumberInput = cardNumberInput,
                    onCardNumberChange = { cardNumberInput = it },
                    cardExpiryInput = cardExpiryInput,
                    onCardExpiryChange = { cardExpiryInput = it },
                    cardCvvInput = cardCvvInput,
                    onCardCvvChange = { cardCvvInput = it },
                    selectedBank = selectedBank,
                    onSelectBank = { selectedBank = it },
                    copiedUpiId = copiedUpiId,
                    onCopyUpiId = { copiedUpiId = true }
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (is30PercentAdvance && balancePay > 0) {
                    // Remaining 50% Balance on Delivery Information Card (Only for 50% Advance)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFDCFCE7),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VegGreen, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        "Baki 50% Balance (₹${balancePay.toInt()}) Delivery Par Dijiye",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF166534)
                                    )
                                    Text(
                                        "Khana deliver hone par delivery boy ko cash ya UPI se payment karein",
                                        fontSize = 11.sp,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Money, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Cash On Delivery (₹${balancePay.toInt()})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF15803D))
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("UPI on Delivery (₹${balancePay.toInt()})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF15803D))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // 100% Full Payment Reassurance Card (Zero Balance Remaining)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFDCFCE7),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VegGreen, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "100% Full Payment (Zero Balance)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = Color(0xFF166534)
                                )
                                Text(
                                    "Delivery ke time par delivery boy ko koi extra payment nahi deni hai. Aapka order 100% prepaid confirm hoga.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF15803D),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Surface(color = Color.White, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { isProcessingPayment = true },
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
private fun PaymentMethodDetailsCard(
    method: PaymentMethod,
    payableNow: Double,
    upiIdInput: String,
    onUpiIdChange: (String) -> Unit,
    cardNumberInput: String,
    onCardNumberChange: (String) -> Unit,
    cardExpiryInput: String,
    onCardExpiryChange: (String) -> Unit,
    cardCvvInput: String,
    onCardCvvChange: (String) -> Unit,
    selectedBank: String,
    onSelectBank: (String) -> Unit,
    copiedUpiId: Boolean,
    onCopyUpiId: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            when (method) {
                PaymentMethod.DYNAMIC_QR -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dynamic UPI QR Code", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "⏱️ 09:59 mins valid",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
                            shadowElevation = 3.dp,
                            modifier = Modifier.size(200.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Canvas(modifier = Modifier.size(160.dp)) {
                                    val moduleSize = size.width / 25f
                                    val darkColor = androidx.compose.ui.graphics.Color(0xFF0F172A)
                                    val saffronColor = androidx.compose.ui.graphics.Color(0xFFE65100)

                                    fun drawFinder(startX: Float, startY: Float) {
                                        drawRect(
                                            color = darkColor,
                                            topLeft = Offset(startX, startY),
                                            size = Size(moduleSize * 7, moduleSize * 7)
                                        )
                                        drawRect(
                                            color = androidx.compose.ui.graphics.Color.White,
                                            topLeft = Offset(startX + moduleSize, startY + moduleSize),
                                            size = Size(moduleSize * 5, moduleSize * 5)
                                        )
                                        drawRect(
                                            color = darkColor,
                                            topLeft = Offset(startX + moduleSize * 2, startY + moduleSize * 2),
                                            size = Size(moduleSize * 3, moduleSize * 3)
                                        )
                                    }

                                    drawFinder(0f, 0f)
                                    drawFinder(moduleSize * 18, 0f)
                                    drawFinder(0f, moduleSize * 18)

                                    for (r in 0 until 25) {
                                        for (c in 0 until 25) {
                                            val inTopLeft = r < 8 && c < 8
                                            val inTopRight = r < 8 && c >= 17
                                            val inBottomLeft = r >= 17 && c < 8
                                            val inCenterBadge = r in 9..15 && c in 9..15

                                            if (!inTopLeft && !inTopRight && !inBottomLeft && !inCenterBadge) {
                                                val hash = (r * 31 + c * 17 + payableNow.toInt()) % 11
                                                if (hash in listOf(0, 2, 3, 5, 7, 8)) {
                                                    drawRect(
                                                        color = if ((r + c) % 5 == 0) saffronColor else darkColor,
                                                        topLeft = Offset(c * moduleSize, r * moduleSize),
                                                        size = Size(moduleSize * 0.92f, moduleSize * 0.92f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Surface(
                                    color = Color(0xFFFFF7ED),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.5.dp, SaffronPrimary),
                                    shadowElevation = 2.dp,
                                    modifier = Modifier.padding(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("₹${payableNow.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SaffronPrimary)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Scan with Google Pay, PhonePe, Paytm, BHIM or any UPI app",
                        fontSize = 11.5.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Caterer Central UPI ID", fontSize = 10.sp, color = Color.Gray)
                                Text("catererswale.central@icici", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                            }
                            OutlinedButton(
                                onClick = onCopyUpiId,
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, if (copiedUpiId) VegGreen else SaffronPrimary)
                            ) {
                                Icon(
                                    if (copiedUpiId) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = if (copiedUpiId) VegGreen else SaffronPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (copiedUpiId) "Copied!" else "Copy", fontSize = 11.sp, color = if (copiedUpiId) VegGreen else SaffronPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                PaymentMethod.UPI -> {
                    Text("Instant UPI Apps (तुरंत भुगतान)", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Google Pay", "PhonePe", "Paytm", "BHIM").forEach { appName ->
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onUpiIdChange("user@" + appName.lowercase().replace(" ", "")) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Smartphone, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(appName, fontSize = 10.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Or Enter Your UPI ID (VPA)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = upiIdInput,
                        onValueChange = onUpiIdChange,
                        placeholder = { Text("e.g. 9876543210@paytm or name@okhdfcbank", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upi_id_input_field"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("A payment request of ₹${payableNow.toInt()} will be sent to your UPI app", fontSize = 10.5.sp, color = Color.Gray)
                }

                PaymentMethod.CARD -> {
                    Text("Credit or Debit Card", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = cardNumberInput,
                        onValueChange = { if (it.length <= 19) onCardNumberChange(it) },
                        label = { Text("Card Number") },
                        placeholder = { Text("XXXX XXXX XXXX XXXX") },
                        modifier = Modifier.fillMaxWidth().testTag("card_number_input"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = cardExpiryInput,
                            onValueChange = { if (it.length <= 5) onCardExpiryChange(it) },
                            label = { Text("Expiry (MM/YY)") },
                            placeholder = { Text("12/28") },
                            modifier = Modifier.weight(1f).testTag("card_expiry_input"),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = cardCvvInput,
                            onValueChange = { if (it.length <= 4) onCardCvvChange(it) },
                            label = { Text("CVV") },
                            placeholder = { Text("•••") },
                            modifier = Modifier.weight(1f).testTag("card_cvv_input"),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = VegGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Secured with 256-Bit SSL Banking Protection", fontSize = 10.5.sp, color = VegGreen)
                    }
                }

                PaymentMethod.NET_BANKING -> {
                    Text("Select Bank for Netbanking", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    val banks = listOf("State Bank of India", "HDFC Bank", "ICICI Bank", "Axis Bank", "Punjab National Bank", "Kotak Bank")
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        banks.chunked(2).forEach { rowBanks ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowBanks.forEach { b ->
                                    val isSelectedBank = b == selectedBank
                                    Surface(
                                        color = if (isSelectedBank) Color(0xFFFFF7ED) else Color(0xFFF8FAFC),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, if (isSelectedBank) SaffronPrimary else Color(0xFFE2E8F0)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onSelectBank(b) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = isSelectedBank,
                                                onClick = { onSelectBank(b) },
                                                colors = RadioButtonDefaults.colors(selectedColor = SaffronPrimary),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(b, fontSize = 11.5.sp, fontWeight = if (isSelectedBank) FontWeight.Bold else FontWeight.Normal, maxLines = 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {
                    Text("Selected payment method: ${method.name}", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun OrderConfirmationScreen(
    orderId: String,
    onTrackOrder: () -> Unit,
    onGoHome: () -> Unit,
    onViewOrders: () -> Unit = onGoHome,
    viewModel: CaterersViewModel? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    val orders = viewModel?.ordersList?.collectAsState()?.value ?: emptyList()
    val order = orders.find { it.orderId == orderId } ?: orders.firstOrNull()

    var showInvoiceDialog by remember { mutableStateOf(false) }

    val catererName = order?.catererName ?: "A1 Huma Caterers"
    val deliveryDate = order?.deliveryDate ?: "2026-07-25"
    val deliveryTimeSlot = order?.deliveryTimeSlot ?: "12:30 PM - 01:00 PM"
    val deliveryAddress = order?.deliveryAddress ?: "Flat 402, Green Park Apartments, Okhla Phase 3"
    val totalAmount = order?.totalAmount ?: 1060.0
    val advancePaid = order?.advancePaidAmount ?: totalAmount
    val balanceAmount = order?.balanceAmount ?: 0.0
    val isFullPaid = balanceAmount <= 0.0 || order?.paymentStatus == PaymentStatus.FULL_PAID
    val deliveryOtp = order?.deliveryOtp ?: "4829"

    fun shareBooking() {
        val shareText = """
            🎉 Catering Booking Confirmed on Caterers Wale!
            📦 Order ID: $orderId
            👨‍🍳 Caterer: $catererName
            📅 Delivery: $deliveryDate ($deliveryTimeSlot)
            📍 Venue: $deliveryAddress
            💰 Total: ₹${totalAmount.toInt()} (${if (isFullPaid) "100% Fully Paid" else "Advance Paid: ₹${advancePaid.toInt()} | Balance: ₹${balanceAmount.toInt()}"})
            🔐 Delivery OTP: $deliveryOtp
            
            Track live order status on Caterers Wale App!
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Booking Details")
        try {
            context.startActivity(shareIntent)
            viewModel?.showFeedback("Opening sharing options... 📤")
        } catch (_: Exception) {
            clipboardManager.setText(AnnotatedString(shareText))
            viewModel?.showFeedback("Booking summary copied to clipboard! 📋")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6F0))
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Success badge
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(VegGreen, RoundedCornerShape(38.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(46.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Catering Booking Confirmed!",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color(0xFFFEF3C7), RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Order ID: $orderId",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB45309)
            )
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(orderId))
                    viewModel?.showFeedback("Order ID #$orderId copied! 📋")
                },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Order ID", tint = Color(0xFFB45309), modifier = Modifier.size(14.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your caterer has received the booking notification. Production schedule is active.",
            fontSize = 12.5.sp,
            color = Color(0xFF64748B),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Order Summary Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = catererName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = if (order?.itemsSummary?.isNotBlank() == true) order.itemsSummary else "Catering Menu Package",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            maxLines = 2
                        )
                    }
                    Surface(
                        color = if (isFullPaid) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isFullPaid) "100% Paid" else "50% Advance",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFullPaid) Color(0xFF166534) else Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(deliveryDate, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(deliveryTimeSlot, fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${totalAmount.toInt()}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                        if (!isFullPaid) {
                            Text(
                                text = "Bal: ₹${balanceAmount.toInt()} on delivery",
                                fontSize = 10.5.sp,
                                color = Color(0xFFDC2626),
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(
                                text = "Zero Balance",
                                fontSize = 10.5.sp,
                                color = VegGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = deliveryAddress,
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Tiles: Download Invoice & Share Booking
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Download / View Invoice Tile
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showInvoiceDialog = true }
                    .testTag("download_invoice_tile"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFEFF6FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Tax Invoice", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Text("View & Save PDF", fontSize = 10.5.sp, color = Color(0xFF64748B))
                    }
                }
            }

            // Share Booking Tile
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { shareBooking() }
                    .testTag("share_booking_tile"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFF0FDF4), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = VegGreen, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Share Booking", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Text("WhatsApp / SMS", fontSize = 10.5.sp, color = Color(0xFF64748B))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // PRIMARY BUTTON: Track Live Order Status
        Button(
            onClick = {
                viewModel?.showFeedback("⚡ Opening Live GPS Tracking for #$orderId...")
                onTrackOrder()
            },
            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("track_order_button")
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Track Live Order Status", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // SECONDARY BUTTON: View in My Orders
        OutlinedButton(
            onClick = {
                viewModel?.showFeedback("Opening My Catering Bookings...")
                onViewOrders()
            },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF334155)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("view_orders_button")
        ) {
            Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("View in My Orders", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TERTIARY BUTTON: Back to Home
        TextButton(
            onClick = {
                viewModel?.showFeedback("Welcome back to Home")
                onGoHome()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("back_to_home_button")
        ) {
            Icon(Icons.Default.Home, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Back to Home Screen", fontSize = 13.5.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
        }
    }

    // Official Tax Invoice Modal Dialog
    if (showInvoiceDialog) {
        AlertDialog(
            onDismissRequest = { showInvoiceDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Official Tax Invoice", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF0F172A))
                        Text("INV-$orderId", fontSize = 12.sp, color = SaffronPrimary, fontWeight = FontWeight.SemiBold)
                    }
                    IconButton(onClick = { showInvoiceDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("CATERER DETAILS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                    Text(catererName, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                    Text("GSTIN: 07AABCC1234D1Z5 • FSSAI Lic: 13322999000142", fontSize = 10.5.sp, color = Color(0xFF64748B))

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("BILLED TO", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                    Text(order?.customerName ?: "Rohan Verma", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                    Text(order?.customerMobile ?: "+91 98765 11223", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text(deliveryAddress, fontSize = 11.sp, color = Color(0xFF64748B))

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("BOOKING PARTICULARS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                    Text(if (order?.itemsSummary?.isNotBlank() == true) order.itemsSummary else "Catering Order Items", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("Delivery Slot: $deliveryDate ($deliveryTimeSlot)", fontSize = 11.sp, color = Color(0xFF64748B))

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text("₹${(totalAmount * 0.95).toInt()}", fontSize = 12.sp, color = Color(0xFF1E293B))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("GST (5% Catering GST)", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text("₹${(totalAmount * 0.05).toInt()}", fontSize = 12.sp, color = Color(0xFF1E293B))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery & Degs Handling", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text("FREE (Special Offer)", fontSize = 12.sp, color = VegGreen, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text("₹${totalAmount.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Amount Paid (${if (isFullPaid) "100% Full" else "Advance"})", fontSize = 12.sp, color = VegGreen, fontWeight = FontWeight.SemiBold)
                        Text("₹${advancePaid.toInt()}", fontSize = 12.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                    }
                    if (!isFullPaid) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Balance Due on Delivery", fontSize = 12.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                            Text("₹${balanceAmount.toInt()}", fontSize = 12.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Payment Method: ${order?.paymentMethod ?: "Online Payment"} • Status: ${if (isFullPaid) "SUCCESS / FULLY PAID" else "ADVANCE PAID"}",
                            fontSize = 10.5.sp,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            NotificationHelper.showSystemNotification(
                                context = context,
                                title = "Tax Invoice Downloaded 📄",
                                message = "Invoice INV-$orderId has been saved to your downloads."
                            )
                        } catch (_: Exception) {}
                        viewModel?.showFeedback("Tax Invoice #INV-$orderId saved to device! 📄")
                        showInvoiceDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Download PDF")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        shareBooking()
                        showInvoiceDialog = false
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share")
                }
            }
        )
    }
}
