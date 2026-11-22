package com.example.ui.kitchen

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.models.BartanHandoverAlert
import com.example.data.models.KitchenStaffMember
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Real-time In-Place Bartan Verification Notification Dialog for Kitchen.
 * Pops up immediately when a Delivery Boy taps "Handover" in their app.
 *
 * Allows kitchen staff to physically verify and accept utensils on the spot
 * WITHOUT needing any OTP code ("Code ki zaroorat nahi padegi, wahi par jaanch kar le").
 *
 * Clearly attributes the verification audit to the active/selected kitchen staff
 * ("isase maloom padega bartan verification Kaun Kiya").
 */
@Composable
fun KitchenIncomingBartanHandoverDialog(
    alert: BartanHandoverAlert,
    activeStaff: KitchenStaffMember,
    staffList: List<KitchenStaffMember>,
    onDismiss: () -> Unit,
    onSwitchStaff: (KitchenStaffMember) -> Unit,
    onConfirmInspection: (
        returnedHandis: Int,
        returnedSpoons: Int,
        returnedBoxes: Int,
        missingRemarks: String,
        verifyingStaffName: String
    ) -> Unit
) {
    val context = LocalContext.current

    // Steppers initialized with what the delivery partner brought
    val issuedHandis = if (alert.handiCount > 0) alert.handiCount else 2
    val issuedSpoons = if (alert.spoonsCount > 0) alert.spoonsCount else 2
    val issuedBoxes = if (alert.boxesCount > 0) alert.boxesCount else 1
    val totalIssued = issuedHandis + issuedSpoons + issuedBoxes

    var retHandis by remember { mutableIntStateOf(issuedHandis) }
    var retSpoons by remember { mutableIntStateOf(issuedSpoons) }
    var retBoxes by remember { mutableIntStateOf(issuedBoxes) }

    val totalReturned = retHandis + retSpoons + retBoxes
    val isFullReturn = totalReturned >= totalIssued
    val isPartial = totalReturned in 1 until totalIssued

    var remarksText by remember { mutableStateOf("") }
    var selectedStaff by remember { mutableStateOf(activeStaff) }
    var showStaffPickerDropdown by remember { mutableStateOf(false) }

    val currentTimeStr = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(alert.timestamp))
    }

    // Gentle pulse animation for alert icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 520.dp)
                .padding(vertical = 12.dp)
                .testTag("kitchen_handover_alert_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Alert Banner
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .scale(pulseScale)
                                .background(Color(0xFFD97706), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🍲", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "📢 COUNTER NOTIFICATION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF92400E)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFF16A34A),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "NO CODE NEEDED",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                "Delivery Partner Bartan Handover Kar Raha Hai!",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF78350F)
                            )
                            Text(
                                "काउंटर पर बर्तनों की सीधी जांच करें और यहीं जमा करें",
                                fontSize = 11.sp,
                                color = Color(0xFF92400E)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rider & Order Identification Card
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("🛵 Delivery Partner (काउंटर पर उपस्थित):", fontSize = 10.5.sp, color = Color.Gray)
                            Text(
                                alert.deliveryBoyName.ifBlank { "Ramesh Sharma (Rider)" },
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "📦 Order #${alert.orderId} • $currentTimeStr",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SaffronPrimary
                            )
                        }

                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.SoupKitchen,
                                    contentDescription = "Kitchen Counter",
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Physical Item Counting / Stepper Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "बर्तनों की भौतिक जांच (Physical Verification):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = Color(0xFF1E293B)
                    )

                    // 1-Tap All Returned shortcut
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.clickable {
                            retHandis = issuedHandis
                            retSpoons = issuedSpoons
                            retBoxes = issuedBoxes
                            remarksText = "Sabhi bartan theek haalat me prapt hue."
                        }
                    ) {
                        Text(
                            "✅ 100% Wapas Mile",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Item 1: Handis
                HandoverItemCounter(
                    icon = "🍲",
                    name = "Handi / Deg (हांडी)",
                    issued = issuedHandis,
                    count = retHandis,
                    onIncrement = { retHandis++ },
                    onDecrement = { if (retHandis > 0) retHandis-- }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Item 2: Spoons / Karchhe
                HandoverItemCounter(
                    icon = "🥄",
                    name = "Large Spoons / Karchha (कड़छा)",
                    issued = issuedSpoons,
                    count = retSpoons,
                    onIncrement = { retSpoons++ },
                    onDecrement = { if (retSpoons > 0) retSpoons-- }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Item 3: Crates / Boxes
                HandoverItemCounter(
                    icon = "📦",
                    name = "Crates / Delivery Boxes (कैरेट)",
                    issued = issuedBoxes,
                    count = retBoxes,
                    onIncrement = { retBoxes++ },
                    onDecrement = { if (retBoxes > 0) retBoxes-- }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Verification Condition Summary
                Surface(
                    color = if (isFullReturn) Color(0xFFF0FDF4) else Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (isFullReturn) Color(0xFFBBF7D0) else Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (isFullReturn) "✅" else "⚠️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                if (isFullReturn) "Full Return Verified ($totalReturned / $totalIssued Pcs Prapt)"
                                else "Partial Return: $totalReturned / $totalIssued Pcs (${totalIssued - totalReturned} Missing)",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFullReturn) VegGreen else Color(0xFFB45309)
                            )
                            Text(
                                "किचन स्टॉक में तुरंत जुड़ जाएगा और डिलीवरी पार्टनर का हिसाब क्लियर होगा।",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Missing remarks (optional)
                OutlinedTextField(
                    value = remarksText,
                    onValueChange = { remarksText = it },
                    label = { Text("कंडीशन या मिसिंग नोट (Remarks - Optional)", fontSize = 11.5.sp) },
                    placeholder = { Text("उदा. हांडी साफ है, सब सही मिला", fontSize = 11.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("handover_remarks_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Staff Audit Identity Section ("bartan verification Kaun Kiya")
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("👤", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "जांचकर्ता स्टाफ (Audit Record):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF334155)
                                )
                            }

                            // Staff Switcher dropdown trigger
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, Color(0xFF94A3B8)),
                                modifier = Modifier.clickable { showStaffPickerDropdown = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("बदलें 🔁", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            "${selectedStaff.roleBadge} ${selectedStaff.name} (${selectedStaff.roleTitle})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            "सत्यापन के बाद रिकॉर्ड में दर्ज होगा: 'Verified by ${selectedStaff.name}'",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )

                        // Staff Selector Menu
                        DropdownMenu(
                            expanded = showStaffPickerDropdown,
                            onDismissRequest = { showStaffPickerDropdown = false }
                        ) {
                            staffList.forEach { staff ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text("${staff.roleBadge} ${staff.name}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text(staff.roleTitle, fontSize = 10.sp, color = Color.Gray)
                                        }
                                    },
                                    onClick = {
                                        selectedStaff = staff
                                        onSwitchStaff(staff)
                                        showStaffPickerDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Primary Action Button: Accept & Restock without code
                Button(
                    onClick = {
                        val staffTitle = "${selectedStaff.name} (${selectedStaff.roleTitle})"
                        onConfirmInspection(
                            retHandis,
                            retSpoons,
                            retBoxes,
                            remarksText,
                            staffTitle
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_handover_direct_btn")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "✅ जांच सही है - बर्तन जमा करें (No Code Needed)",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Secondary Button: Later
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Text("बाद में जांच करें (Dismiss Alert)", fontSize = 11.5.sp, color = Color(0xFF64748B))
                }
            }
        }
    }
}

@Composable
private fun HandoverItemCounter(
    icon: String,
    name: String,
    issued: Int,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFF1E293B))
                    Text("दिए गए थे: $issued Pcs", fontSize = 10.sp, color = Color.Gray)
                }
            }

            // Stepper controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onDecrement,
                    enabled = count > 0,
                    modifier = Modifier.size(30.dp)
                ) {
                    Surface(
                        color = if (count > 0) Color(0xFFE2E8F0) else Color(0xFFF1F5F9),
                        shape = CircleShape,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("-", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (count > 0) Color.Black else Color.Gray)
                        }
                    }
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, if (count >= issued) VegGreen else Color(0xFFCBD5E1)),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "$count",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = if (count >= issued) VegGreen else Color(0xFF0F172A),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }

                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier.size(30.dp)
                ) {
                    Surface(
                        color = SaffronPrimary.copy(alpha = 0.15f),
                        shape = CircleShape,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("+", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                        }
                    }
                }
            }
        }
    }
}
