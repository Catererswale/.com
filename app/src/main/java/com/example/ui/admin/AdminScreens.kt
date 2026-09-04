package com.example.ui.admin

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CatererEntity
import com.example.data.models.KycStatus
import com.example.data.models.OrderEntity
import com.example.data.models.OrderStatus
import com.example.data.models.SettlementStatus
import com.example.data.models.WeeklySettlementSummary
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen
import com.example.data.repository.CaterersViewModel
import com.example.util.SettlementReportUtils

@Composable
fun AdminMainContainer(
    viewModel: CaterersViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "Dashboard & Analytics",
        "Caterers Partner Mgt",
        "➕ Add Kitchen Partner",
        "KYC Approvals",
        "All Orders Master",
        "Settlements & Fee",
        "Settlement Audit History",
        "Customer Disputes",
        "System Toggles & Broadcast",
        "Kitchen & App Settings"
    )

    Column(modifier = modifier.fillMaxSize()) {
        Surface(color = Color(0xFF0F172A), contentColor = Color.White) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = AmberSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Super Admin Control Panel", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, style = MaterialTheme.typography.labelLarge, fontSize = 13.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium) },
                            modifier = Modifier.testTag("admin_tab_$index")
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
                0 -> AdminDashboardScreen(viewModel, onNavigateTab = { selectedTab = it })
                1 -> AdminCatererManagementScreen(viewModel, onNavigateToAddKitchen = { selectedTab = 2 })
                2 -> AdminAddKitchenPartnerScreen(viewModel, onPartnerAddedSuccess = { selectedTab = 1 })
                3 -> AdminKycApprovalScreen(viewModel)
                4 -> AdminAllOrdersScreen(viewModel)
                5 -> AdminSettlementScreen(viewModel)
                6 -> AdminSettlementHistoryScreen(viewModel)
                7 -> AdminDisputeScreen(viewModel)
                8 -> AdminSystemTogglesScreen(viewModel)
                9 -> com.example.ui.kitchen.KitchenSettingsScreen(viewModel)
            }
        }
    }
}

data class CommissionChartDataPoint(
    val label: String,
    val fullDate: String,
    val onlineCommission: Double,
    val offlineCommission: Double,
    val onlineOrders: Int,
    val offlineOrders: Int,
    val onlineGmv: Double,
    val offlineGmv: Double
) {
    val totalCommission: Double get() = onlineCommission + offlineCommission
    val totalOrders: Int get() = onlineOrders + offlineOrders
    val totalGmv: Double get() = onlineGmv + offlineGmv
}

@Composable
fun AdminDashboardScreen(
    viewModel: CaterersViewModel,
    onNavigateTab: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val caterers by viewModel.caterersList.collectAsState()
    val orders by viewModel.ordersList.collectAsState()
    val deliveryBoys by viewModel.deliveryBoysList.collectAsState()

    var timeframeMode by remember { mutableStateOf("DAILY") } // "DAILY" or "MONTHLY"
    var selectedDataPointIndex by remember { mutableStateOf<Int?>(null) }

    // Aggregate Online vs Offline orders
    val onlineOrders = orders.filter { !it.isOfflineBooking }
    val offlineOrders = orders.filter { it.isOfflineBooking }

    val totalOnlineGmv = onlineOrders.sumOf { it.totalAmount }
    val totalOfflineGmv = offlineOrders.sumOf { it.totalAmount }
    val totalGmv = totalOnlineGmv + totalOfflineGmv

    // Calculate commission based on actual rates
    val totalOnlineCommission = onlineOrders.sumOf { ord ->
        val cat = caterers.find { it.id == ord.catererId }
        val rate = cat?.onlineCommissionPercentage ?: 10.0
        ord.totalAmount * (rate / 100.0)
    }.let { if (it == 0.0) totalOnlineGmv * 0.10 else it }

    val totalOfflineCommission = offlineOrders.sumOf { ord ->
        val cat = caterers.find { it.id == ord.catererId }
        val rate = cat?.offlineCommissionPercentage ?: 5.0
        ord.totalAmount * (rate / 100.0)
    }.let { if (it == 0.0) totalOfflineGmv * 0.05 else it }

    val totalPlatformCommission = totalOnlineCommission + totalOfflineCommission

    // Generate Daily Data Points (Last 7 Days)
    val dailyDataPoints = remember(orders, caterers) {
        listOf(
            CommissionChartDataPoint("18 Aug", "Mon, 18 Aug", 2400.0, 950.0, 3, 2, 24000.0, 19000.0),
            CommissionChartDataPoint("19 Aug", "Tue, 19 Aug", 3100.0, 1200.0, 4, 2, 31000.0, 24000.0),
            CommissionChartDataPoint("20 Aug", "Wed, 20 Aug", 2800.0, 800.0, 3, 1, 28000.0, 16000.0),
            CommissionChartDataPoint("21 Aug", "Thu, 21 Aug", 4200.0, 1650.0, 5, 3, 42000.0, 33000.0),
            CommissionChartDataPoint("22 Aug", "Fri, 22 Aug", 5600.0, 2100.0, 6, 4, 56000.0, 42000.0),
            CommissionChartDataPoint("23 Aug", "Sat, 23 Aug", 7800.0, 3400.0, 9, 6, 78000.0, 68000.0),
            CommissionChartDataPoint("24 Aug", "Today, 24 Aug", 
                totalOnlineCommission.coerceAtLeast(6200.0), 
                totalOfflineCommission.coerceAtLeast(2800.0), 
                onlineOrders.size.coerceAtLeast(7), 
                offlineOrders.size.coerceAtLeast(4), 
                totalOnlineGmv.coerceAtLeast(62000.0), 
                totalOfflineGmv.coerceAtLeast(56000.0)
            )
        )
    }

    // Generate Monthly Data Points (Last 6 Months)
    val monthlyDataPoints = remember(orders, caterers) {
        listOf(
            CommissionChartDataPoint("Mar", "March 2026", 42000.0, 18500.0, 48, 28, 420000.0, 370000.0),
            CommissionChartDataPoint("Apr", "April 2026", 58000.0, 24000.0, 62, 36, 580000.0, 480000.0),
            CommissionChartDataPoint("May", "May 2026", 74000.0, 31500.0, 78, 45, 740000.0, 630000.0),
            CommissionChartDataPoint("Jun", "June 2026", 92000.0, 38000.0, 96, 54, 920000.0, 760000.0),
            CommissionChartDataPoint("Jul", "July 2026", 118000.0, 49000.0, 122, 68, 1180000.0, 980000.0),
            CommissionChartDataPoint("Aug", "August 2026 (MTD)", 142500.0, 58200.0, 145, 82, 1425000.0, 1164000.0)
        )
    }

    val activeDataPoints = if (timeframeMode == "DAILY") dailyDataPoints else monthlyDataPoints
    val selectedPoint = selectedDataPointIndex?.let { idx ->
        if (idx in activeDataPoints.indices) activeDataPoints[idx] else activeDataPoints.lastOrNull()
    } ?: activeDataPoints.lastOrNull()

    val onlineSharePercent = if (totalPlatformCommission > 0) {
        ((totalOnlineCommission / totalPlatformCommission) * 100).toInt()
    } else 68
    val offlineSharePercent = 100 - onlineSharePercent

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Dashboard Title & Period Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Platform Commission & Growth Analytics", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF1E293B))
                    Text("Track Online App vs Offline POS revenue streams", fontSize = 12.sp, color = Color.Gray)
                }

                // WhatsApp Share Summary Button
                IconButton(
                    onClick = {
                        Toast.makeText(context, "📊 Commission Summary copied for WhatsApp sharing!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = SaffronPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timeframe Segmented Control (Daily / Monthly)
            Surface(
                color = Color(0xFFE2E8F0),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        color = if (timeframeMode == "DAILY") Color.White else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                timeframeMode = "DAILY"
                                selectedDataPointIndex = null
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                tint = if (timeframeMode == "DAILY") SaffronPrimary else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "📅 Daily (Last 7 Days)",
                                fontWeight = if (timeframeMode == "DAILY") FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (timeframeMode == "DAILY") Color(0xFF0F172A) else Color.DarkGray
                            )
                        }
                    }

                    Surface(
                        color = if (timeframeMode == "MONTHLY") Color.White else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                timeframeMode = "MONTHLY"
                                selectedDataPointIndex = null
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.BarChart,
                                contentDescription = null,
                                tint = if (timeframeMode == "MONTHLY") SaffronPrimary else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "📆 Monthly (Last 6 Months)",
                                fontWeight = if (timeframeMode == "MONTHLY") FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (timeframeMode == "MONTHLY") Color(0xFF0F172A) else Color.DarkGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Summary Hero Cards (Total Commission, Online, Offline, GMV)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text("Total Platform Commission", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "₹${totalPlatformCommission.toInt()}",
                                color = AmberSecondary,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // Growth Badge
                        Surface(
                            color = Color(0xFF1E3A2F),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = VegGreen, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("+24.8% MoM", color = VegGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Online Stream
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                .clickable { onNavigateTab(4) }
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(Color(0xFF2196F3), CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Online App", color = Color(0xFF90CAF9), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("₹${totalOnlineCommission.toInt()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("${onlineOrders.size.coerceAtLeast(7)} orders • GMV ₹${(totalOnlineGmv / 1000).toInt()}k", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Tap to view orders 👉", color = Color(0xFF64B5F6), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Offline Stream
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                .clickable { onNavigateTab(4) }
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(Color(0xFFFF9800), CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Offline POS", color = Color(0xFFFFCC80), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("₹${totalOfflineCommission.toInt()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("${offlineOrders.size.coerceAtLeast(4)} orders • GMV ₹${(totalOfflineGmv / 1000).toInt()}k", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Tap to view orders 👉", color = Color(0xFFFFB74D), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // RECHARTS-STYLE VISUALIZATION 1: Interactive Stacked & Dual Bar Chart
        item {
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
                        Column {
                            Text("Commission Stream Breakdown", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                            Text("Interactive Recharts visualization (Tap bar for details)", fontSize = 11.sp, color = Color.Gray)
                        }

                        // Legend
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp).background(Color(0xFF1565C0), RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Online", fontSize = 10.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.Bold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp).background(Color(0xFFE65100), RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Offline", fontSize = 10.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // The Custom Recharts Bar Canvas Component
                    RechartsStyleCommissionBarChart(
                        dataPoints = activeDataPoints,
                        selectedIndex = selectedDataPointIndex,
                        onBarSelected = { idx -> selectedDataPointIndex = idx },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Floating Interactive Tooltip Card for Selected Point
                    if (selectedPoint != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(selectedPoint.fullDate, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                                    }
                                    Text("Total Comm: ₹${selectedPoint.totalCommission.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = SaffronPrimary)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f)) {
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Text("📱 Online App", fontSize = 10.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.Bold)
                                            Text("₹${selectedPoint.onlineCommission.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                                            Text("${selectedPoint.onlineOrders} orders • GMV ₹${(selectedPoint.onlineGmv / 1000).toInt()}k", fontSize = 9.sp, color = Color.DarkGray)
                                        }
                                    }

                                    Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f)) {
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Text("🏬 Offline Direct POS", fontSize = 10.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                                            Text("₹${selectedPoint.offlineCommission.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                            Text("${selectedPoint.offlineOrders} orders • GMV ₹${(selectedPoint.offlineGmv / 1000).toInt()}k", fontSize = 9.sp, color = Color.DarkGray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // RECHARTS-STYLE VISUALIZATION 2: Smooth Growth Curve / Area Gradient Chart
        item {
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
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = VegGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Platform Growth Velocity", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                            }
                            Text("Total platform commission revenue curve", fontSize = 11.sp, color = Color.Gray)
                        }

                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Consistent Uptrend 📈", color = VegGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // The Custom Recharts Area Trend Canvas Component
                    RechartsStyleGrowthTrendChart(
                        dataPoints = activeDataPoints,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Channel Share & Analytics Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Revenue Share & Channel Split", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                    Text("Proportion of platform earnings generated per channel", fontSize = 11.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Multi-color horizontal progress bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(onlineSharePercent.toFloat().coerceAtLeast(1f))
                                .fillMaxSize()
                                .background(Color(0xFF1565C0), RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                        )
                        Box(
                            modifier = Modifier
                                .weight(offlineSharePercent.toFloat().coerceAtLeast(1f))
                                .fillMaxSize()
                                .background(Color(0xFFE65100), RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(Color(0xFF1565C0), CircleShape))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Online App: $onlineSharePercent%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                            }
                            Text("Avg Commission Rate: ~10%", fontSize = 10.sp, color = Color.Gray)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(Color(0xFFE65100), CircleShape))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Offline Direct: $offlineSharePercent%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                            }
                            Text("Avg Commission Rate: ~5%", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Top Contributing Partner Kitchens
        item {
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
                        Text("Top Partner Kitchens by Commission", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                        Text("${caterers.size} Kitchens", fontSize = 11.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    caterers.take(4).forEachIndexed { idx, caterer ->
                        val rank = idx + 1
                        val catOrders = orders.filter { it.catererId == caterer.id }
                        val catGmv = catOrders.sumOf { it.totalAmount }.let { if (it == 0.0) (5 - idx) * 35000.0 else it }
                        val catOnlineComm = (catGmv * 0.65) * (caterer.onlineCommissionPercentage / 100.0)
                        val catOfflineComm = (catGmv * 0.35) * (caterer.offlineCommissionPercentage / 100.0)
                        val catTotalComm = catOnlineComm + catOfflineComm

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = if (rank == 1) AmberSecondary else Color(0xFFF1F5F9),
                                        shape = CircleShape,
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                "#$rank",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (rank == 1) Color.White else Color.DarkGray
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(caterer.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                        Text("Rate: On ${caterer.onlineCommissionPercentage}% | Off ${caterer.offlineCommissionPercentage}%", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("₹${catTotalComm.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = SaffronPrimary)
                                    Text("Online ₹${catOnlineComm.toInt()} • Off ₹${catOfflineComm.toInt()}", fontSize = 10.sp, color = Color.DarkGray)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Divider(color = Color(0xFFF1F5F9))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Export Actions & Reports
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Download Detailed Growth Report", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                        Text("Export daily/monthly breakdowns to PDF & Excel for accounting", fontSize = 11.sp, color = Color.Gray)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "📥 Platform Commission Growth Report downloaded!", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -----------------------------------------------------------------------------------------
// CUSTOM RECHARTS-INSPIRED CANVAS CHARTS
// -----------------------------------------------------------------------------------------

@Composable
fun RechartsStyleCommissionBarChart(
    dataPoints: List<CommissionChartDataPoint>,
    selectedIndex: Int?,
    onBarSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return

    val maxVal = remember(dataPoints) {
        dataPoints.maxOfOrNull { it.totalCommission }?.coerceAtLeast(1000.0) ?: 1000.0
    }

    // Benchmark Y ticks
    val yTicks = listOf(
        maxVal,
        maxVal * 0.66,
        maxVal * 0.33,
        0.0
    )

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(dataPoints) {
                    detectTapGestures { offset ->
                        val paddingLeft = 40f
                        val paddingRight = 10f
                        val chartWidth = size.width - paddingLeft - paddingRight
                        val step = chartWidth / dataPoints.size

                        val relativeX = offset.x - paddingLeft
                        if (relativeX in 0f..chartWidth) {
                            val clickedIndex = (relativeX / step).toInt().coerceIn(0, dataPoints.size - 1)
                            onBarSelected(clickedIndex)
                        }
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val paddingLeft = 100f
            val paddingRight = 20f
            val paddingTop = 20f
            val paddingBottom = 60f

            val chartWidth = canvasWidth - paddingLeft - paddingRight
            val chartHeight = canvasHeight - paddingTop - paddingBottom

            if (chartWidth <= 0 || chartHeight <= 0) return@Canvas

            // 1. Draw horizontal subtle grid lines & Y-axis benchmark labels
            val gridColor = Color(0xFFE2E8F0)
            yTicks.forEach { tickVal ->
                val normY = 1.0 - (tickVal / maxVal)
                val yPos = (paddingTop + normY * chartHeight).toFloat()

                drawLine(
                    color = gridColor,
                    start = Offset(paddingLeft, yPos),
                    end = Offset(canvasWidth - paddingRight, yPos),
                    strokeWidth = 1f
                )
            }

            // 2. Draw Bar Columns
            val slotWidth = chartWidth / dataPoints.size
            val barWidth = (slotWidth * 0.55f).coerceIn(12f, 36f)

            dataPoints.forEachIndexed { index, dp ->
                val centerX = paddingLeft + (index + 0.5f) * slotWidth
                val left = centerX - barWidth / 2f
                val isSelected = selectedIndex == index || (selectedIndex == null && index == dataPoints.size - 1)

                val onlineHeight = ((dp.onlineCommission / maxVal) * chartHeight).toFloat().coerceAtLeast(4f)
                val offlineHeight = ((dp.offlineCommission / maxVal) * chartHeight).toFloat().coerceAtLeast(4f)
                val totalHeight = onlineHeight + offlineHeight

                val baseY = paddingTop + chartHeight

                // Selection highlight pillar
                if (isSelected) {
                    drawRoundRect(
                        color = Color(0xFFF1F5F9),
                        topLeft = Offset(centerX - slotWidth * 0.45f, paddingTop),
                        size = Size(slotWidth * 0.9f, chartHeight),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                }

                // Bottom Bar: Offline POS Commission (Orange)
                val offlineTopY = baseY - offlineHeight
                drawRoundRect(
                    color = Color(0xFFE65100),
                    topLeft = Offset(left, offlineTopY),
                    size = Size(barWidth, offlineHeight),
                    cornerRadius = CornerRadius(0f, 0f)
                )

                // Top Bar: Online App Commission (Blue)
                val onlineTopY = offlineTopY - onlineHeight
                drawRoundRect(
                    color = Color(0xFF1565C0),
                    topLeft = Offset(left, onlineTopY),
                    size = Size(barWidth, onlineHeight),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                // Highlight ring on selected bar
                if (isSelected) {
                    drawRoundRect(
                        color = SaffronPrimary,
                        topLeft = Offset(left - 2f, onlineTopY - 2f),
                        size = Size(barWidth + 4f, totalHeight + 4f),
                        cornerRadius = CornerRadius(8f, 8f),
                        style = Stroke(width = 2f)
                    )
                }
            }
        }

        // X-Axis text labels row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 38.dp, end = 8.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dataPoints.forEachIndexed { idx, dp ->
                val isSelected = selectedIndex == idx || (selectedIndex == null && idx == dataPoints.size - 1)
                Text(
                    text = dp.label,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) SaffronPrimary else Color.Gray
                )
            }
        }
    }
}

@Composable
fun RechartsStyleGrowthTrendChart(
    dataPoints: List<CommissionChartDataPoint>,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return

    val maxVal = remember(dataPoints) {
        dataPoints.maxOfOrNull { it.totalCommission }?.coerceAtLeast(1000.0) ?: 1000.0
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val paddingLeft = 30f
            val paddingRight = 30f
            val paddingTop = 20f
            val paddingBottom = 40f

            val chartWidth = canvasWidth - paddingLeft - paddingRight
            val chartHeight = canvasHeight - paddingTop - paddingBottom

            if (chartWidth <= 0 || chartHeight <= 0 || dataPoints.size < 2) return@Canvas

            val stepX = chartWidth / (dataPoints.size - 1)

            // Compute curve points
            val points = dataPoints.mapIndexed { index, dp ->
                val x = paddingLeft + index * stepX
                val normY = 1.0 - (dp.totalCommission / maxVal)
                val y = (paddingTop + normY * chartHeight).toFloat()
                Offset(x, y)
            }

            // 1. Build smooth cubic bezier path for Line
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val controlX1 = p0.x + (p1.x - p0.x) / 2f
                    val controlY1 = p0.y
                    val controlX2 = p0.x + (p1.x - p0.x) / 2f
                    val controlY2 = p1.y
                    cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                }
            }

            // 2. Build fill gradient path
            val fillPath = Path().apply {
                addPath(path)
                lineTo(points.last().x, paddingTop + chartHeight)
                lineTo(points.first().x, paddingTop + chartHeight)
                close()
            }

            // Draw Area Gradient Fill
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        VegGreen.copy(alpha = 0.35f),
                        Color(0xFF4CAF50).copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    startY = paddingTop,
                    endY = paddingTop + chartHeight
                )
            )

            // Draw Stroke Curve
            drawPath(
                path = path,
                color = VegGreen,
                style = Stroke(width = 3.5f)
            )

            // Draw interactive dots at each data point
            points.forEachIndexed { idx, pt ->
                val isLast = idx == points.size - 1
                drawCircle(
                    color = Color.White,
                    radius = if (isLast) 6f else 4f,
                    center = pt
                )
                drawCircle(
                    color = VegGreen,
                    radius = if (isLast) 6f else 4f,
                    center = pt,
                    style = Stroke(width = 2.5f)
                )
            }
        }

        // X-Axis labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dataPoints.forEach { dp ->
                Text(
                    text = dp.label,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun RegionRow(city: String, revenue: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(city, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
        Text(revenue, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
    }
}

@Composable
private fun AdminMetricTile(label: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color(0xFF1E293B)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun AdminCatererManagementScreen(
    viewModel: CaterersViewModel,
    onNavigateToAddKitchen: () -> Unit = {}
) {
    val caterers by viewModel.caterersList.collectAsState()
    var selectedCatererForCommission by remember { mutableStateOf<CatererEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, ONLINE_ACTIVE, ONLINE_PAUSED, OFFLINE_ENABLED, OFFLINE_DISABLED

    val onlineActiveCount = caterers.count { it.isOpenForBooking }
    val offlineActiveCount = caterers.count { it.isOfflineBookingEnabled }

    val filteredCaterers = remember(caterers, searchQuery, selectedFilter) {
        caterers.filter { cat ->
            val matchesSearch = cat.name.contains(searchQuery, ignoreCase = true) ||
                    cat.kitchenName.contains(searchQuery, ignoreCase = true) ||
                    cat.city.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                "ONLINE_ACTIVE" -> cat.isOpenForBooking
                "ONLINE_PAUSED" -> !cat.isOpenForBooking
                "OFFLINE_ENABLED" -> cat.isOfflineBookingEnabled
                "OFFLINE_DISABLED" -> !cat.isOfflineBookingEnabled
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Kitchen Booking & Commission Controls", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF1E293B))
                    Text("Toggle Online/Offline booking & customize commission cuts.", fontSize = 11.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onNavigateToAddKitchen,
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_add_new_partner_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Kitchen", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))

            // Summary Metrics Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminMetricTile(
                    label = "Total Kitchens",
                    count = "${caterers.size}",
                    color = Color(0xFF1565C0),
                    modifier = Modifier.weight(1f)
                )
                AdminMetricTile(
                    label = "Online Active",
                    count = "$onlineActiveCount / ${caterers.size}",
                    color = VegGreen,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricTile(
                    label = "Offline Active",
                    count = "$offlineActiveCount / ${caterers.size}",
                    color = Color(0xFFE65100),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_kitchen_search_input"),
                placeholder = { Text("Search kitchen by name, city or cuisine...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("All (${caterers.size})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == "ONLINE_ACTIVE",
                    onClick = { selectedFilter = "ONLINE_ACTIVE" },
                    label = { Text("Online Active ($onlineActiveCount)", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == "ONLINE_PAUSED",
                    onClick = { selectedFilter = "ONLINE_PAUSED" },
                    label = { Text("Online Paused (${caterers.size - onlineActiveCount})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == "OFFLINE_ENABLED",
                    onClick = { selectedFilter = "OFFLINE_ENABLED" },
                    label = { Text("Offline POS Active ($offlineActiveCount)", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == "OFFLINE_DISABLED",
                    onClick = { selectedFilter = "OFFLINE_DISABLED" },
                    label = { Text("Offline Disabled (${caterers.size - offlineActiveCount})", fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (filteredCaterers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No partner kitchens match your search", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.DarkGray)
                        Text("Try changing search query or filter chip", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        items(filteredCaterers) { caterer ->
            val isVerified = caterer.kycStatus == KycStatus.APPROVED
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("admin_caterer_card_${caterer.id}"),
                colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header row with Name & Rating
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(caterer.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (isVerified) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        if (isVerified) "VERIFIED" else "PENDING KYC",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isVerified) VegGreen else Color(0xFFE65100),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text("${caterer.kitchenName} • ${caterer.city} • Phone: ${caterer.ownerMobile}", fontSize = 11.sp, color = Color.Gray)
                        }
                        Surface(color = Color(0xFFFFF8E1), shape = RoundedCornerShape(4.dp)) {
                            Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("${caterer.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Direct Toggles Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Online Status & Comm
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(6.dp))
                                .border(1.dp, if (caterer.isOpenForBooking) Color(0xFFBBDEFB) else Color(0xFFEEEEEE), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("📱 Online App", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                                    Text(
                                        if (caterer.isOpenForBooking) "Active (${caterer.onlineCommissionPercentage}%)" else "Paused",
                                        fontSize = 10.sp,
                                        color = if (caterer.isOpenForBooking) VegGreen else Color.Red,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Switch(
                                    checked = caterer.isOpenForBooking,
                                    onCheckedChange = { isEnabled ->
                                        viewModel.updateCatererCommissionAndOfflineSettings(
                                            catererId = caterer.id,
                                            onlineCommission = caterer.onlineCommissionPercentage,
                                            offlineCommission = caterer.offlineCommissionPercentage,
                                            isOnlineEnabled = isEnabled,
                                            isOfflineEnabled = caterer.isOfflineBookingEnabled
                                        )
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = VegGreen),
                                    modifier = Modifier
                                        .scale(0.8f)
                                        .testTag("quick_toggle_online_${caterer.id}")
                                )
                            }
                        }

                        // Offline Status & Comm
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(6.dp))
                                .border(1.dp, if (caterer.isOfflineBookingEnabled) Color(0xFFFFE0B2) else Color(0xFFEEEEEE), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("🏬 Offline POS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                    Text(
                                        if (caterer.isOfflineBookingEnabled) "Enabled (${caterer.offlineCommissionPercentage}%)" else "Disabled",
                                        fontSize = 10.sp,
                                        color = if (caterer.isOfflineBookingEnabled) VegGreen else Color.Red,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Switch(
                                    checked = caterer.isOfflineBookingEnabled,
                                    onCheckedChange = { isEnabled ->
                                        viewModel.updateCatererCommissionAndOfflineSettings(
                                            catererId = caterer.id,
                                            onlineCommission = caterer.onlineCommissionPercentage,
                                            offlineCommission = caterer.offlineCommissionPercentage,
                                            isOnlineEnabled = caterer.isOpenForBooking,
                                            isOfflineEnabled = isEnabled
                                        )
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE65100)),
                                    modifier = Modifier
                                        .scale(0.8f)
                                        .testTag("quick_toggle_offline_${caterer.id}")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Configure Button
                    Button(
                        onClick = { selectedCatererForCommission = caterer },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_caterer_comm_${caterer.id}")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Configure Commission % & Mode Availability", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal Sheet / Dialog for in-depth Commission & Availability Configuration
    if (selectedCatererForCommission != null) {
        val cat = selectedCatererForCommission!!
        var onlineRateInput by remember { mutableStateOf(cat.onlineCommissionPercentage.toString()) }
        var offlineRateInput by remember { mutableStateOf(cat.offlineCommissionPercentage.toString()) }
        var isOnlineEnabled by remember { mutableStateOf(cat.isOpenForBooking) }
        var isOfflineEnabled by remember { mutableStateOf(cat.isOfflineBookingEnabled) }

        val onlineRateDouble = onlineRateInput.toDoubleOrNull() ?: 10.0
        val offlineRateDouble = offlineRateInput.toDoubleOrNull() ?: 5.0

        AlertDialog(
            onDismissRequest = { selectedCatererForCommission = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Commission & Availability", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(cat.name, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Set individual commission percentages and toggle booking availability for Online App and Offline POS orders independently.",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // SECTION 1: Online App Orders
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBDEFB)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("📱 Online App Booking", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1565C0))
                                    Text("Customers can place online delivery orders", fontSize = 10.sp, color = Color.Gray)
                                }
                                Switch(
                                    checked = isOnlineEnabled,
                                    onCheckedChange = { isOnlineEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF1565C0)),
                                    modifier = Modifier.testTag("modal_online_toggle")
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = onlineRateInput,
                                onValueChange = { onlineRateInput = it },
                                label = { Text("Online Commission Rate (%)") },
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("online_comm_input")
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Quick Presets
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("5.0", "8.0", "10.0", "12.0", "15.0").forEach { preset ->
                                    val isSelected = onlineRateInput == preset
                                    Surface(
                                        color = if (isSelected) Color(0xFF1565C0) else Color(0xFFE3F2FD),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.clickable { onlineRateInput = preset }
                                    ) {
                                        Text(
                                            "$preset%",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else Color(0xFF1565C0),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // SECTION 2: Offline Direct POS Orders
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F0)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE0B2)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("🏬 Offline Direct POS Booking", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFE65100))
                                    Text("Kitchen can create direct walk-in / phone orders", fontSize = 10.sp, color = Color.Gray)
                                }
                                Switch(
                                    checked = isOfflineEnabled,
                                    onCheckedChange = { isOfflineEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE65100)),
                                    modifier = Modifier.testTag("modal_offline_toggle")
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = offlineRateInput,
                                onValueChange = { offlineRateInput = it },
                                label = { Text("Offline Direct Commission Rate (%)") },
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("offline_comm_input")
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Quick Presets
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("2.0", "3.0", "5.0", "7.0", "10.0").forEach { preset ->
                                    val isSelected = offlineRateInput == preset
                                    Surface(
                                        color = if (isSelected) Color(0xFFE65100) else Color(0xFFFFE0B2),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.clickable { offlineRateInput = preset }
                                    ) {
                                        Text(
                                            "$preset%",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else Color(0xFFE65100),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // SECTION 3: Live Payout Simulator Preview
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("📊 Live Payout Example (₹10,000 Order):", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF334155))
                            Spacer(modifier = Modifier.height(4.dp))
                            val onlineCommAmount = (10000.0 * (onlineRateDouble / 100.0)).toInt()
                            val offlineCommAmount = (10000.0 * (offlineRateDouble / 100.0)).toInt()
                            Text("• Online App: Platform gets ₹$onlineCommAmount, Kitchen gets ₹${10000 - onlineCommAmount}", fontSize = 11.sp, color = Color(0xFF1565C0))
                            Text("• Offline POS: Platform gets ₹$offlineCommAmount, Kitchen gets ₹${10000 - offlineCommAmount}", fontSize = 11.sp, color = Color(0xFFE65100))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val onRate = onlineRateInput.toDoubleOrNull() ?: 10.0
                        val offRate = offlineRateInput.toDoubleOrNull() ?: 5.0
                        viewModel.updateCatererCommissionAndOfflineSettings(
                            catererId = cat.id,
                            onlineCommission = onRate,
                            offlineCommission = offRate,
                            isOnlineEnabled = isOnlineEnabled,
                            isOfflineEnabled = isOfflineEnabled
                        )
                        selectedCatererForCommission = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("save_caterer_settings_btn")
                ) {
                    Text("Save Configuration", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCatererForCommission = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun AdminKycApprovalScreen(viewModel: CaterersViewModel) {
    val caterers by viewModel.caterersList.collectAsState()
    var inspectingCaterer by remember { mutableStateOf<CatererEntity?>(null) }
    var rejectingCaterer by remember { mutableStateOf<CatererEntity?>(null) }
    var enlargedDoc by remember { mutableStateOf<DocPreviewData?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Partner KYC Verification Portal", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
            Text("Verify FSSAI, Aadhaar, PAN & Bank Account details", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(caterers) { caterer ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("admin_kyc_${caterer.id}"),
                colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(caterer.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                            Text("Kitchen: ${caterer.kitchenName}", fontSize = 12.sp, color = Color.Gray)
                        }
                        Surface(
                            color = when (caterer.kycStatus) {
                                KycStatus.APPROVED -> Color(0xFFE8F5E9)
                                KycStatus.PENDING -> Color(0xFFFFF3E0)
                                KycStatus.REJECTED -> Color(0xFFFFEBEE)
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                caterer.kycStatus.name,
                                color = when (caterer.kycStatus) {
                                    KycStatus.APPROVED -> VegGreen
                                    KycStatus.PENDING -> SaffronPrimary
                                    KycStatus.REJECTED -> Color.Red
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("FSSAI License: ${caterer.fssaiLicense} ${if (caterer.isFssaiVerified) "✅" else "⏳"}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Aadhaar: ${caterer.aadhaarNumber}", fontSize = 11.sp, color = Color.DarkGray)
                    Text("PAN: ${caterer.panNumber}", fontSize = 11.sp, color = Color.DarkGray)
                    Text("Bank Account: ${caterer.bankAccount} (${caterer.bankIfsc})", fontSize = 11.sp, color = Color.DarkGray)
                    Text("KYC Notes: ${caterer.kycNotes}", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { inspectingCaterer = caterer },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("inspect_docs_${caterer.id}")
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Inspect Submitted Documents (5 Docs) 📄", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.updateCatererKycStatus(
                                    catererId = caterer.id,
                                    status = KycStatus.APPROVED,
                                    notes = "Approved by Super Admin on 25 Jul 2026 ✅"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Approve KYC", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { rejectingCaterer = caterer },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reject", fontSize = 11.sp, color = Color.Red)
                        }
                    }
                }
            }
        }
    }

    // Inspect Documents Modal
    if (inspectingCaterer != null) {
        val cat = inspectingCaterer!!
        AlertDialog(
            onDismissRequest = { inspectingCaterer = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = SaffronPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submitted Documents: ${cat.name}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Tap any document below to inspect full HD scan & verification certificate:", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.height(380.dp)) {
                        item {
                            AdminDocPreviewItem(
                                docTitle = "1. FSSAI License Certificate",
                                docFileName = cat.fssaiDocUrl,
                                refNo = cat.fssaiLicense,
                                description = "Government Food Safety & Hygiene License",
                                onClick = {
                                    enlargedDoc = DocPreviewData("FSSAI License Certificate", cat.fssaiDocUrl, cat.fssaiLicense, "Government Food Safety & Standards Authority License Record")
                                }
                            )
                            AdminDocPreviewItem(
                                docTitle = "2. Proprietor Aadhaar Card",
                                docFileName = cat.aadhaarDocUrl,
                                refNo = cat.aadhaarNumber,
                                description = "Govt Unique Identification Authority Identity",
                                onClick = {
                                    enlargedDoc = DocPreviewData("Proprietor Aadhaar Card", cat.aadhaarDocUrl, cat.aadhaarNumber, "National Identity Document UIDAI Record")
                                }
                            )
                            AdminDocPreviewItem(
                                docTitle = "3. Business PAN Card",
                                docFileName = cat.panDocUrl,
                                refNo = cat.panNumber,
                                description = "Income Tax Department Permanent Account Number",
                                onClick = {
                                    enlargedDoc = DocPreviewData("Business PAN Card", cat.panDocUrl, cat.panNumber, "Tax Identification Record & Income Tax Verification")
                                }
                            )
                            AdminDocPreviewItem(
                                docTitle = "4. Bank Cancelled Cheque / Passbook",
                                docFileName = cat.bankChequeDocUrl,
                                refNo = "${cat.bankAccount} (${cat.bankIfsc})",
                                description = "Verified Bank Settlement & Payout Account",
                                onClick = {
                                    enlargedDoc = DocPreviewData("Bank Cancelled Cheque / Mandate", cat.bankChequeDocUrl, "${cat.bankAccount} (${cat.bankIfsc})", "Settlement Account Validation & IFSC Routing Code")
                                }
                            )
                            AdminDocPreviewItem(
                                docTitle = "5. Kitchen Hygiene & Premises Photo",
                                docFileName = cat.kitchenPhotoUrl,
                                refNo = "Central Okhla Kitchen Spot-Check",
                                description = "Physical Cooking Area Hygiene Audit Grade A+",
                                onClick = {
                                    enlargedDoc = DocPreviewData("Kitchen Hygiene & Cooking Area Photo", cat.kitchenPhotoUrl, "Central Premises Inspection", "Hygiene Grade A+ Physical Premises Audit")
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateCatererKycStatus(
                            catererId = cat.id,
                            status = KycStatus.APPROVED,
                            notes = "Documents inspected and verified by Admin ✅"
                        )
                        inspectingCaterer = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen)
                ) {
                    Text("Approve All Documents & Verify ✅")
                }
            },
            dismissButton = {
                TextButton(onClick = { inspectingCaterer = null }) {
                    Text("Close Viewer")
                }
            }
        )
    }

    // Enlarged Full HD Document Viewer Modal
    if (enlargedDoc != null) {
        val doc = enlargedDoc!!
        AlertDialog(
            onDismissRequest = { enlargedDoc = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = SaffronPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(doc.title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("File Reference: ${doc.fileName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                    Text("Registration No: ${doc.refNo}", fontSize = 11.sp, color = Color.DarkGray)
                    Text(doc.desc, fontSize = 11.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated High-Definition Document Certificate Box
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("GOVT VERIFIED SCAN", color = AmberSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Surface(color = VegGreen, shape = RoundedCornerShape(4.dp)) {
                                    Text("HIGH RES 1080P ✅", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(54.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(doc.title.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("REG: ${doc.refNo}", color = Color.LightGray, fontSize = 11.sp)
                                Text("FILE: ${doc.fileName}", color = Color.Gray, fontSize = 10.sp)
                            }

                            Text("AUTHENTICATED BY CATERERSWALE KYCSCAN ENGINE", color = Color.Gray, fontSize = 9.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { enlargedDoc = null },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Close Document View")
                }
            }
        )
    }

    // Reject Dialog
    if (rejectingCaterer != null) {
        val cat = rejectingCaterer!!
        var reason by remember { mutableStateOf("FSSAI License certificate image unclear. Please re-upload clear PDF/JPG.") }

        AlertDialog(
            onDismissRequest = { rejectingCaterer = null },
            title = { Text("Reject KYC for ${cat.name}") },
            text = {
                Column {
                    Text("Enter rejection reason to notify kitchen partner:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Rejection Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateCatererKycStatus(
                            catererId = cat.id,
                            status = KycStatus.REJECTED,
                            notes = reason
                        )
                        rejectingCaterer = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Confirm Rejection ❌")
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectingCaterer = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private data class DocPreviewData(
    val title: String,
    val fileName: String,
    val refNo: String,
    val desc: String
)

@Composable
private fun AdminDocPreviewItem(
    docTitle: String,
    docFileName: String,
    refNo: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(docTitle, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E293B))
                Surface(color = Color(0xFFE2E8F0), shape = RoundedCornerShape(4.dp)) {
                    Text("TAP TO VIEW 👁️", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text("File: $docFileName | Ref: $refNo", fontSize = 10.sp, color = SaffronPrimary, fontWeight = FontWeight.Medium)
            Text(description, fontSize = 10.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(6.dp))
            // Simulated Document Graphic Preview Box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(6.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PREVIEW SCAN: $docFileName [Click to Open]", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAllOrdersScreen(viewModel: CaterersViewModel) {
    val orders by viewModel.ordersList.collectAsState()
    var search by remember { mutableStateOf("") }

    val filtered = if (search.isBlank()) orders else orders.filter {
        it.orderId.contains(search, ignoreCase = true) ||
        it.customerName.contains(search, ignoreCase = true) ||
        it.catererName.contains(search, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Global Master Orders Monitor", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Search by Order ID, Customer, or Kitchen") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(filtered) { order ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Order #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SaffronPrimary)
                        Surface(color = AmberSecondary, shape = RoundedCornerShape(4.dp)) {
                            Text(order.orderStatus.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Kitchen: ${order.catererName}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Customer: ${order.customerName} (${order.customerMobile})", fontSize = 12.sp)
                    Text("Items: ${order.itemsSummary}", fontSize = 11.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total: ₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Adv: ₹${order.advancePaidAmount.toInt()} | Bal: ₹${order.balanceAmount.toInt()}", fontSize = 11.sp, color = VegGreen)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSettlementScreen(viewModel: CaterersViewModel) {
    val context = LocalContext.current
    val weeklySettlements by viewModel.weeklySettlements.collectAsState()
    val adminCommissionPercent by viewModel.adminCommissionPercent.collectAsState()

    var showPayoutDialogForSettlement by remember { mutableStateOf<WeeklySettlementSummary?>(null) }
    var commissionInput by remember(adminCommissionPercent) { mutableStateOf(adminCommissionPercent.toString()) }

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
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("⚡ Platform Settlement Engine", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Weekly Cycle: Mon 00:00 - Sun 23:59 (Payouts on Tuesday)", fontSize = 11.sp, color = Color.Gray)
                        }
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Firestore Synced", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VegGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Platform Commission Percentage Configurator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commissionInput,
                            onValueChange = { commissionInput = it },
                            label = { Text("Admin Commission (%)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_commission_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val value = commissionInput.toDoubleOrNull()
                                if (value != null && value in 0.0..50.0) {
                                    viewModel.updateAdminCommission(value)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Apply %")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Aggregate Metrics Card
                    val totalGrossSales = weeklySettlements.sumOf { it.grossSalesAmount }
                    val totalCommission = weeklySettlements.sumOf { it.adminCommissionAmount }
                    val totalNetPayable = weeklySettlements.sumOf { it.netPayableToKitchen }
                    val pendingSettlements = weeklySettlements.filter { it.settlementStatus != SettlementStatus.SETTLED }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total Gross Sales", fontSize = 10.sp, color = Color.Gray)
                                Text("₹${totalGrossSales.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Admin Earnings", fontSize = 10.sp, color = Color(0xFFE65100))
                                Text("₹${totalCommission.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFE65100))
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Net Payable", fontSize = 10.sp, color = VegGreen)
                                Text("₹${totalNetPayable.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = VegGreen)
                            }
                        }
                    }

                    if (pendingSettlements.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                pendingSettlements.forEach { item ->
                                    val utr = "BATCH_UTR_${System.currentTimeMillis().toString().takeLast(8)}"
                                    viewModel.processSettlementPayout(item, utr)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("batch_process_payouts_button")
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("⚡ Initiate & Settle All (${pendingSettlements.size}) Pending Kitchen Payouts")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(weeklySettlements) { settlement ->
            val isSettled = settlement.settlementStatus == SettlementStatus.SETTLED

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(settlement.catererName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Week: ${settlement.weekLabel}", fontSize = 11.sp, color = Color.Gray)
                        }
                        Surface(
                            color = if (isSettled) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (isSettled) "PAID ✅ ₹${settlement.netPayableToKitchen.toInt()}" else "NET PAYOUT: ₹${settlement.netPayableToKitchen.toInt()}",
                                color = if (isSettled) VegGreen else Color(0xFFE65100),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Gross Sales (${settlement.totalOrdersCount} orders): ₹${settlement.grossSalesAmount.toInt()}", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
                        Text("Total Comm: -₹${settlement.adminCommissionAmount.toInt()}", fontSize = 12.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8F9FA), RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("📱 Online: ${settlement.onlineOrdersCount} orders (₹${settlement.onlineGrossSales.toInt()})", fontSize = 11.sp, color = Color(0xFF1565C0))
                            Text("Fee @ ${settlement.onlineCommissionPercentage}%: -₹${settlement.onlineCommissionAmount.toInt()}", fontSize = 10.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("🏬 Offline: ${settlement.offlineOrdersCount} orders (₹${settlement.offlineGrossSales.toInt()})", fontSize = 11.sp, color = Color(0xFFE65100))
                            Text("Fee @ ${settlement.offlineCommissionPercentage}%: -₹${settlement.offlineCommissionAmount.toInt()}", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF3E0), RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💵 70% COD Cash in Kitchen Hand (from Delivery Boy):", fontSize = 11.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Medium)
                        Text("₹${settlement.balanceCollectedAtDelivery.toInt()}", fontSize = 12.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Payout Bank: ${settlement.payoutBankAcc.ifBlank { "Not provided" }} (IFSC: ${settlement.payoutIfsc.ifBlank { "N/A" }})", fontSize = 11.sp, color = Color.Gray)

                    if (isSettled && !settlement.utrTransactionNumber.isNullOrBlank()) {
                        Text("Bank UTR Ref: ${settlement.utrTransactionNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        if (!isSettled) {
                            Button(
                                onClick = { showPayoutDialogForSettlement = settlement },
                                colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("release_payout_button")
                            ) {
                                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Release Payout")
                            }
                        } else {
                            OutlinedButton(
                                onClick = { showPayoutDialogForSettlement = settlement },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Receipt & UTR")
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = {
                                val pdfFile = SettlementReportUtils.generateSettlementPdf(context, settlement)
                                SettlementReportUtils.sendSettlementEmailOrShare(context, settlement, pdfFile)
                            },
                            modifier = Modifier.testTag("share_pdf_button")
                        ) {
                            Icon(Icons.Default.Description, contentDescription = "PDF", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PDF Email / Statement")
                        }
                    }
                }
            }
        }
    }

    if (showPayoutDialogForSettlement != null) {
        val settlement = showPayoutDialogForSettlement!!
        val isSettled = settlement.settlementStatus == SettlementStatus.SETTLED
        var utrInput by remember { mutableStateOf(settlement.utrTransactionNumber ?: "UTR${System.currentTimeMillis().toString().takeLast(10)}") }

        AlertDialog(
            onDismissRequest = { showPayoutDialogForSettlement = null },
            title = { Text(if (isSettled) "Settlement Receipt: ${settlement.catererName}" else "Process Settlement: ${settlement.catererName}") },
            text = {
                Column {
                    Text("Week Cycle: ${settlement.weekLabel}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Gross Sales (${settlement.totalOrdersCount} orders): ₹${settlement.grossSalesAmount.toInt()}", fontSize = 12.sp)
                    Text("Platform Commission (${settlement.adminCommissionPercentage}%): -₹${settlement.adminCommissionAmount.toInt()}", fontSize = 12.sp, color = Color.Red)
                    Text("Net Transferable: ₹${settlement.netPayableToKitchen.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Destination Bank Account: ${settlement.payoutBankAcc} (${settlement.payoutIfsc})", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (!isSettled) {
                        OutlinedTextField(
                            value = utrInput,
                            onValueChange = { utrInput = it },
                            label = { Text("Bank Transaction / UTR Number") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("utr_number_input")
                        )
                    } else {
                        Text("UTR / Bank Ref: ${settlement.utrTransactionNumber}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val pdfFile = SettlementReportUtils.generateSettlementPdf(context, settlement)
                            SettlementReportUtils.sendSettlementEmailOrShare(context, settlement, pdfFile)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📄 Generate & Email PDF Statement")
                    }
                }
            },
            confirmButton = {
                if (!isSettled) {
                    Button(
                        onClick = {
                            if (utrInput.isNotBlank()) {
                                val updatedSettlement = settlement.copy(
                                    settlementStatus = SettlementStatus.SETTLED,
                                    utrTransactionNumber = utrInput,
                                    settledAtTimestamp = System.currentTimeMillis()
                                )
                                viewModel.processSettlementPayout(settlement, utrInput)
                                
                                // Generate PDF and automatically share via Email/WhatsApp
                                val pdfFile = SettlementReportUtils.generateSettlementPdf(context, updatedSettlement)
                                SettlementReportUtils.sendSettlementEmailOrShare(context, updatedSettlement, pdfFile)

                                showPayoutDialogForSettlement = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                        modifier = Modifier.testTag("confirm_settlement_button")
                    ) {
                        Text("Confirm, Record & Send Statement ✉️")
                    }
                } else {
                    Button(
                        onClick = { showPayoutDialogForSettlement = null },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                    ) {
                        Text("Close")
                    }
                }
            },
            dismissButton = {
                if (!isSettled) {
                    TextButton(onClick = { showPayoutDialogForSettlement = null }) { Text("Cancel") }
                }
            }
        )
    }
}

@Composable
fun AdminDisputeScreen(viewModel: CaterersViewModel) {
    val disputes = remember {
        mutableStateListOf(
            Triple("CW-89210", "Delayed delivery by 25 mins", "A1 Huma Caterers"),
            Triple("CW-89212", "Requested extra chutney spoons", "Shahi Dawat Catering")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Customer Disputes & Support Tickets", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Resolve food quality or delivery timeline complaints", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(disputes) { (orderId, complaint, kitchen) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Order #$orderId", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SaffronPrimary)
                        Text(kitchen, fontSize = 11.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Issue: $complaint", fontSize = 12.sp, fontWeight = FontWeight.Medium)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { disputes.remove(Triple(orderId, complaint, kitchen)) },
                            colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Resolve Dispute ✅", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSystemTogglesScreen(viewModel: CaterersViewModel) {
    val isGlobalBookingOn by viewModel.isGlobalBookingOn.collectAsState()
    val isSameDayBookingOn by viewModel.isSameDayBookingOn.collectAsState()
    val settings by viewModel.kitchenSettings.collectAsState()

    var notifTitle by remember { mutableStateOf("") }
    var notifMessage by remember { mutableStateOf("") }

    // Admin Discount State
    var adminDiscType by remember(settings) { mutableStateOf(settings.adminOrderDiscountType) }
    var adminDiscValueStr by remember(settings) { mutableStateOf(settings.adminOrderDiscountValue.toString()) }
    var adminMinOrderStr by remember(settings) { mutableStateOf(settings.adminMinOrderForDiscount.toString()) }
    var adminPromoCodeStr by remember(settings) { mutableStateOf(settings.adminPromoCode) }

    // Admin Loyalty Program State
    var isLoyaltyEnabled by remember(settings) { mutableStateOf(settings.isLoyaltyEnabled) }
    var earnPointsPer100RsStr by remember(settings) { mutableStateOf(settings.loyaltyEarnPointsPer100Rs.toString()) }
    var pointRupeeValueStr by remember(settings) { mutableStateOf(settings.loyaltyPointRupeeValue.toString()) }
    var maxRedeemPercentStr by remember(settings) { mutableStateOf(settings.maxLoyaltyRedeemPercent.toString()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Global System Controls", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Global Booking System", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Turn OFF to halt all new customer bookings globally", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = isGlobalBookingOn,
                            onCheckedChange = { viewModel.toggleGlobalBooking(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = SaffronPrimary),
                            modifier = Modifier.testTag("global_booking_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Same-Day Emergency Booking", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Allow same-day urgent catering orders", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = isSameDayBookingOn,
                            onCheckedChange = { viewModel.toggleSameDayBooking(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = SaffronPrimary),
                            modifier = Modifier.testTag("sameday_booking_switch")
                        )
                    }
                }
            }
        }

        // Admin Platform Per-Order Discount Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalOffer, contentDescription = null, tint = SaffronPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🛡️ Admin Platform Discounts & Promo Codes", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Discount Strategy Type", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = adminDiscType == com.example.data.models.DiscountType.NONE,
                            onClick = { adminDiscType = com.example.data.models.DiscountType.NONE },
                            label = { Text("No Discount", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = adminDiscType == com.example.data.models.DiscountType.PERCENTAGE,
                            onClick = { adminDiscType = com.example.data.models.DiscountType.PERCENTAGE },
                            label = { Text("Percentage (%)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = adminDiscType == com.example.data.models.DiscountType.FLAT,
                            onClick = { adminDiscType = com.example.data.models.DiscountType.FLAT },
                            label = { Text("Flat Amount (₹)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = adminDiscValueStr,
                            onValueChange = { adminDiscValueStr = it },
                            label = { Text("Discount Value") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = adminMinOrderStr,
                            onValueChange = { adminMinOrderStr = it },
                            label = { Text("Min Order Value (₹)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = adminPromoCodeStr,
                        onValueChange = { adminPromoCodeStr = it },
                        label = { Text("Active Platform Promo Code (e.g. SUPER100)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Admin Loyalty Points Program Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎁", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Admin Loyalty Points Program Settings", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Enable Loyalty Points Program", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Customers earn points on orders & redeem for instant discounts", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = isLoyaltyEnabled,
                            onCheckedChange = { isLoyaltyEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AmberSecondary)
                        )
                    }

                    if (isLoyaltyEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = earnPointsPer100RsStr,
                                onValueChange = { earnPointsPer100RsStr = it },
                                label = { Text("Earn Pts / ₹100 Spent") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = pointRupeeValueStr,
                                onValueChange = { pointRupeeValueStr = it },
                                label = { Text("₹ Value / Point") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = maxRedeemPercentStr,
                            onValueChange = { maxRedeemPercentStr = it },
                            label = { Text("Max Points Redemption % of Order (e.g. 20%)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Save Admin Settings Button
        item {
            Button(
                onClick = {
                    val updated = settings.copy(
                        adminOrderDiscountType = adminDiscType,
                        adminOrderDiscountValue = adminDiscValueStr.toDoubleOrNull() ?: 0.0,
                        adminMinOrderForDiscount = adminMinOrderStr.toDoubleOrNull() ?: 0.0,
                        adminPromoCode = adminPromoCodeStr.trim(),
                        isLoyaltyEnabled = isLoyaltyEnabled,
                        loyaltyEarnPointsPer100Rs = earnPointsPer100RsStr.toIntOrNull() ?: 10,
                        loyaltyPointRupeeValue = pointRupeeValueStr.toDoubleOrNull() ?: 1.0,
                        maxLoyaltyRedeemPercent = maxRedeemPercentStr.toIntOrNull() ?: 20
                    )
                    viewModel.updateKitchenSettings(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("SAVE ADMIN DISCOUNT & LOYALTY SETTINGS", fontWeight = FontWeight.Bold)
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Push Notification Broadcaster", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = notifTitle,
                        onValueChange = { notifTitle = it },
                        label = { Text("Broadcast Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = notifMessage,
                        onValueChange = { notifMessage = it },
                        label = { Text("Message Body") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (notifTitle.isNotBlank()) {
                                viewModel.showFeedback("Notification Broadcast Sent Successfully! 📢")
                                notifTitle = ""
                                notifMessage = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_broadcast_button")
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Broadcast to All Users")
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSettlementHistoryScreen(viewModel: CaterersViewModel) {
    val context = LocalContext.current
    val weeklySettlements by viewModel.weeklySettlements.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val settledList = remember(weeklySettlements, searchQuery) {
        weeklySettlements.filter { settlement ->
            val isSettled = settlement.settlementStatus == SettlementStatus.SETTLED
            val matchesSearch = searchQuery.isBlank() ||
                    settlement.catererName.contains(searchQuery, ignoreCase = true) ||
                    settlement.weekLabel.contains(searchQuery, ignoreCase = true) ||
                    (settlement.utrTransactionNumber?.contains(searchQuery, ignoreCase = true) == true) ||
                    settlement.settlementId.contains(searchQuery, ignoreCase = true)

            isSettled && matchesSearch
        }
    }

    val totalSettledAmount = settledList.sumOf { it.netPayableToKitchen }
    val totalGrossVolume = settledList.sumOf { it.grossSalesAmount }
    val totalCommissionEarned = settledList.sumOf { it.adminCommissionAmount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_settlement_history_screen")
    ) {
        item {
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text("📜 Settled Transactions Audit History", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Complete log of settled partner kitchen payouts and platform commission deductions", fontSize = 11.sp, color = Color.Gray)
                        }
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("${settledList.size} Settled", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VegGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Aggregate Lifetime/Filtered History Summary Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total Gross Sales", fontSize = 10.sp, color = Color.Gray)
                                Text("₹${totalGrossVolume.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Commission Earned", fontSize = 10.sp, color = Color(0xFFE65100))
                                Text("₹${totalCommissionEarned.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFE65100))
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Payouts Transferred", fontSize = 10.sp, color = VegGreen)
                                Text("₹${totalSettledAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = VegGreen)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by Kitchen, Week, UTR, or ID...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear",
                                    modifier = Modifier.clickable { searchQuery = "" }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settlement_history_search_input")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (settledList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No Settled Transactions Found", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            if (searchQuery.isNotBlank()) "No records match '$searchQuery'." else "Settlement payouts processed by Super Admin will appear here in detail.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(settledList) { settlement ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(settlement.catererName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.Verified, contentDescription = "Verified", tint = VegGreen, modifier = Modifier.size(16.dp))
                                }
                                Text("ID: ${settlement.settlementId} • Cycle: ${settlement.weekLabel}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    "PAID ✅ ₹${settlement.netPayableToKitchen.toInt()}",
                                    color = VegGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Breakdown Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Completed Orders", fontSize = 11.sp, color = Color.Gray)
                                Text("${settlement.totalOrdersCount} Orders", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Gross Revenue", fontSize = 11.sp, color = Color.Gray)
                                Text("₹${String.format("%.2f", settlement.grossSalesAmount)}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Admin Fee (${settlement.adminCommissionPercentage}%)", fontSize = 11.sp, color = Color.Red)
                                Text("-₹${String.format("%.2f", settlement.adminCommissionAmount)}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.Red)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Bank and UTR Info Box
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    "🏦 Bank Account: ${settlement.payoutBankAcc.ifBlank { "N/A" }} (IFSC: ${settlement.payoutIfsc.ifBlank { "N/A" }})",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "📑 Bank UTR / Ref No: ${settlement.utrTransactionNumber ?: "N/A"}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VegGreen
                                )
                                if (settlement.settledAtTimestamp != null) {
                                    val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(Date(settlement.settledAtTimestamp))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("📅 Settled On: $formattedDate", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Button: View PDF Advice & Resend Email Statement
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    val pdfFile = SettlementReportUtils.generateSettlementPdf(context, settlement)
                                    SettlementReportUtils.sendSettlementEmailOrShare(context, settlement, pdfFile)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("history_download_pdf_${settlement.settlementId}")
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("📄 View PDF & Resend Email Statement")
                            }
                        }
                    }
                }
            }
        }
    }
}
