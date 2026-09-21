package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.OrderEntity
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen
import com.example.util.CancellationTier
import com.example.util.TimeSlotUtils

/**
 * Customer Order Cancellation Dialog implementing the 3-Tier Policy:
 * 1. > 24 Hours: 100% Refund
 * 2. 12-24 Hours: No cash return (Option to reschedule within 7 days by paying 50% remainder, or forfeit to company fund)
 * 3. < 12 Hours & Same Day: 0% refund to customer. Kitchen receives advance minus 10% platform commission.
 */
@Composable
fun CustomerCancellationDialog(
    order: OrderEntity,
    onDismiss: () -> Unit,
    onConfirmCancel: (reason: String) -> Unit,
    onSwitchToReschedule: () -> Unit
) {
    val policy = remember(order) { TimeSlotUtils.evaluateCancellationPolicy(order) }
    var selectedReason by remember { mutableStateOf("Event postponed / cancelled") }
    var customNotes by remember { mutableStateOf("") }

    val presetReasons = listOf(
        "Event postponed / cancelled",
        "Guest count changed significantly",
        "Venue or location changed",
        "Emergency / Personal reason",
        "Other"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFFEE2E2), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(28.dp))
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Cancel Order #${order.orderId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Cancellation & Refund Policy Review",
                    fontSize = 11.5.sp,
                    color = Color(0xFF64748B)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Tier Badge Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (policy.tier) {
                        CancellationTier.MORE_THAN_24_HOURS_FULL_REFUND -> Color(0xFFDCFCE7)
                        CancellationTier.BETWEEN_12_AND_24_HOURS_RESCHEDULE_OR_FORFEIT -> Color(0xFFFEF3C7)
                        CancellationTier.LESS_THAN_12_HOURS_NO_REFUND,
                        CancellationTier.SAME_DAY_NO_REFUND -> Color(0xFFFEE2E2)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (policy.tier) {
                                    CancellationTier.MORE_THAN_24_HOURS_FULL_REFUND -> Icons.Default.CheckCircle
                                    CancellationTier.BETWEEN_12_AND_24_HOURS_RESCHEDULE_OR_FORFEIT -> Icons.Default.Warning
                                    else -> Icons.Default.Info
                                },
                                contentDescription = null,
                                tint = when (policy.tier) {
                                    CancellationTier.MORE_THAN_24_HOURS_FULL_REFUND -> Color(0xFF166534)
                                    CancellationTier.BETWEEN_12_AND_24_HOURS_RESCHEDULE_OR_FORFEIT -> Color(0xFF92400E)
                                    else -> Color(0xFF991B1B)
                                },
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (policy.tier) {
                                    CancellationTier.MORE_THAN_24_HOURS_FULL_REFUND -> "100% Refund Eligible (>24h)"
                                    CancellationTier.BETWEEN_12_AND_24_HOURS_RESCHEDULE_OR_FORFEIT -> "12–24h Window: Reschedule Available"
                                    CancellationTier.SAME_DAY_NO_REFUND -> "Same Day Delivery: 0% Return"
                                    CancellationTier.LESS_THAN_12_HOURS_NO_REFUND -> "Under 12h: 0% Return"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = when (policy.tier) {
                                    CancellationTier.MORE_THAN_24_HOURS_FULL_REFUND -> Color(0xFF166534)
                                    CancellationTier.BETWEEN_12_AND_24_HOURS_RESCHEDULE_OR_FORFEIT -> Color(0xFF92400E)
                                    else -> Color(0xFF991B1B)
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = policy.policyExplanation,
                            fontSize = 11.sp,
                            color = Color(0xFF334155),
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Breakdown of advance & refund
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Order Amount:", fontSize = 11.5.sp, color = Color(0xFF64748B))
                            Text("₹${order.totalAmount.toInt()}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Advance Paid:", fontSize = 11.5.sp, color = Color(0xFF64748B))
                            Text("₹${order.advancePaidAmount.toInt()}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFE2E8F0))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Customer Refund:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text(
                                "₹${policy.customerRefundAmount.toInt()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = if (policy.customerRefundAmount > 0) VegGreen else Color(0xFFDC2626)
                            )
                        }
                        if (policy.kitchenSettlementAmount > 0) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Kitchen Raw Material Credit:", fontSize = 10.5.sp, color = Color(0xFF475569))
                                Text("₹${policy.kitchenSettlementAmount.toInt()}", fontSize = 10.5.sp, color = Color(0xFF475569), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // If in 12-24h window, provide high-visibility Reschedule button
                if (policy.tier == CancellationTier.BETWEEN_12_AND_24_HOURS_RESCHEDULE_OR_FORFEIT) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEFF6FF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "💡 Recommendation: Don't lose your ₹${order.advancePaidAmount.toInt()} advance!",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8)
                            )
                            Text(
                                text = "Reschedule to any date in next 7 days. Remaining ₹${order.balanceAmount.toInt()} balance will be payable to lock the event.",
                                fontSize = 10.5.sp,
                                color = Color(0xFF1E40AF)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    onDismiss()
                                    onSwitchToReschedule()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reschedule_from_cancel_btn")
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reschedule Event (Next 7 Days)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Select Cancellation Reason
                Text("Select Cancellation Reason:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(4.dp))

                presetReasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedReason == reason),
                            onClick = { selectedReason = reason },
                            colors = RadioButtonDefaults.colors(selectedColor = SaffronPrimary),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(reason, fontSize = 11.5.sp, color = Color(0xFF334155))
                    }
                }

                if (selectedReason == "Other") {
                    OutlinedTextField(
                        value = customNotes,
                        onValueChange = { customNotes = it },
                        placeholder = { Text("Please describe the reason...", fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        maxLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalReason = if (selectedReason == "Other" && customNotes.isNotBlank()) customNotes else selectedReason
                    onConfirmCancel(finalReason)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_cancel_order_btn")
            ) {
                Text(
                    text = if (policy.customerRefundAmount > 0) "Confirm & Refund (₹${policy.customerRefundAmount.toInt()})" else "Confirm Cancellation",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Keep Order", fontSize = 11.5.sp)
            }
        }
    )
}

/**
 * Customer Reschedule Dialog:
 * Customer can reschedule within 7 days.
 * Remaining 50% balance must be paid to confirm and lock the booking.
 * Once rescheduled, the order is strictly non-cancellable!
 */
@Composable
fun CustomerRescheduleDialog(
    order: OrderEntity,
    onDismiss: () -> Unit,
    onConfirmReschedule: (newDate: String, newSlot: String) -> Unit
) {
    val nextDays = remember { TimeSlotUtils.getNext7DaysForReschedule() }
    var selectedDate by remember { mutableStateOf(nextDays.firstOrNull()?.first ?: order.deliveryDate) }

    val timeSlots = listOf(
        "07:00 AM - 10:00 AM (Breakfast)",
        "12:00 PM - 03:00 PM (Lunch)",
        "04:00 PM - 07:00 PM (High Tea)",
        "07:30 PM - 10:30 PM (Dinner)"
    )
    var selectedSlot by remember { mutableStateOf(timeSlots[1]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFEFF6FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(28.dp))
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Reschedule Event 🗓️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Select New Date within Next 7 Days",
                    fontSize = 11.5.sp,
                    color = Color(0xFF64748B)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Rule Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "100% Full Payment Lock Required",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = "To confirm reschedule, the remaining balance of ₹${order.balanceAmount.toInt()} must be paid. Rescheduled orders become non-cancellable.",
                                fontSize = 10.5.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Select Date Chips (Next 7 days)
                Text("Select Delivery Date:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(nextDays) { (rawDate, displayLabel) ->
                        val isSelected = (selectedDate == rawDate)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) SaffronPrimary else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) SaffronPrimary else Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier
                                .clickable { selectedDate = rawDate }
                                .testTag("reschedule_date_$rawDate")
                        ) {
                            Text(
                                text = displayLabel,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Select Time Slot
                Text("Select Time Slot:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(4.dp))

                timeSlots.forEach { slot ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSlot = slot }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedSlot == slot),
                            onClick = { selectedSlot = slot },
                            colors = RadioButtonDefaults.colors(selectedColor = SaffronPrimary),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(slot, fontSize = 11.sp, color = Color(0xFF334155))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Payment Breakdown
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Previously Paid (50% Advance):", fontSize = 10.5.sp, color = Color(0xFF64748B))
                            Text("₹${order.advancePaidAmount.toInt()}", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Balance Due Now (50%):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Text("₹${order.balanceAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = SaffronPrimary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmReschedule(selectedDate, selectedSlot)
                },
                colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("pay_and_confirm_reschedule_btn")
            ) {
                Text(
                    text = "Pay ₹${order.balanceAmount.toInt()} & Reschedule ✅",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                )
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Cancel", fontSize = 11.5.sp)
            }
        }
    )
}
