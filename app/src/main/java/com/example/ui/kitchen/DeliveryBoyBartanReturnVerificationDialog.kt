package com.example.ui.kitchen

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dedicated Physical Verification Modal Dialog for Kitchen Manager / Halwai
 * to physically count, inspect, and verify utensils returned by a Delivery Partner.
 *
 * Supports:
 * 1. Visualizing issued containers (Handi, Spoons, Crates/Boxes)
 * 2. Physical item-by-item Plus/Minus counters
 * 3. 1-Click "All Returned 100%" quick-fill
 * 4. Automatic discrepancy detection (Full Return vs Partial Return vs Missing/Damaged)
 * 5. Remarks note for missing/damaged items with rider accountability
 * 6. Kitchen staff signature and restock authorization
 */
@Composable
fun DeliveryBoyBartanReturnVerificationDialog(
    row: ContainerCashSummaryRow,
    currentStaffName: String = "Mohd Salim (बर्तन व स्टोर इनचार्ज)",
    onDismiss: () -> Unit,
    onConfirmVerification: (
        recordId: String,
        orderId: String,
        deliveryBoyId: String,
        returnedHandis: Int,
        returnedSpoons: Int,
        returnedBoxes: Int,
        missingRemarks: String,
        staffName: String
    ) -> Unit
) {
    val context = LocalContext.current

    // Issued quantities at delivery time
    val issuedHandis = if (row.handiCount > 0) row.handiCount else 2
    val issuedSpoons = if (row.spoonsCount > 0) row.spoonsCount else 2
    val issuedBoxes = if (row.boxesCount > 0) row.boxesCount else 1
    val totalIssued = (issuedHandis + issuedSpoons + issuedBoxes).coerceAtLeast(1)

    // Supervisor physical inspection counters (Default set to full issued for fast 1-tap verification)
    var retHandis by remember { mutableIntStateOf(issuedHandis) }
    var retSpoons by remember { mutableIntStateOf(issuedSpoons) }
    var retBoxes by remember { mutableIntStateOf(issuedBoxes) }

    val totalReceived = retHandis + retSpoons + retBoxes
    val isFullReturn = totalReceived >= totalIssued
    val isPartialReturn = totalReceived in 1 until totalIssued
    val isZeroReturn = totalReceived == 0
    val missingCount = (totalIssued - totalReceived).coerceAtLeast(0)

    var missingRemarks by remember { mutableStateOf("") }
    var staffName by remember { mutableStateOf(currentStaffName) }

    val currentTimeStr = remember {
        SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.getDefault()).format(Date())
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 520.dp)
                .padding(vertical = 16.dp)
                .testTag("delivery_boy_return_verification_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = SaffronPrimary.copy(alpha = 0.12f),
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🍲", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "बर्तन वापसी वेरिफिकेशन",
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Rider Return Physical Verification",
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Order & Delivery Boy Details Card
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "#${row.orderId}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = Color(0xFF2563EB),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    row.customerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF1E293B)
                                )
                            }

                            Text(
                                text = row.eventDate.ifBlank { "Event Date" },
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Rider info & contact
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.DeliveryDining,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "🛵 राइडर: ${row.deliveryBoyName.ifBlank { "Delivery Partner" }}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B)
                                )
                            }

                            if (row.deliveryBoyMobile.isNotBlank()) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${row.deliveryBoyMobile}"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = VegGreen, modifier = Modifier.size(15.dp))
                                    }

                                    IconButton(
                                        onClick = {
                                            val cleanMobile = row.deliveryBoyMobile.replace("+91", "").replace(" ", "").replace("-", "").trim()
                                            val msg = "Namaste ${row.deliveryBoyName}, Order #${row.orderId} ke bartan physical verification chal raha hai."
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/91$cleanMobile?text=${Uri.encode(msg)}"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color(0xFF25D366), modifier = Modifier.size(15.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Issued at Delivery
                Surface(
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "📦 राइडर को दिए गए बर्तन (Issued Utensils):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF92400E)
                            )
                            Surface(
                                color = AmberSecondary,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "कुल $totalIssued बर्तन",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            UtensilIssuedBadge(icon = "🍲", name = "Handi/Deg", count = issuedHandis, modifier = Modifier.weight(1f))
                            UtensilIssuedBadge(icon = "🥄", name = "Chammach", count = issuedSpoons, modifier = Modifier.weight(1f))
                            UtensilIssuedBadge(icon = "📦", name = "Box/Crate", count = issuedBoxes, modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Physical Inspection Counters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🔍 फिज़िकल गिनती (Physical Count):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = Color(0xFF0F172A)
                    )

                    // Quick All Full Button
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable {
                            retHandis = issuedHandis
                            retSpoons = issuedSpoons
                            retBoxes = issuedBoxes
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚡ सब पूरे मिले", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Item 1: Handi
                ReturnCounterRow(
                    icon = "🍲",
                    title = "Metal Handi / Degs",
                    issued = issuedHandis,
                    count = retHandis,
                    onIncrement = { retHandis++ },
                    onDecrement = { if (retHandis > 0) retHandis-- }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Item 2: Spoons
                ReturnCounterRow(
                    icon = "🥄",
                    title = "Serving Spoons / Chammach",
                    issued = issuedSpoons,
                    count = retSpoons,
                    onIncrement = { retSpoons++ },
                    onDecrement = { if (retSpoons > 0) retSpoons-- }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Item 3: Boxes
                ReturnCounterRow(
                    icon = "📦",
                    title = "Hot Crates / Dabba",
                    issued = issuedBoxes,
                    count = retBoxes,
                    onIncrement = { retBoxes++ },
                    onDecrement = { if (retBoxes > 0) retBoxes-- }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: Live Condition / Result Banner
                Surface(
                    color = when {
                        isFullReturn -> Color(0xFFF0FDF4)
                        isPartialReturn -> Color(0xFFFFFBEB)
                        else -> Color(0xFFFEF2F2)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        when {
                            isFullReturn -> Color(0xFFBBF7D0)
                            isPartialReturn -> Color(0xFFFDE68A)
                            else -> Color(0xFFFECACA)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when {
                                    isFullReturn -> "✅"
                                    isPartialReturn -> "⚠️"
                                    else -> "❌"
                                },
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    isFullReturn -> "पूर्ण वापसी (100% Full Return Verified)"
                                    isPartialReturn -> "अधूरा रिटर्न (Partial Return: $missingCount बर्तन बाकी)"
                                    else -> "कोई बर्तन प्राप्त नहीं हुआ (0 Received / Lost)"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = when {
                                    isFullReturn -> VegGreen
                                    isPartialReturn -> Color(0xFFB45309)
                                    else -> Color(0xFFDC2626)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = when {
                                isFullReturn ->
                                    "कुल $totalReceived बर्तन किचन स्टॉक में रीस्टॉक होंगे और राइडर '${row.deliveryBoyName}' का खाता क्लियर हो जाएगा।"
                                isPartialReturn ->
                                    "प्राप्त $totalReceived बर्तन किचन में रीस्टॉक होंगे। शेष $missingCount बर्तन राइडर '${row.deliveryBoyName}' के खाते में पेंडिंग रहेंगे।"
                                else ->
                                    "कोई बर्तन स्टॉक में नहीं जुड़ेगा। राइडर पर पूरी पेंडेंसी बनी रहेगी।"
                            },
                            fontSize = 11.sp,
                            color = when {
                                isFullReturn -> Color(0xFF166534)
                                isPartialReturn -> Color(0xFF78350F)
                                else -> Color(0xFF991B1B)
                            },
                            lineHeight = 15.sp
                        )
                    }
                }

                // Discrepancy Remarks Input (If not full return or supervisor wants to note)
                if (!isFullReturn || missingRemarks.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "📝 कमी / डैमेज का कारण (Remarks):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Quick Reason Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "1 बर्तन ग्राहक के पास छूटा",
                            "बर्तन टूटा / डैमेज",
                            "कल सुबह लाएगा"
                        ).forEach { reason ->
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { missingRemarks = reason }
                            ) {
                                Text(
                                    reason,
                                    fontSize = 9.5.sp,
                                    color = Color(0xFF475569),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = missingRemarks,
                        onValueChange = { missingRemarks = it },
                        placeholder = { Text("टिप्पणी दर्ज करें...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Staff Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = staffName,
                        onValueChange = { staffName = it },
                        label = { Text("जांचकर्ता (Staff Name)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = currentTimeStr,
                            fontSize = 10.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Confirmation Button
                Button(
                    onClick = {
                        val recordId = row.containerRecordId ?: row.orderId
                        val boyId = row.deliveryBoyId
                        onConfirmVerification(
                            recordId,
                            row.orderId,
                            boyId,
                            retHandis,
                            retSpoons,
                            retBoxes,
                            missingRemarks,
                            staffName
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_bartan_verification_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFullReturn) VegGreen else Color(0xFFD97706)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        if (isFullReturn) Icons.Default.CheckCircle else Icons.Default.Verified,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFullReturn) "सत्यापित करें और किचन में जमा करें 🚀"
                        else "अधूरा रिटर्न रिकॉर्ड करें ($totalReceived/$totalIssued बर्तन) ⚠️",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel (रद्द करें)", fontSize = 12.sp, color = Color(0xFF64748B))
                }
            }
        }
    }
}

@Composable
private fun UtensilIssuedBadge(
    icon: String,
    name: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 16.sp)
            Text(name, fontSize = 10.sp, color = Color(0xFF78350F), fontWeight = FontWeight.Medium)
            Text("$count Pcs", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
        }
    }
}

@Composable
private fun ReturnCounterRow(
    icon: String,
    title: String,
    issued: Int,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(10.dp),
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        "दिए गए थे: $issued Pcs",
                        fontSize = 10.5.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Stepper controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onDecrement,
                    enabled = count > 0,
                    modifier = Modifier.size(34.dp)
                ) {
                    Surface(
                        color = if (count > 0) Color(0xFFE2E8F0) else Color(0xFFF1F5F9),
                        shape = CircleShape,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (count > 0) Color.Black else Color.Gray)
                        }
                    }
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (count >= issued) VegGreen else Color(0xFFCBD5E1)),
                    modifier = Modifier.padding(horizontal = 6.dp)
                ) {
                    Text(
                        text = "$count",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = if (count >= issued) VegGreen else Color(0xFF0F172A),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier.size(34.dp)
                ) {
                    Surface(
                        color = SaffronPrimary.copy(alpha = 0.15f),
                        shape = CircleShape,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                        }
                    }
                }
            }
        }
    }
}
