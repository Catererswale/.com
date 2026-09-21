package com.example.ui.kitchen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BartanRecordEntity
import com.example.data.models.DeliveryBoyEntity
import com.example.data.models.KitchenUtensilEntity
import com.example.data.models.OrderEntity
import com.example.data.models.PaymentMethod
import com.example.data.repository.CaterersViewModel
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen
import com.example.util.ContainerOverdueHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data model representing a row in the summary table of pending containers and cash
 */
data class ContainerCashSummaryRow(
    val orderId: String,
    val customerName: String,
    val customerMobile: String,
    val deliveryAddress: String,
    val eventDate: String,
    val deliveryBoyName: String,
    val deliveryBoyMobile: String,
    val deliveryBoyId: String,
    // Re-assigned Pickup Boy tracking
    val pickupBoyName: String = "",
    val pickupBoyMobile: String = "",
    val pickupBoyId: String = "",
    // Utensil Quantities for Stock Subtraction
    val handiCount: Int = 2,
    val spoonsCount: Int = 2,
    val boxesCount: Int = 1,
    // Container Information
    val hasContainer: Boolean,
    val containerRecordId: String?,
    val containerDescription: String,
    val isContainerCollected: Boolean,
    val containerStatus: String,
    // Overdue Tracking
    val isOverdue: Boolean = false,
    val daysOverdue: Int = 0,
    val alertSent: Boolean = false,
    val lastAlertDate: String = "",
    // Cash Information
    val hasCash: Boolean,
    val cashAmount: Double,
    val isCashSubmitted: Boolean,
    val rawOrder: OrderEntity?
)

/**
 * Grouped liability summary for a delivery boy
 */
data class DeliveryBoyGroupSummary(
    val boyId: String,
    val boyName: String,
    val boyMobile: String,
    val totalOrders: Int,
    val pendingHandis: Int,
    val pendingCash: Double,
    val overdueCount: Int,
    val orders: List<ContainerCashSummaryRow>
)

/**
 * Dedicated Dashboard Screen for the Kitchen role that displays a summary table
 * of pending containers and cash, allowing the kitchen to manually mark items as
 * 'returned' or 'received' to update the status.
 */
@Composable
fun KitchenPendingContainersCashDashboardScreen(
    viewModel: CaterersViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bartanRecords by viewModel.bartanRecordsList.collectAsState()
    val allOrders by viewModel.ordersList.collectAsState()
    val allDeliveryBoys by viewModel.deliveryBoysList.collectAsState()
    val kitchenUtensils by viewModel.kitchenUtensilsList.collectAsState()

    val myDeliveryBoys = remember(allDeliveryBoys) {
        allDeliveryBoys.filter { it.kitchenId == "caterer_1" }
    }

    // View mode: "TABLE" (tabular horizontal grid) or "CARDS" (compact card list)
    var isTableView by remember { mutableStateOf(true) }
    // Group By Mode: "ORDER" (Order-wise view) or "DELIVERY_BOY" (Grouped by Valet)
    var groupByMode by remember { mutableStateOf("ORDER") }

    // Filter selection: ALL, PENDING_ALL, PENDING_CONTAINERS, PENDING_CASH, BOTH_PENDING, SETTLED
    var filterType by remember { mutableStateOf("PENDING_ALL") }
    var searchQuery by remember { mutableStateOf("") }

    // Utensil Stock & Re-assign States
    var showManageUtensilsDialog by remember { mutableStateOf(false) }
    var rowToReassignPickup by remember { mutableStateOf<ContainerCashSummaryRow?>(null) }

    // Confirmation dialog states for manual actions
    var containerToMarkReturned by remember { mutableStateOf<ContainerCashSummaryRow?>(null) }
    var cashToMarkReceived by remember { mutableStateOf<ContainerCashSummaryRow?>(null) }
    var reminderTargetRow by remember { mutableStateOf<ContainerCashSummaryRow?>(null) }

    // Click-to-view detailed breakdown for top KPI cards & rows
    // "PENDING_CONTAINERS", "PENDING_CASH", "SETTLED"
    var selectedMetricBreakdown by remember { mutableStateOf<String?>(null) }
    var selectedOrderDetailsRow by remember { mutableStateOf<ContainerCashSummaryRow?>(null) }

    // Build unified summary rows
    val allRows = remember(allOrders, bartanRecords) {
        val result = mutableListOf<ContainerCashSummaryRow>()
        val processedOrderIds = mutableSetOf<String>()

        for (order in allOrders) {
            val matchingBartan = bartanRecords.find { it.orderId == order.orderId }
            val hasContainer = matchingBartan != null || order.isBartanPending || order.bartanDescription.isNotBlank()
            val isContainerCollected = matchingBartan?.isCollected ?: order.isBartanReturned
            val containerDesc = matchingBartan?.itemsDescription?.ifBlank { null }
                ?: order.bartanDescription.ifBlank { "Authentic Steel Handi" }

            val daysLate = ContainerOverdueHelper.calculateDaysOverdue(order.deliveryDate)
            val isOverdue = hasContainer && !isContainerCollected && ContainerOverdueHelper.isOverdue(order.deliveryDate, isContainerCollected)

            val cashAmt = if (order.cashCollectedByDeliveryBoy > 0) order.cashCollectedByDeliveryBoy else order.balanceAmount
            val hasCash = cashAmt > 0 || order.paymentMethod == PaymentMethod.CASH_ON_DELIVERY

            if (hasContainer || hasCash) {
                result.add(
                    ContainerCashSummaryRow(
                        orderId = order.orderId,
                        customerName = order.customerName,
                        customerMobile = order.customerMobile,
                        deliveryAddress = order.deliveryAddress,
                        eventDate = order.deliveryDate,
                        deliveryBoyName = order.deliveryBoyName ?: matchingBartan?.deliveryBoyName ?: "Unassigned",
                        deliveryBoyMobile = order.deliveryBoyMobile ?: matchingBartan?.deliveryBoyMobile ?: "",
                        deliveryBoyId = order.deliveryBoyId ?: matchingBartan?.deliveryBoyId ?: "",
                        pickupBoyName = matchingBartan?.pickupBoyName ?: "",
                        pickupBoyMobile = matchingBartan?.pickupBoyMobile ?: "",
                        pickupBoyId = matchingBartan?.pickupBoyId ?: "",
                        handiCount = matchingBartan?.handiCount ?: 2,
                        spoonsCount = matchingBartan?.spoonsCount ?: 2,
                        boxesCount = matchingBartan?.boxesCount ?: 1,
                        hasContainer = hasContainer,
                        containerRecordId = matchingBartan?.id,
                        containerDescription = containerDesc,
                        isContainerCollected = isContainerCollected,
                        containerStatus = matchingBartan?.returnStatus ?: if (isContainerCollected) "COLLECTED" else "PENDING",
                        isOverdue = isOverdue,
                        daysOverdue = daysLate,
                        alertSent = matchingBartan?.morningAlertSent ?: false,
                        lastAlertDate = matchingBartan?.lastMorningAlertDate ?: "",
                        hasCash = hasCash,
                        cashAmount = cashAmt,
                        isCashSubmitted = order.isCashSubmittedToKitchen,
                        rawOrder = order
                    )
                )
                processedOrderIds.add(order.orderId)
            }
        }

        // Include any standalone bartan records that don't exist in orders
        for (record in bartanRecords) {
            if (!processedOrderIds.contains(record.orderId)) {
                val daysLate = ContainerOverdueHelper.calculateDaysOverdue(record.deliveryDate)
                val isOverdue = !record.isCollected && ContainerOverdueHelper.isOverdue(record.deliveryDate, record.isCollected)

                result.add(
                    ContainerCashSummaryRow(
                        orderId = record.orderId,
                        customerName = record.customerName,
                        customerMobile = record.customerMobile,
                        deliveryAddress = record.customerAddress,
                        eventDate = record.deliveryDate,
                        deliveryBoyName = record.deliveryBoyName.ifBlank { "Unassigned" },
                        deliveryBoyMobile = record.deliveryBoyMobile,
                        deliveryBoyId = record.deliveryBoyId,
                        pickupBoyName = record.pickupBoyName,
                        pickupBoyMobile = record.pickupBoyMobile,
                        pickupBoyId = record.pickupBoyId,
                        handiCount = record.handiCount,
                        spoonsCount = record.spoonsCount,
                        boxesCount = record.boxesCount,
                        hasContainer = true,
                        containerRecordId = record.id,
                        containerDescription = record.itemsDescription,
                        isContainerCollected = record.isCollected,
                        containerStatus = record.returnStatus,
                        isOverdue = isOverdue,
                        daysOverdue = daysLate,
                        alertSent = record.morningAlertSent,
                        lastAlertDate = record.lastMorningAlertDate,
                        hasCash = false,
                        cashAmount = 0.0,
                        isCashSubmitted = true,
                        rawOrder = null
                    )
                )
            }
        }
        result
    }

    // Filtered rows based on filter chips and search query
    val filteredRows = remember(allRows, filterType, searchQuery) {
        allRows.filter { row ->
            val matchesFilter = when (filterType) {
                "PENDING_ALL" -> (row.hasContainer && !row.isContainerCollected) || (row.hasCash && !row.isCashSubmitted)
                "OVERDUE" -> row.isOverdue
                "PENDING_CONTAINERS" -> row.hasContainer && !row.isContainerCollected
                "PENDING_CASH" -> row.hasCash && !row.isCashSubmitted
                "BOTH_PENDING" -> (row.hasContainer && !row.isContainerCollected) && (row.hasCash && !row.isCashSubmitted)
                "SETTLED" -> (!row.hasContainer || row.isContainerCollected) && (!row.hasCash || row.isCashSubmitted)
                else -> true
            }

            val matchesSearch = if (searchQuery.isBlank()) true else {
                row.orderId.contains(searchQuery, ignoreCase = true) ||
                    row.customerName.contains(searchQuery, ignoreCase = true) ||
                    row.customerMobile.contains(searchQuery, ignoreCase = true) ||
                    row.deliveryBoyName.contains(searchQuery, ignoreCase = true) ||
                    row.deliveryAddress.contains(searchQuery, ignoreCase = true)
            }

            matchesFilter && matchesSearch
        }
    }

    // Delivery Boy Grouped Summaries (Aggregated Pending Handi & 50% Cash by Valet)
    val deliveryBoyGroups = remember(filteredRows) {
        filteredRows.groupBy { it.deliveryBoyName.ifBlank { "Unassigned" } }
            .map { (boyName, rows) ->
                val boyId = rows.firstOrNull { it.deliveryBoyId.isNotBlank() }?.deliveryBoyId ?: ""
                val boyMobile = rows.firstOrNull { it.deliveryBoyMobile.isNotBlank() }?.deliveryBoyMobile ?: ""
                val pendingHandis = rows.filter { it.hasContainer && !it.isContainerCollected }.sumOf { it.handiCount }
                val pendingCash = rows.filter { it.hasCash && !it.isCashSubmitted }.sumOf { it.cashAmount }
                val overdue = rows.count { it.isOverdue }
                DeliveryBoyGroupSummary(
                    boyId = boyId,
                    boyName = boyName,
                    boyMobile = boyMobile,
                    totalOrders = rows.size,
                    pendingHandis = pendingHandis,
                    pendingCash = pendingCash,
                    overdueCount = overdue,
                    orders = rows
                )
            }.sortedByDescending { it.pendingCash + (it.pendingHandis * 500) }
    }

    // Live KPI Counters
    val totalPendingContainers = remember(allRows) { allRows.count { it.hasContainer && !it.isContainerCollected } }
    val totalReturnedContainers = remember(allRows) { allRows.count { it.hasContainer && it.isContainerCollected } }
    val totalOverdueContainers = remember(allRows) { allRows.count { it.isOverdue } }
    val totalPendingCashAmount = remember(allRows) { allRows.filter { it.hasCash && !it.isCashSubmitted }.sumOf { it.cashAmount } }
    val totalReceivedCashAmount = remember(allRows) { allRows.filter { it.hasCash && it.isCashSubmitted }.sumOf { it.cashAmount } }
    val boysWithPendingLiabilities = remember(allRows) {
        allRows.filter { (it.hasContainer && !it.isContainerCollected) || (it.hasCash && !it.isCashSubmitted) }
            .map { it.deliveryBoyName }
            .distinct()
            .filter { it.isNotBlank() && it != "Unassigned" }
            .size
    }

    // Modal Confirmation Dialog: Manually Mark Container as Returned
    if (containerToMarkReturned != null) {
        val row = containerToMarkReturned!!
        val currentDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
        AlertDialog(
            onDismissRequest = { containerToMarkReturned = null },
            icon = { Icon(Icons.Default.SoupKitchen, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(32.dp)) },
            title = { Text("Mark Container Returned?", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column {
                    Text(
                        "Kya aap confirm karte hain ki Order #${row.orderId} ka container (${row.containerDescription}) Kitchen me wapas jama ho gaya hai?",
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("👤 Customer: ${row.customerName} (${row.customerMobile})", fontSize = 11.5.sp, color = Color(0xFF92400E))
                            Text("🛵 Delivery Boy: ${row.deliveryBoyName}", fontSize = 11.5.sp, color = Color(0xFF92400E))
                            Text("🍲 Utensils: ${row.containerDescription}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val recordId = row.containerRecordId ?: row.orderId
                        viewModel.markBartanCollected(recordId, currentDate)
                        containerToMarkReturned = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_mark_returned_btn")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Haan, Mark Returned", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { containerToMarkReturned = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel", fontSize = 12.sp)
                }
            }
        )
    }

    // Modal Confirmation Dialog: Manually Mark Cash as Received
    if (cashToMarkReceived != null) {
        val row = cashToMarkReceived!!
        AlertDialog(
            onDismissRequest = { cashToMarkReceived = null },
            icon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = VegGreen, modifier = Modifier.size(32.dp)) },
            title = { Text("Confirm Cash Received?", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column {
                    Text(
                        "Kya aapko Delivery Boy '${row.deliveryBoyName}' se Order #${row.orderId} ka ₹${row.cashAmount.toInt()} Cash handover prapt ho gaya hai?",
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("💵 Amount: ₹${row.cashAmount.toInt()} (50% COD)", fontWeight = FontWeight.Black, fontSize = 14.sp, color = VegGreen)
                            Text("👤 Customer: ${row.customerName} (${row.customerMobile})", fontSize = 11.5.sp, color = Color(0xFF166534))
                            Text("🛵 Handed over by: ${row.deliveryBoyName}", fontSize = 11.5.sp, color = Color(0xFF166534))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Kitchen confirm karne ke baad status turant 'Fully Settled' update ho jayega.",
                                fontSize = 10.5.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.markCashReceivedByKitchen(row.orderId)
                        cashToMarkReceived = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_mark_received_btn")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Confirm & Mark Received", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { cashToMarkReceived = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel", fontSize = 12.sp)
                }
            }
        )
    }

    // Modal Confirmation Dialog: Send Manual Overdue Reminder to Specific Delivery Boy
    if (reminderTargetRow != null) {
        val row = reminderTargetRow!!
        val effectiveBoy = remember(row, myDeliveryBoys) {
            myDeliveryBoys.find { it.id == row.deliveryBoyId || it.name.equals(row.deliveryBoyName, ignoreCase = true) }
                ?: myDeliveryBoys.firstOrNull()
        }
        val boyName = row.deliveryBoyName.takeIf { it.isNotBlank() && it != "Unassigned" }
            ?: effectiveBoy?.name ?: "Delivery Partner"
        val boyMobile = row.deliveryBoyMobile.takeIf { it.isNotBlank() }
            ?: effectiveBoy?.mobile ?: ""
        val boyId = row.deliveryBoyId.takeIf { it.isNotBlank() }
            ?: effectiveBoy?.id ?: "boy_1"

        AlertDialog(
            onDismissRequest = { reminderTargetRow = null },
            icon = {
                Surface(
                    color = Color(0xFFFEE2E2),
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Send Overdue Container Reminder",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.5.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "🚨 ${row.daysOverdue} DAYS OVERDUE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            },
            text = {
                Column {
                    Text(
                        "Send a high-priority push reminder notification to delivery partner '$boyName' to collect and return overdue catering containers.",
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🛵 Delivery Boy: ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF78350F))
                                Text(
                                    "$boyName ${if (boyMobile.isNotBlank()) "($boyMobile)" else ""}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF78350F)
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🍲 Containers: ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF78350F))
                                Text(row.containerDescription, fontSize = 11.sp, color = Color(0xFF92400E))
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("👤 Customer: ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF78350F))
                                Text("${row.customerName} (${row.customerMobile})", fontSize = 11.sp, color = Color(0xFF78350F))
                            }
                            if (row.deliveryAddress.isNotBlank()) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text("📍 Address: ${row.deliveryAddress}", fontSize = 10.5.sp, color = Color(0xFF78350F), maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text("📅 Delivered On: ${row.eventDate}", fontSize = 10.5.sp, color = Color(0xFF92400E))
                        }
                    }

                    if (boyMobile.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val msg = "Urgent: Order #${row.orderId} catering utensils (${row.containerDescription}) at ${row.customerName} (${row.deliveryAddress}) are overdue by ${row.daysOverdue} days. Please collect and return to kitchen immediately."
                                    val uri = Uri.parse("https://api.whatsapp.com/send?phone=91$boyMobile&text=${Uri.encode(msg)}")
                                    val i = Intent(Intent.ACTION_VIEW, uri)
                                    context.startActivity(i)
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, tint = VegGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WhatsApp", fontSize = 10.5.sp, color = VegGreen)
                            }

                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$boyMobile"))
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call Boy", fontSize = 10.5.sp, color = SaffronPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendManualContainerReminderToDeliveryBoy(
                            bartanRecordId = row.containerRecordId,
                            orderId = row.orderId,
                            deliveryBoyId = boyId,
                            deliveryBoyName = boyName,
                            deliveryBoyMobile = boyMobile,
                            customerName = row.customerName,
                            customerMobile = row.customerMobile,
                            customerAddress = row.deliveryAddress,
                            containerDescription = row.containerDescription,
                            daysOverdue = row.daysOverdue
                        )
                        reminderTargetRow = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_send_overdue_reminder_btn")
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Send Notification", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { reminderTargetRow = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel", fontSize = 11.5.sp)
                }
            }
        )
    }

    // Full Details Breakdown Modal (Triggered by Clicking Pending Handi, Pending Cash, Settled in Kitchen)
    if (selectedMetricBreakdown != null) {
        val breakdownType = selectedMetricBreakdown!!
        val matchingMetricRows = remember(allRows, breakdownType) {
            when (breakdownType) {
                "PENDING_CONTAINERS" -> allRows.filter { it.hasContainer && !it.isContainerCollected }
                "PENDING_CASH" -> allRows.filter { it.hasCash && !it.isCashSubmitted }
                "SETTLED" -> allRows.filter { (!it.hasContainer || it.isContainerCollected) && (!it.hasCash || it.isCashSubmitted) }
                else -> allRows
            }
        }

        KitchenMetricDetailsBreakdownDialog(
            metricType = breakdownType,
            allMatchingRows = matchingMetricRows,
            allDeliveryBoys = allDeliveryBoys,
            context = context,
            onDismiss = { selectedMetricBreakdown = null },
            onMarkReturned = { row -> containerToMarkReturned = row },
            onMarkReceived = { row -> cashToMarkReceived = row },
            onSendReminder = { row -> reminderTargetRow = row }
        )
    }

    // Single Order Details Modal (Triggered when tapping a specific order in table/card)
    if (selectedOrderDetailsRow != null) {
        KitchenSingleOrderDetailsDialog(
            row = selectedOrderDetailsRow!!,
            allDeliveryBoys = allDeliveryBoys,
            context = context,
            onDismiss = { selectedOrderDetailsRow = null },
            onMarkReturned = { row -> containerToMarkReturned = row },
            onMarkReceived = { row -> cashToMarkReceived = row },
            onSendReminder = { row -> reminderTargetRow = row }
        )
    }

    // Re-assign Bartan Pickup Boy Dialog
    if (rowToReassignPickup != null) {
        KitchenReassignPickupBoyDialog(
            row = rowToReassignPickup!!,
            allDeliveryBoys = allDeliveryBoys,
            context = context,
            onDismiss = { rowToReassignPickup = null },
            onConfirmReassign = { newBoy ->
                viewModel.reassignBartanPickupBoy(
                    recordId = rowToReassignPickup!!.containerRecordId ?: rowToReassignPickup!!.orderId,
                    newBoy = newBoy,
                    customerName = rowToReassignPickup!!.customerName,
                    handiCount = rowToReassignPickup!!.handiCount
                )
                rowToReassignPickup = null
            }
        )
    }

    // Utensils Stock Master Management Dialog
    if (showManageUtensilsDialog) {
        KitchenManageUtensilsInventoryDialog(
            utensils = kitchenUtensils,
            allRows = allRows,
            onDismiss = { showManageUtensilsDialog = false },
            onUpdateUtensil = { updatedUtensil ->
                viewModel.saveOrUpdateUtensil(updatedUtensil)
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // 1. Executive Title & Actions Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = SaffronPrimary.copy(alpha = 0.12f),
                                shape = CircleShape,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🍲", fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "Pending Containers & Cash Dashboard",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    "Kitchen Manager Verification & Status Update Table",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        // Quick Share / Morning Alert Button
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = { viewModel.trigger930AmMorningAlert(true) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "9:30 AM Alert", tint = AmberSecondary)
                            }

                            IconButton(
                                onClick = {
                                    val summaryText = "📊 *A1 Huma Kitchen - Pending Containers & Cash Summary*\n" +
                                        "🍲 Pending Containers: $totalPendingContainers Handi\n" +
                                        "💵 Pending 50% Cash: ₹${totalPendingCashAmount.toInt()}\n" +
                                        "🛵 Active Boys with Balance: $boysWithPendingLiabilities Boys\n" +
                                        "✅ Returned Handi: $totalReturnedContainers | Cash Received: ₹${totalReceivedCashAmount.toInt()}"
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        putExtra(Intent.EXTRA_TEXT, summaryText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Kitchen Hisaab Summary"))
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", tint = SaffronPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Sirf Kitchen Manager ke paas authority hai ki containers ko 'Returned' aur 50% cash ko 'Received' mark kare. Delivery boy ke app se mark karne ke buttons hata diye gaye hain.",
                        fontSize = 11.5.sp,
                        color = Color(0xFF475569),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // 1.5 Bartan Stock Master & Live Circulation Status
        item {
            KitchenUtensilsStockMasterCard(
                kitchenUtensils = kitchenUtensils,
                allRows = allRows,
                onManageStock = { showManageUtensilsDialog = true }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 2. Summary KPI Metric Cards (Clickable: Pending Bartan, Pending Payment, Payment Mila)
        item {
            val isPendingContainersActive = filterType == "PENDING_CONTAINERS"
            val isPendingCashActive = filterType == "PENDING_CASH"
            val isSettledActive = filterType == "SETTLED"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pending Containers KPI Card (Pending Bartan)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            filterType = "PENDING_CONTAINERS"
                            selectedMetricBreakdown = "PENDING_CONTAINERS"
                        }
                        .testTag("kpi_card_pending_handi"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPendingContainersActive) Color(0xFFFEF3C7)
                        else if (totalPendingContainers > 0) Color(0xFFFFFBEB)
                        else Color(0xFFF8FAFC)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        if (isPendingContainersActive) 2.dp else 1.dp,
                        if (isPendingContainersActive) AmberSecondary
                        else if (totalPendingContainers > 0) Color(0xFFFDE68A)
                        else Color(0xFFE2E8F0)
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🍲", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Pending Handi",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (totalPendingContainers > 0) Color(0xFF92400E) else Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            "$totalPendingContainers Handi",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = if (totalPendingContainers > 0) AmberSecondary else VegGreen
                        )
                        Text(
                            if (totalPendingContainers > 0) "To be collected" else "All Returned ✅",
                            fontSize = 9.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text("Tap for details", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = AmberSecondary)
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(10.dp), tint = AmberSecondary)
                        }
                    }
                }

                // Pending 50% Cash KPI Card (Pending Payment)
                Card(
                    modifier = Modifier
                        .weight(1.15f)
                        .clickable {
                            filterType = "PENDING_CASH"
                            selectedMetricBreakdown = "PENDING_CASH"
                        }
                        .testTag("kpi_card_pending_cash"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPendingCashActive) Color(0xFFFEE2E2)
                        else if (totalPendingCashAmount > 0) Color(0xFFFEF2F2)
                        else Color(0xFFF0FDF4)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        if (isPendingCashActive) 2.dp else 1.dp,
                        if (isPendingCashActive) Color(0xFFDC2626)
                        else if (totalPendingCashAmount > 0) Color(0xFFFECACA)
                        else Color(0xFFBBF7D0)
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💵", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Pending Cash",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (totalPendingCashAmount > 0) Color(0xFF991B1B) else VegGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            "₹${totalPendingCashAmount.toInt()}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = if (totalPendingCashAmount > 0) Color(0xFFDC2626) else VegGreen
                        )
                        Text(
                            if (totalPendingCashAmount > 0) "With Delivery Boys" else "All Settled ✅",
                            fontSize = 9.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text("Tap for details", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(10.dp), tint = Color(0xFFDC2626))
                        }
                    }
                }

                // Settled / Completed Metrics (Payment Mila & Safe)
                Card(
                    modifier = Modifier
                        .weight(1.05f)
                        .clickable {
                            filterType = "SETTLED"
                            selectedMetricBreakdown = "SETTLED"
                        }
                        .testTag("kpi_card_settled_kitchen"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSettledActive) Color(0xFFDCFCE7)
                        else Color(0xFFF0FDF4)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        if (isSettledActive) 2.dp else 1.dp,
                        if (isSettledActive) VegGreen
                        else Color(0xFFBBF7D0)
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VegGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Settled / Mila", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            "₹${totalReceivedCashAmount.toInt()}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = VegGreen
                        )
                        Text(
                            "$totalReturnedContainers Handi Safe",
                            fontSize = 9.sp,
                            color = Color(0xFF166534)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text("Tap for details", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(10.dp), tint = VegGreen)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // 3. Search & View Format Controls
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (totalOverdueContainers > 0) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFFECACA)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { filterType = "OVERDUE" }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "🚨 $totalOverdueContainers Overdue Containers need reminder!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF991B1B)
                                )
                            }
                            Text(
                                "View Overdue →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Search Input Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("summary_table_search_input"),
                    placeholder = { Text("Search Order #, Customer, Phone, Delivery Boy...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filter Chips & Table / Card Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Filter Chips Row (Scrollable)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SummaryFilterChip(
                            label = "Pending All (${totalPendingContainers + allRows.count { it.hasCash && !it.isCashSubmitted }})",
                            selected = filterType == "PENDING_ALL",
                            onClick = { filterType = "PENDING_ALL" }
                        )
                        SummaryFilterChip(
                            label = "🚨 Overdue ($totalOverdueContainers)",
                            selected = filterType == "OVERDUE",
                            onClick = { filterType = "OVERDUE" }
                        )
                        SummaryFilterChip(
                            label = "🍲 Handi Pending ($totalPendingContainers)",
                            selected = filterType == "PENDING_CONTAINERS",
                            onClick = { filterType = "PENDING_CONTAINERS" }
                        )
                        SummaryFilterChip(
                            label = "💵 Cash Pending (₹${totalPendingCashAmount.toInt()})",
                            selected = filterType == "PENDING_CASH",
                            onClick = { filterType = "PENDING_CASH" }
                        )
                        SummaryFilterChip(
                            label = "Both Pending",
                            selected = filterType == "BOTH_PENDING",
                            onClick = { filterType = "BOTH_PENDING" }
                        )
                        SummaryFilterChip(
                            label = "Settled / History",
                            selected = filterType == "SETTLED",
                            onClick = { filterType = "SETTLED" }
                        )
                        SummaryFilterChip(
                            label = "All Items (${allRows.size})",
                            selected = filterType == "ALL",
                            onClick = { filterType = "ALL" }
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Layout Mode Switcher (Table vs Card view - active when in Order-wise mode)
                    if (groupByMode == "ORDER") {
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            Row(modifier = Modifier.padding(2.dp)) {
                                IconButton(
                                    onClick = { isTableView = true },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.TableChart,
                                        contentDescription = "Table View",
                                        tint = if (isTableView) SaffronPrimary else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { isTableView = false },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ViewAgenda,
                                        contentDescription = "Cards View",
                                        tint = if (!isTableView) SaffronPrimary else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Group-by Mode Switcher: [📋 By Order] vs [🛵 By Delivery Boy]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            color = if (groupByMode == "ORDER") SaffronPrimary else Color.Transparent,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { groupByMode = "ORDER" }
                        ) {
                            Text(
                                "📋 Order-wise (${filteredRows.size})",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (groupByMode == "ORDER") Color.White else Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        Surface(
                            color = if (groupByMode == "DELIVERY_BOY") SaffronPrimary else Color.Transparent,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { groupByMode = "DELIVERY_BOY" }
                        ) {
                            Text(
                                "🛵 By Delivery Boy (${deliveryBoyGroups.size})",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (groupByMode == "DELIVERY_BOY") Color.White else Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // 4. Content: Empty State or Summary Table / Cards / Delivery Boy Groups
        if (filteredRows.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No Pending Items Found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (searchQuery.isNotBlank()) "No records match search '$searchQuery'." else "Aapke paas chuni hui filter ke mutabiq koi pending container ya cash nahi hai. Sab hisaab clear hai!",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        if (filterType != "ALL") {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = {
                                    filterType = "ALL"
                                    searchQuery = ""
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Show All Records", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        } else if (groupByMode == "DELIVERY_BOY") {
            // ==================== DELIVERY BOY GROUPED VIEW ====================
            items(deliveryBoyGroups) { group ->
                DeliveryBoyGroupCard(
                    group = group,
                    context = context,
                    allDeliveryBoys = allDeliveryBoys,
                    onViewDetails = { row -> selectedOrderDetailsRow = row },
                    onMarkReturned = { row -> containerToMarkReturned = row },
                    onMarkReceived = { row -> cashToMarkReceived = row },
                    onSendReminder = { row -> reminderTargetRow = row },
                    onReassignPickup = { row -> rowToReassignPickup = row }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        } else if (isTableView) {
            // ==================== SUMMARY DATA TABLE VIEW ====================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("kitchen_summary_table_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Horizontally scrollable tabular container
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Column {
                                // Table Header Row
                                TableHeaderRow()

                                HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 1.dp)

                                // Table Rows
                                filteredRows.forEachIndexed { index, row ->
                                    TableDataRow(
                                        index = index,
                                        row = row,
                                        context = context,
                                        onViewDetails = { selectedOrderDetailsRow = row },
                                        onMarkReturned = { containerToMarkReturned = row },
                                        onMarkReceived = { cashToMarkReceived = row },
                                        onSendReminder = { reminderTargetRow = row },
                                        onReassignPickup = { rowToReassignPickup = row }
                                    )
                                    if (index < filteredRows.size - 1) {
                                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                                    }
                                }
                            }
                        }

                        // Table Footer info
                        Surface(
                            color = Color(0xFFF8FAFC),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Showing ${filteredRows.size} of ${allRows.size} entries",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "Tip: Swipe horizontally to view all columns",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // ==================== COMPACT CARD LIST VIEW ====================
            items(filteredRows) { row ->
                SummaryCardItem(
                    row = row,
                    context = context,
                    onViewDetails = { selectedOrderDetailsRow = row },
                    onMarkReturned = { containerToMarkReturned = row },
                    onMarkReceived = { cashToMarkReceived = row },
                    onSendReminder = { reminderTargetRow = row },
                    onReassignPickup = { rowToReassignPickup = row }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// TABLE HEADER COMPOSABLE
// -------------------------------------------------------------
@Composable
private fun TableHeaderRow() {
    Row(
        modifier = Modifier
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableCell(text = "Order # & Date", width = 115.dp, isHeader = true)
        TableCell(text = "Customer Details", width = 150.dp, isHeader = true)
        TableCell(text = "Delivery Boy", width = 130.dp, isHeader = true)
        TableCell(text = "Containers (Handi)", width = 170.dp, isHeader = true)
        TableCell(text = "50% Cash Handover", width = 160.dp, isHeader = true)
        TableCell(text = "Actions & Details", width = 210.dp, isHeader = true)
    }
}

// -------------------------------------------------------------
// TABLE DATA ROW COMPOSABLE
// -------------------------------------------------------------
@Composable
private fun TableDataRow(
    index: Int,
    row: ContainerCashSummaryRow,
    context: android.content.Context,
    onViewDetails: () -> Unit,
    onMarkReturned: () -> Unit,
    onMarkReceived: () -> Unit,
    onSendReminder: () -> Unit,
    onReassignPickup: () -> Unit
) {
    val bgColor = if (index % 2 == 0) Color.White else Color(0xFFFBFDFF)

    Row(
        modifier = Modifier
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Col 1: Order # & Date (Clickable to view details)
        Box(
            modifier = Modifier
                .width(115.dp)
                .clickable { onViewDetails() }
        ) {
            Column {
                Text(
                    "#${row.orderId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF2563EB)
                )
                Text(
                    row.eventDate.ifBlank { "Recent" },
                    fontSize = 10.5.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    "🔍 View Details",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB)
                )
            }
        }

        // Col 2: Customer Details & Call Action
        Box(modifier = Modifier.width(150.dp)) {
            Column {
                Text(
                    row.customerName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.5.sp,
                    color = Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(row.customerMobile, fontSize = 10.sp, color = Color.Gray)
                    if (row.customerMobile.isNotBlank()) {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${row.customerMobile}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = VegGreen, modifier = Modifier.size(12.dp))
                        }
                    }
                }
                Text(
                    row.deliveryAddress,
                    fontSize = 9.5.sp,
                    color = Color(0xFF94A3B8),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Col 3: Delivery Boy
        Box(modifier = Modifier.width(130.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        row.deliveryBoyName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.5.sp,
                        color = Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (row.deliveryBoyMobile.isNotBlank()) {
                    Text("📞 ${row.deliveryBoyMobile}", fontSize = 9.5.sp, color = Color.Gray)
                }
                if (row.pickupBoyName.isNotBlank() && row.pickupBoyName != row.deliveryBoyName) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            "🔄 Pickup: ${row.pickupBoyName}",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }

        // Col 4: Containers Status & Details
        Box(modifier = Modifier.width(170.dp)) {
            if (!row.hasContainer) {
                Text("— None —", fontSize = 11.sp, color = Color.Gray)
            } else {
                Column {
                    Text(
                        row.containerDescription,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    if (row.isContainerCollected) {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "✅ RETURNED",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = VegGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else if (row.isOverdue) {
                        Surface(
                            color = Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "🚨 OVERDUE (${row.daysOverdue}d)",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFDC2626),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "⚠️ PENDING RETURN",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Col 5: 50% Cash Status & Details
        Box(modifier = Modifier.width(160.dp)) {
            if (!row.hasCash) {
                Text("— Paid Online —", fontSize = 11.sp, color = VegGreen)
            } else {
                Column {
                    Text(
                        "₹${row.cashAmount.toInt()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (row.isCashSubmitted) VegGreen else Color(0xFFDC2626)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    if (row.isCashSubmitted) {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "✅ RECEIVED (Safe)",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = VegGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Surface(
                            color = Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "⏳ IN TRANSIT (With Boy)",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Col 6: Manual Action Buttons & Details for Kitchen
        Box(modifier = Modifier.width(210.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // View Details Button
                OutlinedButton(
                    onClick = onViewDetails,
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("table_view_details_${row.orderId}")
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Order Details & Boy", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                }

                // Button 1: Mark Container as Returned
                if (row.hasContainer && !row.isContainerCollected) {
                    Button(
                        onClick = onMarkReturned,
                        colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mark_returned_${row.orderId}")
                    ) {
                        Text("🍲 Mark Returned", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }

                    // Button 1A: Re-assign Pickup Boy
                    OutlinedButton(
                        onClick = onReassignPickup,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(11.dp), tint = SaffronPrimary)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("🔄 Re-assign Pickup", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                    }

                    // Button 1B: Overdue Manual Reminder Button to Delivery Boy
                    if (row.isOverdue) {
                        Button(
                            onClick = onSendReminder,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 3.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("remind_overdue_boy_table_${row.orderId}")
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("🔔 Remind ${row.deliveryBoyName.take(10)}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Button 2: Mark Cash as Received
                if (row.hasCash && !row.isCashSubmitted) {
                    Button(
                        onClick = onMarkReceived,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mark_received_${row.orderId}")
                    ) {
                        Text("💵 Mark Received (₹${row.cashAmount.toInt()})", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // If both are already completed
                if ((!row.hasContainer || row.isContainerCollected) && (!row.hasCash || row.isCashSubmitted)) {
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("All Settled ✅", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPACT CARD VIEW COMPOSABLE
// -------------------------------------------------------------
@Composable
private fun SummaryCardItem(
    row: ContainerCashSummaryRow,
    context: android.content.Context,
    onViewDetails: () -> Unit,
    onMarkReturned: () -> Unit,
    onMarkReceived: () -> Unit,
    onSendReminder: () -> Unit,
    onReassignPickup: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("summary_card_${row.orderId}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            if (row.isOverdue)
                Color(0xFFFECACA)
            else if ((row.hasContainer && !row.isContainerCollected) || (row.hasCash && !row.isCashSubmitted))
                Color(0xFFFED7AA)
            else
                Color(0xFFE2E8F0)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Order ID & Date
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewDetails() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (row.isOverdue) Color(0xFFFEE2E2) else Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "#${row.orderId}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (row.isOverdue) Color(0xFFDC2626) else Color(0xFF2563EB),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(row.eventDate.ifBlank { "Event Order" }, fontSize = 11.sp, color = Color.Gray)
                }

                // Delivery Boy chip & Pickup Boy chip
                Column(horizontalAlignment = Alignment.End) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onViewDetails() }
                    ) {
                        Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(row.deliveryBoyName, fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp, color = Color(0xFF1E293B))
                    }
                    if (row.pickupBoyName.isNotBlank() && row.pickupBoyName != row.deliveryBoyName) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                "🔄 Pickup: ${row.pickupBoyName}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Customer details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(row.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                    Text(row.deliveryAddress, fontSize = 10.5.sp, color = Color(0xFF64748B), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                if (row.customerMobile.isNotBlank()) {
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${row.customerMobile}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call Customer", tint = VegGreen, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            // Two-column metrics inside card: Containers & Cash
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Container status box
                Surface(
                    modifier = Modifier.weight(1f),
                    color = if (row.isOverdue) Color(0xFFFEF2F2)
                    else if (row.hasContainer && !row.isContainerCollected) Color(0xFFFFFBEB)
                    else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        if (row.isOverdue) Color(0xFFFECACA)
                        else if (row.hasContainer && !row.isContainerCollected) Color(0xFFFDE68A)
                        else Color(0xFFE2E8F0)
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🍲", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Container Status", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            if (row.hasContainer) row.containerDescription else "None",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (row.isOverdue) {
                            Text(
                                "🚨 OVERDUE (${row.daysOverdue}d late)",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFDC2626)
                            )
                        } else {
                            Text(
                                if (!row.hasContainer) "No utensils"
                                else if (row.isContainerCollected) "✅ Returned to Kitchen"
                                else "⏳ Customer ke paas",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (!row.hasContainer || row.isContainerCollected) VegGreen else AmberSecondary
                            )
                        }
                    }
                }

                // Cash status box
                Surface(
                    modifier = Modifier.weight(1f),
                    color = if (row.hasCash && !row.isCashSubmitted) Color(0xFFFEF2F2) else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (row.hasCash && !row.isCashSubmitted) Color(0xFFFECACA) else Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💵", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("50% COD Cash", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            if (row.hasCash) "₹${row.cashAmount.toInt()}" else "₹0 (Online)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (row.hasCash && !row.isCashSubmitted) Color(0xFFDC2626) else VegGreen
                        )
                        Text(
                            if (!row.hasCash) "Paid 100% Online"
                            else if (row.isCashSubmitted) "✅ Kitchen Received"
                            else "⏳ Delivery boy ke paas",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (row.isCashSubmitted || !row.hasCash) VegGreen else Color(0xFFDC2626)
                        )
                    }
                }
            }

            // Action Buttons
            if ((row.hasContainer && !row.isContainerCollected) || (row.hasCash && !row.isCashSubmitted)) {
                Spacer(modifier = Modifier.height(10.dp))

                // Overdue Reminder Button for Delivery Boy
                if (row.isOverdue && row.hasContainer && !row.isContainerCollected) {
                    Button(
                        onClick = onSendReminder,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("remind_overdue_boy_card_${row.orderId}"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "🔔 Send Overdue Reminder to ${row.deliveryBoyName.ifBlank { "Delivery Boy" }}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Re-assign Pickup Boy Button
                if (row.hasContainer && !row.isContainerCollected) {
                    OutlinedButton(
                        onClick = onReassignPickup,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp), tint = SaffronPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (row.pickupBoyName.isNotBlank() && row.pickupBoyName != row.deliveryBoyName)
                                "🔄 Re-assign Pickup (Current: ${row.pickupBoyName})"
                            else "🔄 Re-assign Bartan Pickup Boy",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (row.hasContainer && !row.isContainerCollected) {
                        Button(
                            onClick = onMarkReturned,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text("🍲 Mark Returned", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (row.hasCash && !row.isCashSubmitted) {
                        Button(
                            onClick = onMarkReceived,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text("💵 Mark Received (₹${row.cashAmount.toInt()})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details action button for compact card view
            OutlinedButton(
                onClick = onViewDetails,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_view_details_${row.orderId}")
            ) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF2563EB))
                Spacer(modifier = Modifier.width(6.dp))
                Text("🔍 View Delivery Boy, Customer & Order Details", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER CELL & CHIP COMPOSABLES
// -------------------------------------------------------------
@Composable
private fun TableCell(text: String, width: androidx.compose.ui.unit.Dp, isHeader: Boolean = false) {
    Box(
        modifier = Modifier
            .width(width)
            .padding(end = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = if (isHeader) 11.sp else 11.5.sp,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            color = if (isHeader) Color(0xFF475569) else Color(0xFF1E293B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SummaryFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        },
        shape = RoundedCornerShape(16.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = SaffronPrimary,
            selectedLabelColor = Color.White,
            containerColor = Color.White,
            labelColor = Color(0xFF475569)
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = Color(0xFFCBD5E1),
            selectedBorderColor = SaffronPrimary
        )
    )
}

// =============================================================
// DETAIL BREAKDOWN MODAL DIALOGS (FOR CLICKING KPI OR ORDER ROW)
// =============================================================

@Composable
private fun KitchenMetricDetailsBreakdownDialog(
    metricType: String,
    allMatchingRows: List<ContainerCashSummaryRow>,
    allDeliveryBoys: List<DeliveryBoyEntity>,
    context: android.content.Context,
    onDismiss: () -> Unit,
    onMarkReturned: (ContainerCashSummaryRow) -> Unit,
    onMarkReceived: (ContainerCashSummaryRow) -> Unit,
    onSendReminder: (ContainerCashSummaryRow) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = remember(allMatchingRows, searchQuery) {
        if (searchQuery.isBlank()) {
            allMatchingRows
        } else {
            val q = searchQuery.trim().lowercase(Locale.getDefault())
            allMatchingRows.filter {
                it.orderId.lowercase(Locale.getDefault()).contains(q) ||
                it.customerName.lowercase(Locale.getDefault()).contains(q) ||
                it.customerMobile.contains(q) ||
                it.deliveryBoyName.lowercase(Locale.getDefault()).contains(q) ||
                it.deliveryBoyMobile.contains(q) ||
                it.deliveryAddress.lowercase(Locale.getDefault()).contains(q)
            }
        }
    }

    val (headerTitle, headerSubtitle, headerBg, headerIcon) = when (metricType) {
        "PENDING_CONTAINERS" -> Quad(
            "🍲 Pending Bartan (Handi) Details",
            "${allMatchingRows.size} Orders with Utensils Pending Return from Customer",
            Color(0xFFEA580C),
            Icons.Default.SoupKitchen
        )
        "PENDING_CASH" -> {
            val totalCash = allMatchingRows.sumOf { it.cashAmount }.toInt()
            Quad(
                "💵 Pending 50% Cash Collection (₹$totalCash)",
                "${allMatchingRows.size} Orders with COD Cash Pending Handover to Kitchen",
                Color(0xFF1E40AF),
                Icons.Default.AttachMoney
            )
        }
        "SETTLED" -> Quad(
            "✅ Payment Mila & Bartan Returned",
            "${allMatchingRows.size} Orders Fully Cleared & Hisaab Closed",
            VegGreen,
            Icons.Default.CheckCircle
        )
        else -> Quad(
            "📋 Orders Breakdown",
            "${allMatchingRows.size} Orders Listed",
            Color(0xFF0F172A),
            Icons.Default.Info
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("metric_breakdown_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Banner
                Surface(
                    color = headerBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(headerIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    headerTitle,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    headerSubtitle,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_metric_dialog_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Search Bar
                Surface(
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("metric_dialog_search_input"),
                        placeholder = {
                            Text("Search by Customer, Delivery Boy, Mobile, Order #...", fontSize = 11.5.sp)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = SaffronPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )
                }

                HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                // List of matching orders
                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VegGreen, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                if (searchQuery.isBlank()) "No records found in this category!" else "No matching orders for '$searchQuery'",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF334155)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "All orders are updated.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredList) { row ->
                            DetailBreakdownOrderCard(
                                row = row,
                                allDeliveryBoys = allDeliveryBoys,
                                context = context,
                                onMarkReturned = { onMarkReturned(row) },
                                onMarkReceived = { onMarkReceived(row) },
                                onSendReminder = { onSendReminder(row) }
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                // Bottom Bar
                Surface(
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Showing ${filteredList.size} of ${allMatchingRows.size} entries",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B)
                        )
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Done / Close", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KitchenSingleOrderDetailsDialog(
    row: ContainerCashSummaryRow,
    allDeliveryBoys: List<DeliveryBoyEntity>,
    context: android.content.Context,
    onDismiss: () -> Unit,
    onMarkReturned: (ContainerCashSummaryRow) -> Unit,
    onMarkReceived: (ContainerCashSummaryRow) -> Unit,
    onSendReminder: (ContainerCashSummaryRow) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .testTag("single_order_details_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Banner
                Surface(
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                color = SaffronPrimary,
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("#", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "Order #${row.orderId} Details",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Customer, Delivery Boy & Kitchen Hisaab",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_single_order_dialog_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    item {
                        DetailBreakdownOrderCard(
                            row = row,
                            allDeliveryBoys = allDeliveryBoys,
                            context = context,
                            onMarkReturned = { onMarkReturned(row) },
                            onMarkReceived = { onMarkReceived(row) },
                            onSendReminder = { onSendReminder(row) }
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                // Bottom Bar
                Surface(
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                        ) {
                            Text("Close", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailBreakdownOrderCard(
    row: ContainerCashSummaryRow,
    allDeliveryBoys: List<DeliveryBoyEntity>,
    context: android.content.Context,
    onMarkReturned: () -> Unit,
    onMarkReceived: () -> Unit,
    onSendReminder: () -> Unit
) {
    // Find delivery boy entity if available
    val boyEntity = remember(allDeliveryBoys, row.deliveryBoyId, row.deliveryBoyName) {
        allDeliveryBoys.find {
            it.id == row.deliveryBoyId ||
            it.name.equals(row.deliveryBoyName, ignoreCase = true)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("detail_order_card_${row.orderId}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            if (row.isOverdue) Color(0xFFFECACA)
            else if ((row.hasContainer && !row.isContainerCollected) || (row.hasCash && !row.isCashSubmitted)) Color(0xFFFED7AA)
            else Color(0xFFE2E8F0)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // 1. Order Header: ID, Date, Delivery Time & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (row.isOverdue) Color(0xFFFEE2E2) else Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "#${row.orderId}",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = if (row.isOverdue) Color(0xFFDC2626) else Color(0xFF2563EB),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        row.eventDate.ifBlank { "Event Order" },
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                }

                // Status chip
                Surface(
                    color = if (row.isOverdue) Color(0xFFFEF2F2)
                    else if ((!row.hasContainer || row.isContainerCollected) && (!row.hasCash || row.isCashSubmitted)) Color(0xFFDCFCE7)
                    else Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(
                        1.dp,
                        if (row.isOverdue) Color(0xFFFCA5A5)
                        else if ((!row.hasContainer || row.isContainerCollected) && (!row.hasCash || row.isCashSubmitted)) Color(0xFF86EFAC)
                        else Color(0xFFFDE68A)
                    )
                ) {
                    Text(
                        if (row.isOverdue) "🚨 OVERDUE"
                        else if ((!row.hasContainer || row.isContainerCollected) && (!row.hasCash || row.isCashSubmitted)) "✅ ALL SETTLED"
                        else "⏳ PENDING HISAAB",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (row.isOverdue) Color(0xFFB91C1C)
                        else if ((!row.hasContainer || row.isContainerCollected) && (!row.hasCash || row.isCashSubmitted)) VegGreen
                        else Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Food / Items summary if present
            if (row.rawOrder?.itemsSummary?.isNotBlank() == true) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "🍛 ${row.rawOrder.itemsSummary}",
                    fontSize = 11.5.sp,
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // 2. DELIVERY BOY SECTION (PROMINENT FOR KITCHEN PARITY)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF)),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFBAE6FD))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Assigned Delivery Boy",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = Color(0xFF0369A1)
                            )
                        }

                        // Duty Status Badge
                        Surface(
                            color = if (boyEntity?.isOnline != false) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                if (boyEntity?.isOnline != false) "🟢 Online on Duty" else "⚪ Offline",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (boyEntity?.isOnline != false) VegGreen else Color(0xFF64748B),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                row.deliveryBoyName.ifBlank { "Delivery Boy Not Assigned" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = Color(0xFF0F172A)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    if (row.deliveryBoyMobile.isNotBlank()) "📞 ${row.deliveryBoyMobile}" else "No Mobile",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569)
                                )
                                if (boyEntity?.isAadhaarVerified == true) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "✓ Aadhaar Verified",
                                        fontSize = 10.sp,
                                        color = VegGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Communication Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (row.deliveryBoyMobile.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${row.deliveryBoyMobile}"))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(VegGreen, CircleShape)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "Call Delivery Boy", tint = Color.White, modifier = Modifier.size(17.dp))
                                }

                                IconButton(
                                    onClick = {
                                        val cleanMobile = row.deliveryBoyMobile.replace("+91", "").replace(" ", "").replace("-", "").trim()
                                        val msg = when {
                                            row.hasContainer && !row.isContainerCollected && row.hasCash && !row.isCashSubmitted ->
                                                "Namaste ${row.deliveryBoyName}, Order #${row.orderId} ka ₹${row.cashAmount.toInt()} cash aur bartan (${row.containerDescription}) kitchen me submit karwayein."
                                            row.hasContainer && !row.isContainerCollected ->
                                                "Namaste ${row.deliveryBoyName}, Order #${row.orderId} ka bartan (${row.containerDescription}) customer se wapas kitchen laayein."
                                            row.hasCash && !row.isCashSubmitted ->
                                                "Namaste ${row.deliveryBoyName}, Order #${row.orderId} ka ₹${row.cashAmount.toInt()} COD cash kitchen me submit karein."
                                            else ->
                                                "Order #${row.orderId} details: Bartan aur cash verified."
                                        }
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/91$cleanMobile?text=${Uri.encode(msg)}"))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(Color(0xFF25D366), CircleShape)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = "WhatsApp Delivery Boy", tint = Color.White, modifier = Modifier.size(17.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. CUSTOMER DETAILS SECTION
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(17.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Customer Information",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = Color(0xFF334155)
                            )
                        }

                        if (row.rawOrder?.deliveryTimeSlot?.isNotBlank() == true) {
                            Text(
                                "Slot: ${row.rawOrder.deliveryTimeSlot}",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                row.customerName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = Color(0xFF0F172A)
                            )
                            if (row.customerMobile.isNotBlank()) {
                                Text("📞 ${row.customerMobile}", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                            if (row.deliveryAddress.isNotBlank()) {
                                Row(
                                    modifier = Modifier.padding(top = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        row.deliveryAddress,
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Call / WhatsApp Customer
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (row.customerMobile.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${row.customerMobile}"))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(VegGreen, CircleShape)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "Call Customer", tint = Color.White, modifier = Modifier.size(17.dp))
                                }

                                IconButton(
                                    onClick = {
                                        val cleanMobile = row.customerMobile.replace("+91", "").replace(" ", "").replace("-", "").trim()
                                        val msg = "Namaste ${row.customerName}, Order #${row.orderId} ke sambandh me kitchen team se message hai."
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/91$cleanMobile?text=${Uri.encode(msg)}"))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(Color(0xFF25D366), CircleShape)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = "WhatsApp Customer", tint = Color.White, modifier = Modifier.size(17.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. BARTAN & CASH STATUS DUAL CARDS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bartan Card
                Surface(
                    color = if (row.isOverdue) Color(0xFFFFF1F2)
                    else if (row.isContainerCollected) Color(0xFFF0FDF4)
                    else Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        if (row.isOverdue) Color(0xFFFECDD3)
                        else if (row.isContainerCollected) Color(0xFFBBF7D0)
                        else Color(0xFFFDE68A)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.SoupKitchen,
                                contentDescription = null,
                                tint = if (row.isOverdue) Color(0xFFE11D48) else if (row.isContainerCollected) VegGreen else Color(0xFFD97706),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "BARTAN STATUS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (row.isOverdue) Color(0xFFE11D48) else if (row.isContainerCollected) VegGreen else Color(0xFFD97706)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            row.containerDescription,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            if (row.isOverdue) "🚨 Late by ${row.daysOverdue} days"
                            else if (row.isContainerCollected) "✅ Returned to Kitchen"
                            else if (!row.hasContainer) "— No Handi —"
                            else "⏳ Pending with Customer",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (row.isOverdue) Color(0xFFBE123C) else if (row.isContainerCollected) VegGreen else Color(0xFFB45309)
                        )
                    }
                }

                // Cash Card
                Surface(
                    color = if (row.hasCash && !row.isCashSubmitted) Color(0xFFEFF6FF)
                    else if (row.isCashSubmitted) Color(0xFFF0FDF4)
                    else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        if (row.hasCash && !row.isCashSubmitted) Color(0xFFBFDBFE)
                        else if (row.isCashSubmitted) Color(0xFFBBF7D0)
                        else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = if (row.hasCash && !row.isCashSubmitted) Color(0xFF1D4ED8) else if (row.isCashSubmitted) VegGreen else Color(0xFF64748B),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "50% CASH STATUS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (row.hasCash && !row.isCashSubmitted) Color(0xFF1D4ED8) else if (row.isCashSubmitted) VegGreen else Color(0xFF64748B)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            if (row.hasCash) "₹${row.cashAmount.toInt()}" else "₹0 (Online)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (row.hasCash && !row.isCashSubmitted) Color(0xFF1E40AF) else VegGreen
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            if (!row.hasCash) "Paid 100% Online"
                            else if (row.isCashSubmitted) "✅ Payment Mila (Received)"
                            else "⏳ With ${row.deliveryBoyName.take(8)}",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (row.isCashSubmitted || !row.hasCash) VegGreen else Color(0xFF1D4ED8)
                        )
                    }
                }
            }

            // 5. MANUAL KITCHEN ACTIONS
            if ((row.hasContainer && !row.isContainerCollected) || (row.hasCash && !row.isCashSubmitted)) {
                Spacer(modifier = Modifier.height(12.dp))

                // Overdue Reminder Button
                if (row.isOverdue && row.hasContainer && !row.isContainerCollected) {
                    Button(
                        onClick = onSendReminder,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "🔔 Send Overdue Reminder to ${row.deliveryBoyName.ifBlank { "Delivery Boy" }}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (row.hasContainer && !row.isContainerCollected) {
                        Button(
                            onClick = onMarkReturned,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text("🍲 Mark Handi Returned", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (row.hasCash && !row.isCashSubmitted) {
                        Button(
                            onClick = onMarkReceived,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text("💵 Mark Received (₹${row.cashAmount.toInt()})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VegGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "All Hisaab Settled: Handi Returned & Payment Mila",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VegGreen
                        )
                    }
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

// -------------------------------------------------------------
// KITCHEN UTENSILS STOCK MASTER CARD COMPOSABLE
// -------------------------------------------------------------
@Composable
fun KitchenUtensilsStockMasterCard(
    kitchenUtensils: List<KitchenUtensilEntity>,
    allRows: List<ContainerCashSummaryRow>,
    onManageStock: () -> Unit
) {
    val pendingRows = allRows.filter { it.hasContainer && !it.isContainerCollected }
    val handisOutside = pendingRows.sumOf { it.handiCount }
    val spoonsOutside = pendingRows.sumOf { it.spoonsCount }
    val boxesOutside = pendingRows.sumOf { it.boxesCount }

    val handiEntity = kitchenUtensils.find { it.name.contains("Handi", ignoreCase = true) }
    val totalHandi = handiEntity?.totalStock ?: 50
    val availableHandi = (totalHandi - handisOutside).coerceAtLeast(0)

    val spoonEntity = kitchenUtensils.find { it.name.contains("Chammach", ignoreCase = true) || it.name.contains("Spoon", ignoreCase = true) }
    val totalSpoons = spoonEntity?.totalStock ?: 80
    val availableSpoons = (totalSpoons - spoonsOutside).coerceAtLeast(0)

    val boxEntity = kitchenUtensils.find { it.name.contains("Box", ignoreCase = true) }
    val totalBoxes = boxEntity?.totalStock ?: 40
    val availableBoxes = (totalBoxes - boxesOutside).coerceAtLeast(0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = SaffronPrimary.copy(alpha = 0.12f),
                        shape = CircleShape,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🍲", fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            "Bartan Master Stock & Live Circulation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            "Live tracking: Kitchen Stock vs Bahar Market me Bartan",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                OutlinedButton(
                    onClick = onManageStock,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, SaffronPrimary)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp), tint = SaffronPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Stock Update", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UtensilStockMiniCard(
                    modifier = Modifier.weight(1f),
                    icon = "🍲",
                    name = "Biryani Handi",
                    total = totalHandi,
                    outside = handisOutside,
                    available = availableHandi,
                    warningThreshold = 10
                )
                UtensilStockMiniCard(
                    modifier = Modifier.weight(1f),
                    icon = "🥄",
                    name = "Bada Chammach",
                    total = totalSpoons,
                    outside = spoonsOutside,
                    available = availableSpoons,
                    warningThreshold = 15
                )
                UtensilStockMiniCard(
                    modifier = Modifier.weight(1f),
                    icon = "🥗",
                    name = "Kachumber Box",
                    total = totalBoxes,
                    outside = boxesOutside,
                    available = availableBoxes,
                    warningThreshold = 10
                )
            }
        }
    }
}

@Composable
private fun UtensilStockMiniCard(
    modifier: Modifier = Modifier,
    icon: String,
    name: String,
    total: Int,
    outside: Int,
    available: Int,
    warningThreshold: Int
) {
    val isLow = available <= warningThreshold
    Surface(
        modifier = modifier,
        color = if (isLow) Color(0xFFFEF2F2) else Color(0xFFF8FAFC),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (isLow) Color(0xFFFCA5A5) else Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$icon $name", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (isLow) {
                Spacer(modifier = Modifier.height(2.dp))
                Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(3.dp)) {
                    Text("⚠️ LOW STOCK", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFFDC2626), modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total:", fontSize = 9.5.sp, color = Color(0xFF64748B))
                Text("$total", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Bahar:", fontSize = 9.5.sp, color = Color(0xFFDC2626))
                Text("$outside", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Kitchen:", fontSize = 9.5.sp, color = VegGreen)
                Text("$available", fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = if (isLow) Color(0xFFDC2626) else VegGreen)
            }
        }
    }
}

// -------------------------------------------------------------
// DELIVERY BOY GROUP CARD COMPOSABLE
// -------------------------------------------------------------
@Composable
private fun DeliveryBoyGroupCard(
    group: DeliveryBoyGroupSummary,
    context: android.content.Context,
    allDeliveryBoys: List<DeliveryBoyEntity>,
    onViewDetails: (ContainerCashSummaryRow) -> Unit,
    onMarkReturned: (ContainerCashSummaryRow) -> Unit,
    onMarkReceived: (ContainerCashSummaryRow) -> Unit,
    onSendReminder: (ContainerCashSummaryRow) -> Unit,
    onReassignPickup: (ContainerCashSummaryRow) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (group.overdueCount > 0) Color(0xFFFECACA) else Color(0xFFCBD5E1))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Delivery Boy details & Aggregate Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = CircleShape,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(22.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            group.boyName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color(0xFF0F172A)
                        )
                        if (group.boyMobile.isNotBlank()) {
                            Text("📞 ${group.boyMobile}", fontSize = 10.5.sp, color = Color(0xFF64748B))
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Call Boy Button
                    if (group.boyMobile.isNotBlank()) {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${group.boyMobile}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call Boy", tint = VegGreen, modifier = Modifier.size(16.dp))
                        }
                    }

                    // Share WhatsApp Hisaab Button
                    IconButton(
                        onClick = {
                            val boyText = StringBuilder().apply {
                                append("🛵 *A1 Huma Kitchen - Delivery Boy Hisaab: ${group.boyName}*\n")
                                append("Pending Handis: ${group.pendingHandis} | Pending Cash: ₹${group.pendingCash.toInt()}\n\n")
                                group.orders.forEachIndexed { i, o ->
                                    append("${i + 1}. Order #${o.orderId} - ${o.customerName}\n")
                                    append("   📍 ${o.deliveryAddress}\n")
                                    append("   📞 ${o.customerMobile}\n")
                                    if (o.hasContainer && !o.isContainerCollected) {
                                        append("   🍲 Bartan: ${o.handiCount} Handi, ${o.spoonsCount} Spoons\n")
                                    }
                                    if (o.hasCash && !o.isCashSubmitted) {
                                        append("   💵 Cash: ₹${o.cashAmount.toInt()}\n")
                                    }
                                    append("\n")
                                }
                                append("Kripya kitchen aakar jama karein.")
                            }.toString()

                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, boyText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Boy Hisaab"))
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share Hisaab", tint = SaffronPrimary, modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand",
                            tint = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Metrics Row for this boy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🍲", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${group.pendingHandis} Handi Bahar",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }
                }

                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💵", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "₹${group.pendingCash.toInt()} Pending Cash",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                    }
                }

                if (group.overdueCount > 0) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🚨", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                "${group.overdueCount} Overdue",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }
                }
            }

            // Expanded Order List for this delivery boy
            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Assigned Orders (${group.orders.size}):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (row in group.orders) {
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("#${row.orderId} • ${row.customerName}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                                    if (row.customerMobile.isNotBlank()) {
                                        IconButton(
                                            onClick = {
                                                val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${row.customerMobile}"))
                                                context.startActivity(dial)
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Call, contentDescription = "Call Customer", tint = VegGreen, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }

                                Text(row.deliveryAddress, fontSize = 10.sp, color = Color(0xFF64748B), maxLines = 1, overflow = TextOverflow.Ellipsis)

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (row.hasContainer) {
                                        Text(
                                            "🍲 ${row.handiCount}H, ${row.spoonsCount}S • ${if (row.isContainerCollected) "✅ Returned" else "⏳ Bahar"}",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (row.isContainerCollected) VegGreen else Color(0xFF92400E)
                                        )
                                    }
                                    if (row.hasCash) {
                                        Text(
                                            "💵 ₹${row.cashAmount.toInt()} • ${if (row.isCashSubmitted) "✅ Paid" else "⏳ With Boy"}",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (row.isCashSubmitted) VegGreen else Color(0xFF1E40AF)
                                        )
                                    }
                                }

                                if (row.pickupBoyName.isNotBlank() && row.pickupBoyName != row.deliveryBoyName) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                                        Text("🔄 Pickup assigned to: ${row.pickupBoyName}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (row.hasContainer && !row.isContainerCollected) {
                                        Button(
                                            onClick = { onMarkReturned(row) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 3.dp)
                                        ) {
                                            Text("🍲 Returned", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = { onReassignPickup(row) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 3.dp)
                                        ) {
                                            Text("🔄 Re-assign", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                                        }
                                    }

                                    if (row.hasCash && !row.isCashSubmitted) {
                                        Button(
                                            onClick = { onMarkReceived(row) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 3.dp)
                                        ) {
                                            Text("💵 Cash Mila", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { onViewDetails(row) },
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text("Details", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// RE-ASSIGN BARTAN PICKUP BOY DIALOG
// -------------------------------------------------------------
@Composable
private fun KitchenReassignPickupBoyDialog(
    row: ContainerCashSummaryRow,
    allDeliveryBoys: List<DeliveryBoyEntity>,
    context: android.content.Context,
    onDismiss: () -> Unit,
    onConfirmReassign: (DeliveryBoyEntity) -> Unit
) {
    var selectedBoyId by remember {
        mutableStateOf(
            if (row.pickupBoyName.isNotBlank()) {
                allDeliveryBoys.find { it.name == row.pickupBoyName }?.id ?: (allDeliveryBoys.firstOrNull()?.id ?: "")
            } else {
                allDeliveryBoys.firstOrNull { it.name != row.deliveryBoyName }?.id ?: (allDeliveryBoys.firstOrNull()?.id ?: "")
            }
        )
    }
    var sendWhatsAppNotification by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = SaffronPrimary.copy(alpha = 0.12f),
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🔄", fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Re-assign Pickup Valet", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Bartan Retrieval Delegation", fontSize = 10.5.sp, color = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Order context summary
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Order #${row.orderId} • ${row.customerName}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                        Text("📍 ${row.deliveryAddress}", fontSize = 10.sp, color = Color(0xFF64748B), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("🍲 Bartan: ${row.handiCount} Handi, ${row.spoonsCount} Spoons, ${row.boxesCount} Boxes", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF92400E))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Original Delivery: ${row.deliveryBoyName}", fontSize = 10.sp, color = Color(0xFF475569))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Chuniye kis boy ko Bartan Pickup assign karna hai:", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(6.dp))

                if (allDeliveryBoys.isEmpty()) {
                    Text("Koi Delivery Boy register nahi hai.", fontSize = 11.sp, color = Color.Red)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        allDeliveryBoys.forEach { boy ->
                            val isSelected = boy.id == selectedBoyId
                            Surface(
                                color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF3B82F6) else Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedBoyId = boy.id }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedBoyId = boy.id },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2563EB))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            boy.name,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp,
                                            color = Color(0xFF1E293B)
                                        )
                                        if (boy.mobile.isNotBlank()) {
                                            Text("📞 ${boy.mobile}", fontSize = 10.sp, color = Color.Gray)
                                        }
                                    }
                                    if (boy.name == row.deliveryBoyName) {
                                        Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp)) {
                                            Text("Original Delivery", fontSize = 8.5.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { sendWhatsAppNotification = !sendWhatsAppNotification },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = sendWhatsAppNotification,
                        onCheckedChange = { sendWhatsAppNotification = it },
                        colors = CheckboxDefaults.colors(checkedColor = VegGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "WhatsApp par naye boy ko pickup task ka message bhejein",
                        fontSize = 11.sp,
                        color = Color(0xFF334155)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val chosenBoy = allDeliveryBoys.find { it.id == selectedBoyId }
                    if (chosenBoy != null) {
                        onConfirmReassign(chosenBoy)
                        if (sendWhatsAppNotification && chosenBoy.mobile.isNotBlank()) {
                            val msg = "Salam ${chosenBoy.name} bhai,\n" +
                                "Kitchen se aapko Bartan Pickup task assign hua hai:\n" +
                                "📦 Order #${row.orderId}\n" +
                                "👤 Customer: ${row.customerName}\n" +
                                "📞 Phone: ${row.customerMobile}\n" +
                                "📍 Address: ${row.deliveryAddress}\n" +
                                "🍲 Bartan: ${row.handiCount} Handi, ${row.spoonsCount} Spoons, ${row.boxesCount} Boxes\n\n" +
                                "Kripya bartan collect karke kitchen jama karein."
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://api.whatsapp.com/send?phone=+91${chosenBoy.mobile}&text=${Uri.encode(msg)}")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    putExtra(Intent.EXTRA_TEXT, msg)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Send Task to Delivery Boy"))
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirm Re-assign", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontSize = 12.sp)
            }
        }
    )
}

// -------------------------------------------------------------
// MANAGE UTENSILS INVENTORY DIALOG
// -------------------------------------------------------------
@Composable
private fun KitchenManageUtensilsInventoryDialog(
    utensils: List<KitchenUtensilEntity>,
    allRows: List<ContainerCashSummaryRow>,
    onDismiss: () -> Unit,
    onUpdateUtensil: (KitchenUtensilEntity) -> Unit
) {
    val pendingRows = allRows.filter { it.hasContainer && !it.isContainerCollected }
    val handisOutside = pendingRows.sumOf { it.handiCount }
    val spoonsOutside = pendingRows.sumOf { it.spoonsCount }
    val boxesOutside = pendingRows.sumOf { it.boxesCount }

    // Provide default list if database table is initially empty
    val displayList = if (utensils.isNotEmpty()) {
        utensils
    } else {
        listOf(
            KitchenUtensilEntity(id = "utensil_handi", name = "Biryani Handi", icon = "🍲", totalStock = 50, unit = "Pcs"),
            KitchenUtensilEntity(id = "utensil_spoon", name = "Bada Chammach (Serving Spoons)", icon = "🥄", totalStock = 80, unit = "Pcs"),
            KitchenUtensilEntity(id = "utensil_box", name = "Kachumber / Raita Box", icon = "🥗", totalStock = 40, unit = "Pcs")
        )
    }

    var editableList by remember(displayList) { mutableStateOf(displayList) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = SaffronPrimary.copy(alpha = 0.12f),
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🍲", fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Kitchen Bartan Inventory", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Master Stock Management", fontSize = 10.5.sp, color = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Kitchen ke kul bartan stock ko set karein. Bahar gaye hue bartan automatic subtract honge.",
                    fontSize = 11.sp,
                    color = Color(0xFF475569)
                )

                editableList.forEachIndexed { index, item ->
                    val outsideCount = when {
                        item.name.contains("Handi", true) -> handisOutside
                        item.name.contains("Chammach", true) || item.name.contains("Spoon", true) -> spoonsOutside
                        else -> boxesOutside
                    }
                    val currentAvailable = (item.totalStock - outsideCount).coerceAtLeast(0)

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${item.icon} ${item.name}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                                Text("Kitchen: $currentAvailable", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = VegGreen)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Bahar: $outsideCount ${item.unit}", fontSize = 10.5.sp, color = Color(0xFFDC2626))

                                // Increment / Decrement stock controls
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            if (item.totalStock > 0) {
                                                val updated = item.copy(totalStock = item.totalStock - 5)
                                                editableList = editableList.toMutableList().also { it[index] = updated }
                                                onUpdateUtensil(updated)
                                            }
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Surface(
                                            color = Color(0xFFE2E8F0),
                                            shape = CircleShape,
                                            modifier = Modifier.size(22.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("-5", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Text(
                                        "${item.totalStock} ${item.unit}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = Color(0xFF0F172A),
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )

                                    IconButton(
                                        onClick = {
                                            val updated = item.copy(totalStock = item.totalStock + 5)
                                            editableList = editableList.toMutableList().also { it[index] = updated }
                                            onUpdateUtensil(updated)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Surface(
                                            color = SaffronPrimary.copy(alpha = 0.15f),
                                            shape = CircleShape,
                                            modifier = Modifier.size(22.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("+5", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Done", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    )
}
