package com.example.ui.customer

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CartItemEntity
import com.example.data.models.Language
import com.example.data.models.OrderEntity
import com.example.data.models.OrderStatus
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen

/**
 * Page 10 from Image 1: Complete Order Summary Screen
 * Shows items breakdown, Item Total, Delivery Charges, Packing Charges,
 * "You save ₹60 on this order", and "Continue to Payment" button.
 */
@Composable
fun CustomerOrderSummaryScreen(
    catererName: String,
    items: List<CartItemEntity>,
    totalAmount: Double,
    deliveryAddress: String,
    deliveryDate: String,
    deliveryTimeSlot: String,
    customerPhone: String,
    onBack: () -> Unit,
    onProceedToPayment: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemTotal = items.sumOf { it.pricePerUnit * it.quantity }
    val deliveryFee = 30.0
    val packingFee = 20.0
    val discountSaved = 60.0
    val finalCalculatedTotal = (itemTotal + deliveryFee + packingFee).coerceAtLeast(totalAmount)

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
                IconButton(onClick = onBack, modifier = Modifier.testTag("summary_back_btn")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Order Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Caterer Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SaffronPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Kitchen, contentDescription = null, tint = SaffronPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(catererName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                            Text("📅 $deliveryDate • $deliveryTimeSlot", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // Items List Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Selected Items (${items.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.height(10.dp))

                        items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (item.foodType.name == "VEG") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (item.foodType.name == "VEG") VegGreen else Color.Red),
                                        modifier = Modifier.size(12.dp)
                                    ) {}
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "${item.name} (${item.quantity.toInt()} ${item.unitType.name})",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                                Text("₹${(item.pricePerUnit * item.quantity).toInt()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }

            // Delivery Address Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(deliveryAddress, fontSize = 12.sp, color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("📞 Contact: $customerPhone", fontSize = 11.5.sp, color = Color.Gray)
                    }
                }
            }

            // Bill Breakdown Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Bill Details", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Item Total", fontSize = 13.sp, color = Color.Gray)
                            Text("₹${itemTotal.toInt()}", fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Delivery Charges", fontSize = 13.sp, color = Color.Gray)
                            Text("₹${deliveryFee.toInt()}", fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Packing Charges", fontSize = 13.sp, color = Color.Gray)
                            Text("₹${packingFee.toInt()}", fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Amount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text("₹${finalCalculatedTotal.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Savings Banner from Page 10
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = VegGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "🎉 You save ₹${discountSaved.toInt()} on this order with CaterersWale offer!",
                                    color = VegGreen,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom CTA Button
        Surface(color = Color.White, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Payable", fontSize = 11.sp, color = Color.Gray)
                    Text("₹${finalCalculatedTotal.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                }

                Button(
                    onClick = onProceedToPayment,
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("summary_continue_to_payment_btn")
                ) {
                    Text("Continue to Payment 👉", fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Page 19 from Image 1: Wallet & Transactions Modal
 */
@Composable
fun WalletTransactionsDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var walletBalance by remember { mutableStateOf(250.0) }
    var showAddMoneyInput by remember { mutableStateOf(false) }
    var addAmountText by remember { mutableStateOf("500") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = SaffronPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Wallet & Cashback", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Wallet Balance Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Current Balance", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("₹${walletBalance.toInt()}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AmberSecondary)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { showAddMoneyInput = !showAddMoneyInput },
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Money", fontSize = 11.sp)
                            }
                        }
                    }
                }

                if (showAddMoneyInput) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = addAmountText,
                            onValueChange = { addAmountText = it },
                            label = { Text("Amount (₹)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                val amt = addAmountText.toDoubleOrNull() ?: 0.0
                                if (amt > 0) {
                                    walletBalance += amt
                                    showAddMoneyInput = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Deposit", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Recent Transactions", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(8.dp))

                val txns = listOf(
                    Triple("Catering Booking Cashback", "+₹100", "12 May 2024"),
                    Triple("Applied on Order CW12052400123", "-₹150", "10 May 2024"),
                    Triple("Promotional SignUp Bonus", "+₹300", "01 May 2024")
                )

                txns.forEach { (desc, amount, date) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(desc, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
                            Text(date, fontSize = 10.sp, color = Color.Gray)
                        }
                        Text(
                            text = amount,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (amount.startsWith("+")) VegGreen else Color.Red
                        )
                    }
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", fontWeight = FontWeight.Bold, color = SaffronPrimary)
            }
        }
    )
}

/**
 * Page 20 from Image 1: Delete Account (4-Step Secure Flow)
 * Step 1: Reason for Delete
 * Step 2: Confirm Details (Warning)
 * Step 3: OTP Verification
 * Step 4: Account Deleted Successfully
 */
@Composable
fun DeleteAccountFlowDialog(
    onDismiss: () -> Unit,
    onAccountDeleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(1) }
    var selectedReason by remember { mutableStateOf("Not using the app") }
    var isWarningUnderstood by remember { mutableStateOf(false) }
    var otpDigit1 by remember { mutableStateOf("1") }
    var otpDigit2 by remember { mutableStateOf("2") }
    var otpDigit3 by remember { mutableStateOf("3") }
    var otpDigit4 by remember { mutableStateOf("4") }
    var otpDigit5 by remember { mutableStateOf("5") }
    var otpDigit6 by remember { mutableStateOf("6") }

    val reasons = listOf(
        "Not using the app",
        "Found better option",
        "Too many offers",
        "Privacy concerns",
        "Other"
    )

    AlertDialog(
        onDismissRequest = {
            if (currentStep != 4) onDismiss() else {
                onAccountDeleted()
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        when (currentStep) {
                            1 -> "Step 1: Reason for Delete"
                            2 -> "Step 2: Confirm Details"
                            3 -> "Step 3: OTP Verification"
                            else -> "Step 4: Account Deleted"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (currentStep == 4) VegGreen else Color(0xFFDC2626)
                    )
                    Text("Secure Account Removal Flow", fontSize = 10.5.sp, color = Color.Gray)
                }
                if (currentStep != 4) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                when (currentStep) {
                    1 -> {
                        Text("Why do you want to delete your account?", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(10.dp))
                        reasons.forEach { reason ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedReason = reason }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedReason == reason,
                                    onClick = { selectedReason = reason },
                                    colors = RadioButtonDefaults.colors(selectedColor = SaffronPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(reason, fontSize = 12.5.sp)
                            }
                        }
                    }

                    2 -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("This action is permanent and cannot be undone.", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF991B1B), textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "All your data will be deleted including past bookings, catering schedules, active cart, and wallet cashback.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF7F1D1D),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isWarningUnderstood = !isWarningUnderstood }
                        ) {
                            Checkbox(
                                checked = isWarningUnderstood,
                                onCheckedChange = { isWarningUnderstood = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFDC2626))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("I understand and want to delete my account", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    3 -> {
                        Text("Enter 6 digit OTP sent to +91 98765 43210", fontSize = 12.5.sp, color = Color(0xFF1E293B))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf(otpDigit1, otpDigit2, otpDigit3, otpDigit4, otpDigit5, otpDigit6).forEach { digit ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF1F5F9),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SaffronPrimary),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(digit, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Resend OTP in 00:45", fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }

                    4 -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = VegGreen.copy(alpha = 0.15f),
                                modifier = Modifier.size(60.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VegGreen, modifier = Modifier.size(36.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Account Deleted Successfully", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = VegGreen)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Your account and personal data have been completely erased from CaterersWale. Thank you for using our service!",
                                fontSize = 11.5.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            when (currentStep) {
                1 -> {
                    Button(
                        onClick = { currentStep = 2 },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Continue")
                    }
                }
                2 -> {
                    Button(
                        onClick = { currentStep = 3 },
                        enabled = isWarningUnderstood,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Continue to OTP")
                    }
                }
                3 -> {
                    Button(
                        onClick = { currentStep = 4 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Verify & Delete")
                    }
                }
                4 -> {
                    Button(
                        onClick = {
                            onAccountDeleted()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Done")
                    }
                }
            }
        },
        dismissButton = {
            if (currentStep < 4) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

/**
 * Page 18 from Image 1: Change Language Dialog
 */
@Composable
fun ChangeLanguageDialog(
    currentLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedLang by remember { mutableStateOf(currentLanguage) }

    val languages = listOf(
        Language.ENGLISH to "English",
        Language.HINDI to "हिंदी (Hindi)",
        Language.HINGLISH to "Hinglish (Mix)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Language, contentDescription = null, tint = SaffronPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Change Language / भाषा बदलें", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column {
                languages.forEach { (lang, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedLang = lang }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLang == lang,
                            onClick = { selectedLang = lang },
                            colors = RadioButtonDefaults.colors(selectedColor = SaffronPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(label, fontSize = 14.sp, fontWeight = if (selectedLang == lang) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onLanguageSelected(selectedLang)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Page 16 from Image 1: Repeat Order & Order Details Dialog
 */
@Composable
fun OrderDetailsRepeatModal(
    order: OrderEntity,
    onRepeatOrder: (OrderEntity) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Order Details #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Placed on ${order.deliveryDate}", fontSize = 11.sp, color = Color.Gray)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = if (order.orderStatus == OrderStatus.DELIVERED) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Text(
                        text = "Status: ${order.orderStatus.name.replace("_", " ")}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (order.orderStatus == OrderStatus.DELIVERED) VegGreen else SaffronPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text("Caterer: ${order.catererName}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Items Ordered: ${order.itemsSummary}", fontSize = 12.sp, color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(6.dp))
                Text("Delivery Address: ${order.deliveryAddress}", fontSize = 11.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Amount", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SaffronPrimary)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Advance Paid", fontSize = 12.sp, color = VegGreen)
                    Text("₹${order.advancePaidAmount.toInt()}", fontSize = 12.sp, color = VegGreen)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Balance Due", fontSize = 12.sp, color = Color.Gray)
                    Text("₹${order.balanceAmount.toInt()}", fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onRepeatOrder(order)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Repeat This Order")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
